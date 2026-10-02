package {{PACKAGE_NAME}}.core.context.modules;
import java.util.ArrayList;
import java.util.List;
public final class RelevantContext {
    private final List<String> entries=new ArrayList<>();
    public void add(String entry) { entries.add(entry); }
    public String format() { return entries.isEmpty() ? "" : "\nRelevant context:\n" + String.join("\n",entries); }
}
