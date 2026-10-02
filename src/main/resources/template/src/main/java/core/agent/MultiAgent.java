package {{PACKAGE_NAME}}.core.agent;

import {{PACKAGE_NAME}}.core.context.ContextManager;
import {{PACKAGE_NAME}}.core.llm.ChatModel;
import {{PACKAGE_NAME}}.core.prompt.Prompts;
import {{PACKAGE_NAME}}.core.tool.ToolManager;
import {{PACKAGE_NAME}}.evaluation.Evaluation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Researcher and executor run independently; coordinator receives both actual outputs. */
public final class MultiAgent {
    public record SubResult(String name, String output, boolean success, String error) {}
    public record Result(String finalResponse, List<SubResult> subAgents, boolean success) {}
    private final ChatModel model;
    private final ToolManager tools;
    private final Evaluation events;
    public MultiAgent(ChatModel model, ToolManager tools, Evaluation events) { this.model=model; this.tools=tools; this.events=events; }

    public Result run(String input) throws IOException, InterruptedException {
        events.reset(); events.emit("agent", 0, "main_agent");
        SubResult research = subAgent("researcher", Prompts.RESEARCHER, "研究分析：" + input);
        SubResult execution = subAgent("executor", Prompts.EXECUTOR,
                "需求：" + input + "\n研究结果：" + research.output() + "\n请给出执行方案。");
        if (!research.success() || !execution.success()) return new Result("", List.of(research, execution), false);
        ContextManager context = new ContextManager(); context.systemPrompt(Prompts.COORDINATOR);
        context.execution("researcher: " + research.output()); context.execution("executor: " + execution.output());
        context.user("用户需求：" + input + "\n请结合上述两个子 Agent 的结果回答。");
        events.emit("agent", 0, "main_agent");
        Map<String,Object> answer = model.complete(context.messages(), List.of());
        return new Result(String.valueOf(answer.getOrDefault("content", "")), List.of(research, execution), true);
    }
    private SubResult subAgent(String name, String prompt, String input) throws InterruptedException {
        try {
            ContextManager context = new ContextManager(); context.systemPrompt(prompt);
            String output = new SimpleAgent(model, context, tools, events).run(input, false, name);
            return new SubResult(name, output, true, "");
        } catch (IOException | RuntimeException e) { return new SubResult(name, "", false, e.getMessage()); }
    }
}
