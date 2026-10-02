package {{PACKAGE_NAME}}.core.context.modules;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public final class ToolMessageSequenceContext {
    private final int limit;
    private final List<Map<String,Object>> messages=new ArrayList<>();
    public ToolMessageSequenceContext(int limit) { this.limit=limit; }
    public void add(Map<String,Object> message) { messages.add(message); while(messages.size()>limit) messages.remove(0); }
    public List<Map<String,Object>> messages() { return List.copyOf(messages); }
}
