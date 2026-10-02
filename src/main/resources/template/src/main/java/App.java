package {{PACKAGE_NAME}};

import java.nio.file.Path;
import java.util.Arrays;

/** CLI example: one question, optionally with read-only tools. */
public final class App {
    public static void main(String[] args) throws Exception {
        boolean withTools = args.length > 0 && args[0].equals("--tools");
        String prompt = String.join(" ", Arrays.copyOfRange(args, withTools ? 1 : 0, args.length));
        if (prompt.isBlank()) { System.err.println("Usage: App [--tools] question"); System.exit(2); }
        LlmClient llm = new LlmClient(System.getenv("DEEPSEEK_API_KEY"),
                System.getenv().getOrDefault("DEEPSEEK_MODEL", "deepseek-chat"),
                System.getenv().getOrDefault("DEEPSEEK_BASE_URL", "https://api.deepseek.com"));
        Evaluation evaluation = new Evaluation();
        Agent agent = new Agent(llm, new ContextManager(), new ToolManager(Path.of(".")), evaluation);
        System.out.println(agent.run(prompt, withTools));
        System.err.println("Tool calls: " + evaluation.toolCalls());
    }
}
