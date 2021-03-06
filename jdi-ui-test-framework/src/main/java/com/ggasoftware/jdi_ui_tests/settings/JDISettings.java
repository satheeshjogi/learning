package com.ggasoftware.jdi_ui_tests.settings;

import com.ggasoftware.jdi_ui_tests.apiAccessors.selenium.DriverTypes;
import com.ggasoftware.jdi_ui_tests.apiAccessors.selenium.SeleniumDriverFactory;
import com.ggasoftware.jdi_ui_tests.asserter.IAsserter;
import com.ggasoftware.jdi_ui_tests.logger.ListLogger;
import com.ggasoftware.jdi_ui_tests.logger.Log4JLogger;
import com.ggasoftware.jdi_ui_tests.logger.base.ILogger;
import com.ggasoftware.jdi_ui_tests.asserter.TestNGAsserter;
import com.ggasoftware.jdi_ui_tests.logger.TestNGLogger;
import com.ggasoftware.jdi_ui_tests.testRunner.ITestRunner;
import com.ggasoftware.jdi_ui_tests.testRunner.TestNGRunner;
import com.ggasoftware.jdi_ui_tests.utils.common.PropertyReader;
import org.openqa.selenium.WebDriver;

/**
 * Created by Roman_Iovlev on 6/9/2015.
 */
public class JDISettings {
    public static ILogger logger = new ListLogger(new TestNGLogger(), new Log4JLogger());
    public static IAsserter asserter = new TestNGAsserter(true);
    public static ITestRunner testRunner = new TestNGRunner();
    public static SeleniumDriverFactory driverFactory = new SeleniumDriverFactory();

    public static TimeoutSettings timeouts = new TimeoutSettings();

    public static void useDriver(WebDriver driver) { driverFactory.registerDriver(() -> driver); }
    public static void useDriver(DriverTypes driver)  { driverFactory.registerDriver(driver); }

    public static String jdiSettingsPath = "test.properties";
    public static void initJDIFromProperties() throws Exception {
        PropertyReader.getProperties(jdiSettingsPath);
        PropertyReader.fillAction(driverFactory::registerDriver, "driver");
        PropertyReader.fillAction(driverFactory::setRunType, "run.type");
        PropertyReader.fillAction(p -> domain = p, "domain");
    }
    public static void initJDIFromProperties(String propertyPath) throws Exception {
        jdiSettingsPath = propertyPath;
        initJDIFromProperties();
    }

    public static String domain;
    public static boolean hasDomain() {
        return domain != null && !domain.contains("://");
    }
}
