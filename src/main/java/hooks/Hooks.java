package hooks;

import utilities.ConfigReader;
import utilities.JiraClient;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Hooks {

    public static RemoteWebDriver driver;

    @Before
    public void setUp() throws Exception {
        String username = ConfigReader.getLambdaTestUsername();
        String accessKey = ConfigReader.getLambdaTestAccessKey();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", ConfigReader.getBrowserName());
        capabilities.setCapability("browserVersion", ConfigReader.getBrowserVersion());
        capabilities.setCapability("platformName", ConfigReader.getPlatformName());

        org.openqa.selenium.MutableCapabilities ltOptions = new org.openqa.selenium.MutableCapabilities();
        ltOptions.setCapability("build", ConfigReader.getBuildName());
        ltOptions.setCapability("name", ConfigReader.getTestName());
        ltOptions.setCapability("video", ConfigReader.isVideoEnabled());
        ltOptions.setCapability("network", ConfigReader.isNetworkEnabled());
        ltOptions.setCapability("visual", ConfigReader.isVisualEnabled());

        capabilities.setCapability("LT:Options", ltOptions);

        String gridUrl = "https://" + username + ":" + accessKey + "@hub.lambdatest.com/wd/hub";

        driver = new RemoteWebDriver(new URL(gridUrl), capabilities);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitTimeout()));
        driver.manage().window().maximize();

        System.out.println("✅ Test running on LambdaTest!");
    }

    @After
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            if (scenario.isFailed()) {
                // ✅ Mark failed on LambdaTest
                driver.executeScript("lambda-status=failed");

                // ✅ Auto-create Jira Bug
                autoCreateJiraBug(scenario);

            } else {
                driver.executeScript("lambda-status=passed");
                System.out.println("✅ Scenario passed: " + scenario.getName());
            }

            driver.quit();
        }
    }

    // ====================================================
    // 🐛 AUTO JIRA BUG CREATION ON TEST FAILURE
    // ====================================================
    private void autoCreateJiraBug(Scenario scenario) {
        try {
            System.out.println("🐛 Creating Jira bug for failed scenario...");

            // ✅ Build summary and description
            String summary = "❌ TEST FAILED: " + scenario.getName();
            String description = buildJiraDescription(scenario);

            // ✅ Create the bug in Jira
            String bugKey = JiraClient.createBug(summary, description);

            if (bugKey != null) {
                System.out.println("✅ Jira Bug created: " + bugKey);

                // ✅ Attach screenshot as comment
                attachScreenshotComment(bugKey, scenario);

                // ✅ Add LambdaTest session link as comment
                String lambdaComment = buildLambdaTestComment();
                JiraClient.addComment(bugKey, lambdaComment);

                // ✅ Transition bug to In Progress
                JiraClient.transitionStatus(bugKey, "In Progress");

                System.out.println("🔗 View bug at: https://bonolojeanet267.atlassian.net/browse/" + bugKey);
            } else {
                System.err.println("❌ Failed to create Jira bug.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error during Jira bug creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ====================================================
    // 📋 BUILD JIRA DESCRIPTION WITH FULL SCENARIO DETAILS
    // ====================================================
    private String buildJiraDescription(Scenario scenario) {
        StringBuilder sb = new StringBuilder();

        sb.append("🧪 TEST SCENARIO\n");
        sb.append("================\n");
        sb.append("Scenario: ").append(scenario.getName()).append("\n");
        sb.append("Feature:  ").append(scenario.getUri()).append("\n");
        sb.append("Tags:     ").append(scenario.getSourceTagNames()).append("\n");
        sb.append("Status:   ").append(scenario.getStatus()).append("\n\n");

        sb.append("🌐 TEST ENVIRONMENT\n");
        sb.append("===================\n");
        sb.append("Browser:  ").append(ConfigReader.getBrowserName()).append(" ")
                .append(ConfigReader.getBrowserVersion()).append("\n");
        sb.append("Platform: ").append(ConfigReader.getPlatformName()).append("\n");
        sb.append("Build:    ").append(ConfigReader.getBuildName()).append("\n\n");

        sb.append("🔗 CI/CD INFO\n");
        sb.append("=============\n");
        sb.append("GitHub Run #: ").append(getEnv("GITHUB_RUN_NUMBER", "local")).append("\n");
        sb.append("Branch:       ").append(getEnv("GITHUB_REF_NAME", "local")).append("\n");
        sb.append("Triggered by: ").append(getEnv("GITHUB_ACTOR", "local")).append("\n\n");

        sb.append("🕐 Timestamp: ").append(getCurrentTimestamp()).append("\n");

        return sb.toString();
    }

    // ====================================================
    // 📸 ATTACH SCREENSHOT AS JIRA COMMENT
    // ====================================================
    private void attachScreenshotComment(String bugKey, Scenario scenario) {
        try {
            // Take screenshot
            byte[] screenshotBytes = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);

            // Attach to Cucumber report
            scenario.attach(screenshotBytes, "image/png", "Failure Screenshot");

            // Convert to Base64 and add as Jira comment
            String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);

            JiraClient.addComment(bugKey,
                    "📸 Screenshot captured at moment of failure.\n" +
                            "Base64 preview (paste in browser): data:image/png;base64," +
                            base64Screenshot.substring(0, Math.min(100, base64Screenshot.length())) + "...\n\n" +
                            "Full screenshot attached to Cucumber HTML report."
            );

            System.out.println("📸 Screenshot attached to Jira bug: " + bugKey);

        } catch (Exception e) {
            System.err.println("⚠️ Could not attach screenshot: " + e.getMessage());
        }
    }

    // ====================================================
    // 🔗 BUILD LAMBDATEST SESSION COMMENT
    // ====================================================
    private String buildLambdaTestComment() {
        return "🔗 LAMBDATEST SESSION\n" +
                "=====================\n" +
                "Build:        " + ConfigReader.getBuildName() + "\n" +
                "Test Name:    " + ConfigReader.getTestName() + "\n" +
                "GitHub Run #: " + getEnv("GITHUB_RUN_NUMBER", "local") + "\n" +
                "Branch:       " + getEnv("GITHUB_REF_NAME", "local") + "\n" +
                "Timestamp:    " + getCurrentTimestamp() + "\n\n" +
                "👉 View full video & logs at: https://automation.lambdatest.com/build";
    }

    // ====================================================
    // 🛠 HELPERS
    // ====================================================
    private String getCurrentTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
