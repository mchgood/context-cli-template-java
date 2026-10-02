package {{PACKAGE_NAME}}.core.context.modules;
public final class SystemPromptContext {
    private String value="You are a helpful assistant.";
    public void set(String value) { this.value=java.util.Objects.requireNonNull(value); }
    public String get() { return value; }
}
