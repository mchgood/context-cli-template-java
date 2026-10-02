package {{PACKAGE_NAME}}.core.tool;

import {{PACKAGE_NAME}}.core.llm.Json;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/** Read-only local tools restricted to a real working-directory root. */
public final class ToolManager {
    private final Path root;
    private final java.util.Map<String,RegisteredTool> registered = new java.util.LinkedHashMap<>();
    public record RegisteredTool(Map<String,Object> definition, java.util.function.Function<Map<String,Object>,String> handler) {}
    public ToolManager(Path root) throws IOException { this.root = root.toRealPath(); }
    public List<Map<String,Object>> definitions() { var all = new java.util.ArrayList<Map<String,Object>>(List.of(def("read_file", "Read a UTF-8 file", "path"), def("list_files", "List a directory", "path"))); registered.values().forEach(t -> all.add(t.definition())); return all; }
    public void register(String name, String description, Map<String,Object> parameters, java.util.function.Function<Map<String,Object>,String> handler) {
        if (name.equals("read_file") || name.equals("list_files") || registered.containsKey(name)) throw new IllegalArgumentException("Duplicate tool: " + name);
        registered.put(name, new RegisteredTool(Map.of("type","function","function",Map.of("name",name,"description",description,"parameters",parameters)), handler));
    }
    private Map<String,Object> def(String name, String description, String arg) {
        return Map.of("type", "function", "function", Map.of("name", name, "description", description,
                "parameters", Map.of("type", "object", "properties", Map.of(arg, Map.of("type", "string")), "required", List.of(arg))));
    }
    public String execute(String name, String arguments) {
        try {
            Object value = Json.object(Json.parse(arguments)).get("path");
            if (registered.containsKey(name)) return registered.get(name).handler().apply(Json.object(Json.parse(arguments)));
            if (!(value instanceof String s)) throw new IllegalArgumentException("path must be a string");
            Path path = root.resolve(s).normalize();
            if (!path.startsWith(root)) throw new IllegalArgumentException("Path outside root");
            Path real = path.toRealPath();
            if (!real.startsWith(root)) throw new IllegalArgumentException("Path outside root");
            return switch (name) {
                case "read_file" -> { if (!Files.isRegularFile(real, LinkOption.NOFOLLOW_LINKS)) throw new IllegalArgumentException("Not a regular file");
                    if (Files.size(real) > 65536) throw new IllegalArgumentException("File exceeds 64 KiB"); yield Files.readString(real); }
                case "list_files" -> { if (!Files.isDirectory(real)) throw new IllegalArgumentException("Not a directory");
                    try (Stream<Path> stream = Files.list(real)) { yield stream.limit(100).map(p -> p.getFileName().toString()).toList().toString(); } }
                default -> throw new IllegalArgumentException("Unknown tool: " + name);
            };
        } catch (Exception e) { return "Tool error: " + e.getMessage(); }
    }
}
