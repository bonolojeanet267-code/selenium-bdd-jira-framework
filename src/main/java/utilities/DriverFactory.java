package utilities;

import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverFactory {
    // ThreadLocal ensures each thread has its own driver instance
    private static ThreadLocal<RemoteWebDriver> driverThreadLocal = new ThreadLocal<>();

    public static void setDriver(RemoteWebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static RemoteWebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            driverThreadLocal.remove(); // Prevents memory leaks
        }
    }
}
