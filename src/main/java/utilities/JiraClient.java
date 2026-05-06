package utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JiraClient {

    // ✅ Hardcoded for now - will move to GitHub Secrets later
    private static final String JIRA_BASE_URL = "https://bonolojeanet267.atlassian.net";
    private static final String EMAIL         = "bonolojeanet267@gmail.com";
    private static final String API_TOKEN     = "ATATT3xFfGF08Qaksp_0jAQOKp_QukMG85iRbuvvoFi3nTJh5dic6HghBPUVOUWES254-G4H9TVm7T2Xa1gGFNTceSDFjCZJ70LBjDfGjJn3M6COa27WVrN9isp5WHucHdePtFzAMIr75-JAm_nH_-qIK5q40PyhqNEmuVS_4He_s6uIX3VbdPE=00221625";
    private static final String PROJECT_KEY   = "QA";

    private static String getAuthHeader() {
        String auth = EMAIL + ":" + API_TOKEN;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    // ====================================================
    // ✅ PUBLIC API
    // ====================================================

    public static String createBug(String summary, String description) {
        return createIssue(summary, description, "Bug");
    }

    public static String createTask(String summary, String description) {
        return createIssue(summary, description, "Task");
    }

    public static void addComment(String issueKey, String comment) {
        try {
            String jsonPayload = String.format(
                    "{\"body\":{\"type\":\"doc\",\"version\":1,\"content\":[" +
                            "{\"type\":\"paragraph\",\"content\":[" +
                            "{\"type\":\"text\",\"text\":\"%s\"}]}]}}",
                    escapeJson(comment)
            );

            HttpURLConnection conn = createConnection(
                    JIRA_BASE_URL + "/rest/api/3/issue/" + issueKey + "/comment", "POST"
            );
            sendRequest(conn, jsonPayload);

            int code = conn.getResponseCode();
            if (code == 201) {
                System.out.println("✅ Comment added to " + issueKey);
            } else {
                System.err.println("❌ Failed to add comment. Code: " + code);
            }

        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
        }
    }

    public static void transitionStatus(String issueKey, String transitionName) {
        try {
            // Step 1: Get available transitions
            HttpURLConnection conn = createConnection(
                    JIRA_BASE_URL + "/rest/api/3/issue/" + issueKey + "/transitions", "GET"
            );

            String transitionsJson = readResponse(conn);
            String transitionId = extractTransitionId(transitionsJson, transitionName);

            if (transitionId == null) {
                System.err.println("❌ Transition '" + transitionName + "' not found.");
                System.out.println("Available: " + transitionsJson);
                return;
            }

            // Step 2: Perform the transition
            String payload = String.format("{\"transition\":{\"id\":\"%s\"}}", transitionId);
            HttpURLConnection transConn = createConnection(
                    JIRA_BASE_URL + "/rest/api/3/issue/" + issueKey + "/transitions", "POST"
            );
            sendRequest(transConn, payload);

            int code = transConn.getResponseCode();
            if (code == 204) {
                System.out.println("✅ Transitioned " + issueKey + " → '" + transitionName + "'");
            } else {
                System.err.println("❌ Transition failed. Code: " + code);
            }

        } catch (Exception e) {
            System.err.println("Error transitioning issue: " + e.getMessage());
        }
    }

    public static String getIssue(String issueKey) {
        try {
            HttpURLConnection conn = createConnection(
                    JIRA_BASE_URL + "/rest/api/3/issue/" + issueKey, "GET"
            );

            int code = conn.getResponseCode();
            if (code == 200) {
                System.out.println("✅ Fetched issue: " + issueKey);
                return readResponse(conn);
            } else {
                System.err.println("❌ Failed to fetch issue. Code: " + code);
            }
        } catch (Exception e) {
            System.err.println("Error fetching issue: " + e.getMessage());
        }
        return null;
    }

    // ====================================================
    // ✅ CORE ISSUE CREATION
    // ====================================================
    private static String createIssue(String summary, String description, String issueType) {
        try {
            String jsonPayload = String.format(
                    "{\"fields\":{" +
                            "\"project\":{\"key\":\"%s\"}," +
                            "\"summary\":\"%s\"," +
                            "\"description\":{" +
                            "\"type\":\"doc\"," +
                            "\"version\":1," +
                            "\"content\":[{" +
                            "\"type\":\"paragraph\"," +
                            "\"content\":[{\"type\":\"text\",\"text\":\"%s\"}]" +
                            "}]" +
                            "}," +
                            "\"issuetype\":{\"name\":\"%s\"}" +
                            "}}",
                    PROJECT_KEY,
                    escapeJson(summary),
                    escapeJson(description),
                    issueType
            );

            System.out.println("📤 Creating " + issueType + ": " + summary);

            HttpURLConnection conn = createConnection(
                    JIRA_BASE_URL + "/rest/api/3/issue", "POST"
            );
            sendRequest(conn, jsonPayload);

            int responseCode = conn.getResponseCode();
            System.out.println("📥 Response Code: " + responseCode);

            String responseBody = responseCode >= 200 && responseCode < 300
                    ? readResponse(conn)
                    : readErrorResponse(conn);

            System.out.println("📄 Response Body: " + responseBody);

            if (responseCode == 201) {
                String issueKey = extractIssueKey(responseBody);
                System.out.println("✅ Created " + issueType + ": " + issueKey);
                System.out.println("🔗 View at: " + JIRA_BASE_URL + "/browse/" + issueKey);
                return issueKey;
            } else {
                System.err.println("❌ Failed to create " + issueType + ". Code: " + responseCode);
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error creating Jira issue: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ====================================================
    // ✅ HELPER METHODS
    // ====================================================
    private static HttpURLConnection createConnection(String urlString, String method) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", getAuthHeader());
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        if (method.equals("POST") || method.equals("PUT")) {
            conn.setDoOutput(true);
        }
        return conn;
    }

    private static void sendRequest(HttpURLConnection conn, String payload) throws Exception {
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );
        return readAll(reader);
    }

    private static String readErrorResponse(HttpURLConnection conn) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream())
        );
        return readAll(reader);
    }

    private static String readAll(BufferedReader reader) throws Exception {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        return sb.toString();
    }

    private static String extractIssueKey(String jsonResponse) {
        String searchKey = "\"key\":\"";
        int startIndex = jsonResponse.indexOf(searchKey);
        if (startIndex != -1) {
            startIndex += searchKey.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex != -1) return jsonResponse.substring(startIndex, endIndex);
        }
        return PROJECT_KEY + "-UNKNOWN";
    }

    private static String extractTransitionId(String json, String transitionName) {
        String namePattern = "\"name\":\"" + transitionName + "\"";
        int nameIdx = json.indexOf(namePattern);
        if (nameIdx == -1) return null;

        String before = json.substring(0, nameIdx);
        int idIdx = before.lastIndexOf("\"id\":\"");
        if (idIdx == -1) return null;
        int start = idIdx + 6;
        int end = before.indexOf("\"", start);
        return before.substring(start, end);
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ====================================================
    // ✅ LOCAL TEST
    // ====================================================
    public static void main(String[] args) {
        System.out.println("🚀 Testing Jira Connection...");

        String issueKey = createTask(
                "Test from Selenium Framework",
                "This is a test issue created from Java. If you see this in Jira, the integration works!"
        );

        if (issueKey != null) {
            System.out.println("\n🎉 Success! Issue: " + issueKey);
            addComment(issueKey, "Auto-comment from Selenium framework ✅");
            transitionStatus(issueKey, "In Progress");
        } else {
            System.out.println("\n❌ Failed to create issue.");
        }
    }
}

