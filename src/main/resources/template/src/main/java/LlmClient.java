package {{PACKAGE_NAME}};

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** OpenAI-compatible Chat Completions client using Java 17 HttpClient. */
public final class LlmClient {
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final String apiKey, model, baseUrl;
    public LlmClient(String apiKey, String model, String baseUrl) {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalArgumentException("DEEPSEEK_API_KEY is required");
        this.apiKey = apiKey; this.model = model;
        if (!baseUrl.startsWith("https://")) throw new IllegalArgumentException("Base URL must use HTTPS");
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }
    public Map<String,Object> complete(List<Map<String,Object>> messages, List<Map<String,Object>> tools) throws IOException, InterruptedException {
        Map<String,Object> body = new LinkedHashMap<>(); body.put("model", model); body.put("messages", messages);
        if (!tools.isEmpty()) { body.put("tools", tools); body.put("tool_choice", "auto"); }
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/completions"))
                .timeout(Duration.ofSeconds(60)).header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(Json.stringify(body))).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) throw new IOException("LLM HTTP " + response.statusCode() + ": " + response.body().substring(0, Math.min(300, response.body().length())));
        Map<String,Object> root = Json.object(Json.parse(response.body()));
        List<Object> choices = Json.array(root.get("choices"));
        if (choices.isEmpty()) throw new IOException("LLM returned no choices");
        return Json.object(Json.object(choices.get(0)).get("message"));
    }
}
