package {{PACKAGE_NAME}}.core.context.modules;
import java.util.ArrayList;
import java.util.List;
public final class ExecutionHistoryContext {
    private final List<String> entries=new ArrayList<>();
    public void add(String entry) { entries.add(java.util.Objects.requireNonNull(entry)); }
    public List<String> entries() { return List.copyOf(entries); }
    public String format() { return entries.isEmpty() ? "" : "\nExecution history:\n" + String.join("\n",entries); }
}
