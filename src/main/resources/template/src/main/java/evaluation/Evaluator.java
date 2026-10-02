package {{PACKAGE_NAME}}.evaluation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Compare expected Agent/tool presence with the event trace. */
public final class Evaluator {
    public record TestCase(String id, String input, List<String> agents, Map<String,List<String>> tools) {}
    public record Result(boolean passed, List<String> missingAgents, List<String> extraAgents,
                         Map<String,List<String>> missingTools, Map<String,List<String>> extraTools) {}
    private Evaluator() {}
    public static Result evaluate(TestCase expected, Evaluation actual) {
        List<String> missingAgents = difference(expected.agents(), actual.agents());
        List<String> extraAgents = difference(actual.agents(), expected.agents());
        Map<String,List<String>> missingTools = new LinkedHashMap<>(), extraTools = new LinkedHashMap<>();
        Map<String,List<String>> observed = actual.toolsByAgent();
        for (String agent : union(expected.tools().keySet(), observed.keySet())) {
            List<String> missing = difference(expected.tools().getOrDefault(agent,List.of()), observed.getOrDefault(agent,List.of()));
            List<String> extra = difference(observed.getOrDefault(agent,List.of()), expected.tools().getOrDefault(agent,List.of()));
            if (!missing.isEmpty()) missingTools.put(agent,missing);
            if (!extra.isEmpty()) extraTools.put(agent,extra);
        }
        return new Result(missingAgents.isEmpty() && extraAgents.isEmpty() && missingTools.isEmpty() && extraTools.isEmpty(), missingAgents, extraAgents, missingTools, extraTools);
    }
    private static <T> List<T> union(java.util.Collection<T> a, java.util.Collection<T> b) { List<T> result=new ArrayList<>(a); for(T item:b) if(!result.contains(item)) result.add(item); return result; }
    private static <T> List<T> difference(java.util.Collection<T> a, java.util.Collection<T> b) { List<T> result=new ArrayList<>(); for(T item:a) if(!b.contains(item)) result.add(item); return result; }
}
