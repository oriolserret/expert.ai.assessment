package ai.expert.assessment.main;

import ai.expert.assessment.utils.Globals;
import ai.expert.assessment.utils.PropertiesUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author oserret
 */
public class Launcher {

    private static final Logger LAUNCHER_LOGGER = Logger.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {

        String strFileLogConf = System.getProperty("log4j.configuration");
        PropertyConfigurator.configure(strFileLogConf);

        String strFileProperties = System.getProperty("apicall.properties");
        PropertiesUtils.crearProperties(strFileProperties);

        String mode = Globals.VOID_STRING;

        if (System.getProperty("mode") != null) {
            mode = System.getProperty("mode");
        }

        ApiDemo tester = new ApiDemo(strFileLogConf, strFileProperties);

        boolean initOK;

        switch (mode) {
            case "1":
                LAUNCHER_LOGGER.info("-- 1 --");
                LAUNCHER_LOGGER.info("BEGIN TESTER INIT");
                initOK = tester.init();
                LAUNCHER_LOGGER.info("END TESTER INIT");

                if (initOK) {
                    tester.startWithWrapper();
                }
                break;
        }

    }

}
