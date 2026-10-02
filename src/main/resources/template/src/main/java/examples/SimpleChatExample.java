package {{PACKAGE_NAME}}.examples;

import {{PACKAGE_NAME}}.config.Environment;
import {{PACKAGE_NAME}}.core.agent.SimpleAgent;
import {{PACKAGE_NAME}}.core.context.ContextManager;
import {{PACKAGE_NAME}}.core.prompt.Prompts;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Evaluation;
import java.nio.file.Path;

public final class SimpleChatExample {
    public static void main(String[] args) throws Exception {
        ContextManager context = new ContextManager(); context.systemPrompt(Prompts.SIMPLE);
        var agent = new SimpleAgent(Environment.deepSeek(), context, new ToolManager(Path.of(".")), new Evaluation());
        System.out.println(agent.run(args.length==0 ? "你好" : String.join(" ",args), true));
    }
}
