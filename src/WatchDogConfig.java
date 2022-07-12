import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class WatchDogConfig {
    private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/config/watchDogConfig.properties";
    private Logger logger = LogManager.getLogger(this.getClass());
    private String checkInterval;

    public WatchDogConfig() {
        Properties properties = new Properties();

        try (FileReader fileReader = new FileReader(CONFIG_FILE_PATH)){
            properties.load(fileReader);

            checkInterval = properties.getProperty("check_interval");

        } catch (FileNotFoundException e){
            logger.error(" *** 'watchDogConfig.properties' File Not Found ***");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Integer getCheckInterval() {
        return Integer.parseInt(checkInterval);
    }
}
