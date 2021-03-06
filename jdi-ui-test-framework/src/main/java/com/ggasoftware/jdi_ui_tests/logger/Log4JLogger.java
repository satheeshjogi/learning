package com.ggasoftware.jdi_ui_tests.logger;

import com.ggasoftware.jdi_ui_tests.logger.base.AbstractLogger;
import com.ggasoftware.jdi_ui_tests.logger.enums.LogLevels;
import com.ggasoftware.jdi_ui_tests.logger.enums.BusinessInfoTypes;
import com.ggasoftware.jdi_ui_tests.logger.enums.LogInfoTypes;
import com.ggasoftware.jdi_ui_tests.settings.JDIData;
import com.ggasoftware.jdi_ui_tests.utils.common.Timer;
import org.apache.log4j.Logger;

import static java.lang.String.format;
import static org.apache.log4j.LogManager.getLogger;

/**
 * Created by Roman_Iovlev on 6/9/2015.
 */
public class Log4JLogger extends AbstractLogger {

    @Override
    public void inLog(String message, LogLevels logLevel, LogInfoTypes logInfoType) {
        switch (logLevel) {
            case FATAL: log4j.fatal(String.format("%s: %s", Timer.nowTimeShort(), message)); break;
            case ERROR: log4j.error(String.format("%s: %s", Timer.nowTimeShort(), message)); break;
            case WARNING: log4j.warn(String.format("%s: %s", Timer.nowTimeShort(), message)); break;
            case INFO: log4j.info(String.format("%s: %s", Timer.nowTimeShort(), message)); break;
            case DEBUG: log4j.debug(String.format("%s: %s", Timer.nowTimeShort(), message)); break;
        }
    }
    @Override
    public void inLog(String message, BusinessInfoTypes infoType) {
        log4j.info(String.format("[%s] %s: %s", infoType, Timer.nowTimeShort(), message));
    }

    private Logger log4j = getLogger(JDIData.frameworkName + " logger");

    public Log4JLogger() { super(); }
    public Log4JLogger(LogLevels logLevel) { super(logLevel); }

}
