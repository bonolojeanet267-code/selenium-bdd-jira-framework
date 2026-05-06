package driver;

import utilities.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.HashMap;

public class DriverManager {

    public static WebDriver initDriver() throws Exception {

        String username = System.getenv("LT_USERNAME");
        String accessKey =  System.getenv("LT_ACCESS_KEY");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", ConfigReader.getBrowserName());
        capabilities.setCapability("browserVersion", ConfigReader.getBrowserVersion());

        HashMap<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("platformName", ConfigReader.getPlatformName());
        ltOptions.put("build", ConfigReader.getBuildName());
        ltOptions.put("name", ConfigReader.getTestName());
        ltOptions.put("video", ConfigReader.isVideoEnabled());
        ltOptions.put("network", ConfigReader.isNetworkEnabled());
        ltOptions.put("visual", ConfigReader.isVisualEnabled());

        capabilities.setCapability("LT:Options", ltOptions);

        String gridUrl = "https://" + username + ":" + accessKey + "@hub.lambdatest.com/wd/hub";

        return new RemoteWebDriver(new URL(gridUrl), capabilities);
    }
}

