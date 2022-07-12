import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogManager {
    static Logger logger = null;

    public LogManager() {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/config/watchDogLog.properties");
    }

    public static Logger getLogger(Class<?> clazz) {
        if(logger == null){
            logger = Logger.getLogger(clazz);
        }
        return logger;
    }
}
