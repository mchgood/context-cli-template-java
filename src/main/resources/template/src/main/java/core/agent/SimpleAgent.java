package {{PACKAGE_NAME}}.core.agent;

import {{PACKAGE_NAME}}.core.context.ContextManager;
import {{PACKAGE_NAME}}.core.llm.ChatModel;
import {{PACKAGE_NAME}}.core.llm.Json;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Evaluation;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Bounded tool-calling agent. */
public final class SimpleAgent {
    private final ChatModel llm;
    private final ContextManager context;
    private final ToolManager tools;
    private final Evaluation evaluation;
    public SimpleAgent(ChatModel llm, ContextManager context, ToolManager tools, Evaluation evaluation) {
        this.llm = llm; this.context = context; this.tools = tools; this.evaluation = evaluation;
    }
    public String run(String input, boolean allowTools) throws IOException, InterruptedException {
        return run(input, allowTools, "simple_agent");
    }
    public String run(String input, boolean allowTools, String name) throws IOException, InterruptedException {
        evaluation.emit("agent", 0, name);
        context.user(input);
        for (int round = 0; round < 8; round++) {
            long start = System.nanoTime();
            Map<String,Object> answer = llm.complete(context.messages(), allowTools ? tools.definitions() : List.of());
            evaluation.emit("llm", (System.nanoTime() - start) / 1_000_000, "round " + (round + 1));
            Map<String,Object> assistant = new LinkedHashMap<>(); assistant.put("role", "assistant"); assistant.put("content", answer.get("content"));
            Object calls = answer.get("tool_calls");
            if (calls instanceof List<?> list && !list.isEmpty()) {
                if (!allowTools) throw new IOException("Unexpected tool call");
                assistant.put("tool_calls", calls); context.assistant(assistant);
                for (Object raw : list) {
                    Map<String,Object> call = Json.object(raw); Map<String,Object> function = Json.object(call.get("function"));
                    String id = String.valueOf(call.get("id")); String toolName = String.valueOf(function.get("name"));
                    String result = tools.execute(toolName, String.valueOf(function.get("arguments")));
                    context.tool(id, result); evaluation.emit("tool", 0, toolName);
                }
            } else {
                context.assistant(assistant); return String.valueOf(answer.getOrDefault("content", ""));
            }
        }
        throw new IOException("Tool loop exceeded 8 rounds");
    }
}
