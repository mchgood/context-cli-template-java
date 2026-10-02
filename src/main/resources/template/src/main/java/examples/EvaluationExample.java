package {{PACKAGE_NAME}}.examples;

import {{PACKAGE_NAME}}.config.Environment;
import {{PACKAGE_NAME}}.core.agent.SimpleAgent;
import {{PACKAGE_NAME}}.core.context.ContextManager;
import {{PACKAGE_NAME}}.core.prompt.Prompts;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Dataset;
import {{PACKAGE_NAME}}.evaluation.Evaluation;
import {{PACKAGE_NAME}}.evaluation.Evaluator;
import java.nio.file.Path;

/** Behavioral evaluation is model-dependent; check the trace rather than assuming a pass. */
public final class EvaluationExample {
    public static void main(String[] args) throws Exception {
        var test = Dataset.CASES.get(0);
        var events = new Evaluation();
        var context = new ContextManager(); context.systemPrompt(Prompts.SIMPLE);
        String response = new SimpleAgent(Environment.deepSeek(), context, new ToolManager(Path.of(".")), events).run(test.input(), true);
        System.out.println("Response: " + response);
        System.out.println("Result: " + Evaluator.evaluate(test, events));
    }
}
