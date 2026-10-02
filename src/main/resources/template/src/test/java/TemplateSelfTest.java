package {{PACKAGE_NAME}};

import {{PACKAGE_NAME}}.core.agent.MultiAgent;
import {{PACKAGE_NAME}}.core.agent.SimpleAgent;
import {{PACKAGE_NAME}}.core.context.ContextManager;
import {{PACKAGE_NAME}}.core.llm.ChatModel;
import {{PACKAGE_NAME}}.core.llm.Json;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Evaluation;
import {{PACKAGE_NAME}}.evaluation.Evaluator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

/** Run with java -cp target/classes:target/test-classes {{PACKAGE_NAME}}.TemplateSelfTest. */
public final class TemplateSelfTest {
    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("context-template-test");
        Files.writeString(root.resolve("hello.txt"), "world");
        ToolManager tools = new ToolManager(root);
        check(tools.execute("read_file", "{\"path\":\"hello.txt\"}").equals("world"), "read_file");
        check(tools.execute("read_file", "{\"path\":\"../outside\"}").startsWith("Tool error"), "traversal");
        check(Json.stringify(Json.parse("{\"n\":42,\"v\":[true,null]}")).equals("{\"n\":42,\"v\":[true,null]}"), "JSON round trip");

        var answers = new ArrayDeque<Map<String,Object>>();
        answers.add(Map.of("content", "", "tool_calls", List.of(Map.of("id", "call-1", "type", "function", "function", Map.of("name", "read_file", "arguments", "{\"path\":\"hello.txt\"}")))));
        answers.add(Map.of("content", "The file says world"));
        ChatModel fake = (messages, definitions) -> {
            if (answers.size() == 1) check(messages.stream().anyMatch(m -> "tool".equals(m.get("role")) && "world".equals(m.get("content"))), "tool result returned to model");
            return answers.remove();
        };
        Evaluation events = new Evaluation();
        String result = new SimpleAgent(fake, new ContextManager(), tools, events).run("read hello.txt", true);
        check(result.contains("world") && events.toolCalls() == 1, "tool loop");
        check(Evaluator.evaluate(new Evaluator.TestCase("T", "", List.of("simple_agent"), Map.of("simple_agent",List.of("read_file"))),events).passed(), "evaluation");

        var multiAnswers = new ArrayDeque<>(List.of(Map.<String,Object>of("content","research-output"), Map.<String,Object>of("content","execution-output"), Map.<String,Object>of("content","summary")));
        ChatModel multiFake = (messages, definitions) -> {
            if (multiAnswers.size() == 1) check(messages.get(0).get("content").toString().contains("research-output") && messages.get(0).get("content").toString().contains("execution-output"), "coordinator receives both outputs");
            return multiAnswers.remove();
        };
        var multi = new MultiAgent(multiFake, tools, new Evaluation()).run("task");
        check(multi.success() && multi.finalResponse().equals("summary") && multi.subAgents().size()==2, "multi agent");
        System.out.println("TemplateSelfTest passed");
    }
    private static void check(boolean ok, String label) { if (!ok) throw new AssertionError(label); }
}
