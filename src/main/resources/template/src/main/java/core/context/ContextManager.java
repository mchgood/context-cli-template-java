package {{PACKAGE_NAME}}.core.context;

import {{PACKAGE_NAME}}.core.context.modules.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Composes seven separate context modules for each model request. */
public final class ContextManager {
    private final SystemPromptContext system = new SystemPromptContext();
    private final StructuredOutputContext output = new StructuredOutputContext();
    private final MemoryContext memory = new MemoryContext();
    private final RelevantContext relevant = new RelevantContext();
    private final ConversationContext conversation = new ConversationContext(40);
    private final ToolMessageSequenceContext tools = new ToolMessageSequenceContext(40);
    private final ExecutionHistoryContext execution = new ExecutionHistoryContext();
    public void systemPrompt(String value) { system.set(value); }
    public void structuredOutput(String value) { output.set(value); }
    public void remember(String key, String value) { memory.put(key,value); }
    public void relevant(String value) { relevant.add(value); }
    public void execution(String value) { execution.add(value); }
    public void user(String text) { conversation.add(Map.of("role","user","content",text)); }
    public void assistant(Map<String,Object> message) { conversation.add(message); }
    public void tool(String id, String result) { Map<String,Object> message=Map.of("role","tool","tool_call_id",id,"content",result); conversation.add(message); tools.add(message); }
    public List<Map<String,Object>> messages() {
        List<Map<String,Object>> result=new ArrayList<>();
        String prompt=system.get() + output.format() + memory.format() + relevant.format() + execution.format();
        result.add(Map.of("role","system","content",prompt)); result.addAll(conversation.messages()); return result;
    }
    public List<Map<String,Object>> toolMessages() { return tools.messages(); }
    public List<String> executionHistory() { return execution.entries(); }
}
