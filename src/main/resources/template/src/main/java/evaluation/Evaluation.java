package {{PACKAGE_NAME}}.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** In-process event bus and basic trace metrics. */
public final class Evaluation {
    public record Event(String type, long elapsedMillis, String detail) {}
    private final List<Consumer<Event>> listeners = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    public void subscribe(Consumer<Event> listener) { listeners.add(listener); }
    public void emit(String type, long elapsedMillis, String detail) { Event event = new Event(type, elapsedMillis, detail); events.add(event); listeners.forEach(l -> l.accept(event)); }
    public long toolCalls() { return events.stream().filter(e -> e.type().equals("tool")).count(); }
    public List<Event> events() { return List.copyOf(events); }
    public void reset() { events.clear(); }
    public List<String> agents() { return events.stream().filter(e -> e.type().equals("agent")).map(Event::detail).distinct().toList(); }
    public java.util.Map<String,List<String>> toolsByAgent() {
        java.util.Map<String,List<String>> result = new java.util.LinkedHashMap<>();
        String agent = "";
        for (Event event : events) { if (event.type().equals("agent")) { agent=event.detail(); result.putIfAbsent(agent,new ArrayList<>()); }
            else if (event.type().equals("tool")) result.computeIfAbsent(agent,k -> new ArrayList<>()).add(event.detail()); }
        return result;
    }
}
