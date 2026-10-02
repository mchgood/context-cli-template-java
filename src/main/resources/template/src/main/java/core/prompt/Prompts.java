package {{PACKAGE_NAME}}.core.prompt;

/** Default prompts for the single and coordinated examples. */
public final class Prompts {
    private Prompts() {}
    public static final String SIMPLE = "你是一个助手，可按需要调用 list_files 和 read_file 完成任务。";
    public static final String RESEARCHER = "你是研究分析助手，梳理背景、假设与关键问题。";
    public static final String EXECUTOR = "你是方案执行助手，给出可操作的步骤、风险和验证方法。";
    public static final String COORDINATOR = "你是协调者，基于研究和执行结果形成完整答复；明确不确定性。";
}
