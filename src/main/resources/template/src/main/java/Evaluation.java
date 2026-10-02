package {{PACKAGE_NAME}};

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
}
