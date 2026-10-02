package {{PACKAGE_NAME}}.evaluation;

import java.util.List;
import java.util.Map;

/** Example behavioral expectations; model output may vary. */
public final class Dataset {
    private Dataset() {}
    public static final List<Evaluator.TestCase> CASES = List.of(
            new Evaluator.TestCase("S1", "列出当前目录的文件", List.of("simple_agent"), Map.of("simple_agent",List.of("list_files"))),
            new Evaluator.TestCase("S2", "列出文件并读取 README.md", List.of("simple_agent"), Map.of("simple_agent",List.of("list_files","read_file"))),
            new Evaluator.TestCase("M1", "提出提高代码质量的建议", List.of("main_agent","researcher","executor"), Map.of())
    );
}
