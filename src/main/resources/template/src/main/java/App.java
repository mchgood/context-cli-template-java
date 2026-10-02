package {{PACKAGE_NAME}};

import {{PACKAGE_NAME}}.config.Environment;
import {{PACKAGE_NAME}}.core.agent.MultiAgent;
import {{PACKAGE_NAME}}.core.agent.SimpleAgent;
import {{PACKAGE_NAME}}.core.context.ContextManager;
import {{PACKAGE_NAME}}.core.llm.LlmClient;
import {{PACKAGE_NAME}}.core.prompt.Prompts;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Evaluation;
import java.nio.file.Path;
import java.util.Arrays;

/** CLI example: one question, optionally with read-only tools. */
public final class App {
    public static void main(String[] args) throws Exception {
        boolean multi = args.length > 0 && args[0].equals("--multi");
        boolean withTools = args.length > 0 && args[0].equals("--tools");
        String prompt = String.join(" ", Arrays.copyOfRange(args, multi || withTools ? 1 : 0, args.length));
        if (prompt.isBlank()) { System.err.println("Usage: App [--tools|--multi] question"); System.exit(2); }
        LlmClient llm = Environment.deepSeek();
        Evaluation evaluation = new Evaluation();
        ToolManager tools = new ToolManager(Path.of("."));
        if (multi) System.out.println(new MultiAgent(llm, tools, evaluation).run(prompt).finalResponse());
        else {
            ContextManager context = new ContextManager(); context.systemPrompt(Prompts.SIMPLE);
            System.out.println(new SimpleAgent(llm, context, tools, evaluation).run(prompt, withTools));
        }
        System.err.println("Tool calls: " + evaluation.toolCalls());
    }
}
