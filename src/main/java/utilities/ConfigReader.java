package utilities;

public class ConfigReader {

    public static String getLambdaTestUsername() {
        String username = System.getenv("LT_USERNAME");
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("LT_USERNAME environment variable is not set!");
        }
        return username;
    }

    public static String getLambdaTestAccessKey() {
        String accessKey = System.getenv("LT_ACCESS_KEY");
        if (accessKey == null || accessKey.isEmpty()) {
            throw new RuntimeException("LT_ACCESS_KEY environment variable is not set!");
        }
        return accessKey;
    }

    public static String getBrowserName() {
        return "Chrome";
    }

    public static String getBrowserVersion() {
        return "latest";
    }

    public static String getPlatformName() {
        return "Windows 10";
    }

    public static String getBuildName() {
        return "Test Build";
    }

    public static String getTestName() {
        return "Portfolio Test";
    }

    public static boolean isVideoEnabled() {
        return true;
    }

    public static boolean isNetworkEnabled() {
        return true;
    }

    public static boolean isVisualEnabled() {
        return true;
    }

    public static long getImplicitTimeout() {
        return 10;
    }
}