package {{PACKAGE_NAME}};

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Six context categories assembled into Chat Completions messages. */
public final class ContextManager {
    private String systemPrompt = "You are a helpful assistant.";
    private String structuredOutput = "";
    private final Map<String,String> memory = new LinkedHashMap<>();
    private final List<String> relevant = new ArrayList<>();
    private final List<Map<String,Object>> conversation = new ArrayList<>();
    private final List<Map<String,Object>> toolSequence = new ArrayList<>();
    public void systemPrompt(String value) { systemPrompt = value; }
    public void structuredOutput(String value) { structuredOutput = value; }
    public void remember(String key, String value) { memory.put(key, value); }
    public void relevant(String value) { relevant.add(value); }
    public void user(String text) { conversation.add(Map.of("role", "user", "content", text)); trim(); }
    public void assistant(Map<String,Object> message) { conversation.add(message); trim(); }
    public void tool(String id, String result) { Map<String,Object> m = Map.of("role", "tool", "tool_call_id", id, "content", result); conversation.add(m); toolSequence.add(m); trim(); }
    public List<Map<String,Object>> messages() {
        List<Map<String,Object>> result = new ArrayList<>();
        String system = systemPrompt + (memory.isEmpty() ? "" : "\nMemory: " + memory) + (relevant.isEmpty() ? "" : "\nRelevant: " + relevant)
                + (structuredOutput.isBlank() ? "" : "\nOutput: " + structuredOutput);
        result.add(Map.of("role", "system", "content", system)); result.addAll(conversation); return result;
    }
    public List<Map<String,Object>> toolMessages() { return List.copyOf(toolSequence); }
    private void trim() { // Trim only complete preceding turns, never break assistant/tool call adjacency.
        while (conversation.size() > 40) {
            int nextUser = 1;
            while (nextUser < conversation.size() && !"user".equals(conversation.get(nextUser).get("role"))) nextUser++;
            if (nextUser == conversation.size()) break;
            conversation.subList(0, nextUser).clear();
        }
        while (toolSequence.size() > 40) toolSequence.remove(0);
    }
}
