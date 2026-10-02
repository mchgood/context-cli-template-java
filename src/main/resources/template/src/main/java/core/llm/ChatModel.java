package {{PACKAGE_NAME}}.core.llm;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Allows deterministic agents tests and alternate model providers. */
@FunctionalInterface
public interface ChatModel {
    Map<String,Object> complete(List<Map<String,Object>> messages, List<Map<String,Object>> tools) throws IOException, InterruptedException;
}
