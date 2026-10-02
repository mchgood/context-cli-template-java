package {{PACKAGE_NAME}}.core.context.modules;
public final class StructuredOutputContext {
    private String instruction="";
    public void set(String instruction) { this.instruction=java.util.Objects.requireNonNull(instruction); }
    public String format() { return instruction.isBlank() ? "" : "\nOutput requirements: " + instruction; }
}
