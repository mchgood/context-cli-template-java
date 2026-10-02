package {{PACKAGE_NAME}}.core.context.modules;
import java.util.LinkedHashMap;
import java.util.Map;
public final class MemoryContext {
    private final Map<String,String> entries=new LinkedHashMap<>();
    public void put(String key,String value) { entries.put(key,value); }
    public String format() { return entries.isEmpty() ? "" : "\nMemory: " + entries; }
    public Map<String,String> entries() { return Map.copyOf(entries); }
}
