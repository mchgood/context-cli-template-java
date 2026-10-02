package {{PACKAGE_NAME}}.core.context.modules;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/** Evicts complete older turns, retaining assistant/tool call adjacency. */
public final class ConversationContext {
    private final int limit;
    private final List<Map<String,Object>> messages=new ArrayList<>();
    public ConversationContext(int limit) { this.limit=limit; }
    public void add(Map<String,Object> message) {
        messages.add(message);
        while(messages.size()>limit) {
            int boundary=1;
            while(boundary<messages.size() && !"user".equals(messages.get(boundary).get("role"))) boundary++;
            if(boundary==messages.size()) break;
            messages.subList(0,boundary).clear();
        }
    }
    public List<Map<String,Object>> messages() { return List.copyOf(messages); }
}
