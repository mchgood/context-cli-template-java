package {{PACKAGE_NAME}}.examples;

import {{PACKAGE_NAME}}.config.Environment;
import {{PACKAGE_NAME}}.core.agent.MultiAgent;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Evaluation;
import java.nio.file.Path;

public final class MultiChatExample {
    public static void main(String[] args) throws Exception {
        var result = new MultiAgent(Environment.deepSeek(), new ToolManager(Path.of(".")), new Evaluation())
                .run(args.length==0 ? "如何提升代码质量？" : String.join(" ",args));
        if (!result.success()) throw new IllegalStateException("Sub Agent failed: " + result.subAgents());
        System.out.println(result.finalResponse());
    }
}
