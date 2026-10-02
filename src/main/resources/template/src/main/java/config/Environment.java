package {{PACKAGE_NAME}}.config;

import {{PACKAGE_NAME}}.core.llm.LlmClient;

/** Explicit environment configuration; no secret is stored in generated files. */
public final class Environment {
    private Environment() {}
    public static LlmClient deepSeek() {
        return new LlmClient(System.getenv("DEEPSEEK_API_KEY"),
                System.getenv().getOrDefault("DEEPSEEK_MODEL", "deepseek-chat"),
                System.getenv().getOrDefault("DEEPSEEK_BASE_URL", "https://api.deepseek.com"));
    }
}
