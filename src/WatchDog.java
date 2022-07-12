import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class WatchDog {
    private static final String ADAPTER_STATE_OK = "OK";
    private static final String ADAPTER_STATE_NOT_OK = "NOK";
    private static final String STATE_FILE_PATH = System.getProperty("user.dir") + "/config/adapterState.properties";
    private static final String PID_CHECK_COMMAND = "tasklist /nh /fo csv /fi \"pid eq [PID]\"";
    private static final String KILL_COMMAND = "taskkill /f /pid [PID]";
    private static final String RUN_ADAPTER_COMMAND = "java -jar AiRISTAAdapter.jar";
//    private static final String RUN_ADAPTER_COMMAND = "start AiRISTAAdapter";
    private static String pid;
    private static String state;

    public static LogManager logManager;
    public static void main(String[] args) {
        logManager = new LogManager();
        Logger logger = LogManager.getLogger(WatchDog.class);

        WatchDogConfig config = new WatchDogConfig();


        // Adapter 파일 경로 지정
        // 로그파일 잘 쌓이는지 확인
        // 테스트 진행


        while(true) {
            logger.info("-- Interval : "+config.getCheckInterval()+" s ------------------------------------");
            logger.info("------------------ Watchdog Check Start ------------------");
            System.out.println("-- Interval : "+config.getCheckInterval()+" s ------------------------------------");
            System.out.println("------------------ Watchdog Check Start ------------------");

            // Read File
            readStateFile();

            boolean processRunningYn = checkPid();
            System.out.println("processRunningYn : " + processRunningYn);
            try {
                // Check Process alive
                if( !processRunningYn ){
                    // Start Adapter!
                    Runtime.getRuntime().exec(RUN_ADAPTER_COMMAND);
                    logger.info("*** Process is not running! Start Adapter ***");
                    System.out.println("*** Process is not running! Start Adapter ***");
                }

                // Check operation status
                if(ADAPTER_STATE_NOT_OK.equals(state)){
                    Runtime.getRuntime().exec(KILL_COMMAND.replace("[PID]", pid));
                    Runtime.getRuntime().exec(RUN_ADAPTER_COMMAND);
                    logger.info("*** Adapter state is NOK ! Restart Adapter ***");
                    System.out.println("*** Adapter state is NOK ! Restart Adapter ***");
                }

                Thread.sleep(config.getCheckInterval());
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                logger.info("------------------ Watchdog Check End ------------------");
                System.out.println("------------------ Watchdog Check End ------------------");
            }
        }
    }

    public static void readStateFile(){
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(STATE_FILE_PATH)){
            properties.load(fileReader);

            pid = properties.getProperty("pid");
            state = properties.getProperty("state");

            System.out.println(pid + " " + state);

        } catch (FileNotFoundException e){
            System.out.println(" *** 'adapterState.properties' File Not Found ***");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String execCmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c " + cmd);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkPid(){
        String cmdResult = execCmd( PID_CHECK_COMMAND.replace("[PID]", pid) );
        System.out.println(cmdResult);
        return cmdResult.matches(".*"+ pid +".*\n$");
    }
}
