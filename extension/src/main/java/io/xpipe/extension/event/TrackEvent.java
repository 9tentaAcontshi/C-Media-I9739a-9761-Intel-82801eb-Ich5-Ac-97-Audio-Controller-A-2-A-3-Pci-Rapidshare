package io.xpipe.extension.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.Instant;
import java.util.Map;

@Builder
@Getter
public class TrackEvent {

    public static class TrackEventBuilder {

        public TrackEventBuilder windowCategory() {
            this.category("window");
            return this;
        }

        public void handle() {
            build().handle();
        }
    }

    public static TrackEventBuilder fromMessage(String type, String message) {
        return builder().type(type).message(message);
    }

    public static void simple(String type, String message) {
        builder().type(type).message(message).build().handle();
    }

    public static TrackEventBuilder withInfo(String message) {
        return builder().type("info").message(message);
    }

    public static TrackEventBuilder withTrace(String message) {
        return builder().type("trace").message(message);
    }

    public static TrackEventBuilder withTrace(String cat, String message) {
        return builder().category(cat).type("trace").message(message);
    }

    public static void info(String message) {
        builder().type("info").message(message).build().handle();
    }

    public static void warn(String message) {
        builder().type("warn").message(message).build().handle();
    }

    public static TrackEventBuilder withDebug(String message) {
        return builder().type("debug").message(message);
    }

    public static void debug(String message) {
        builder().type("debug").message(message).build().handle();
    }

    public static void trace(String message) {
        builder().type("trace").message(message).build().handle();
    }

    public static void trace(String cat, String message) {
        builder().category(cat).type("trace").message(message).build().handle();
    }

    public static TrackEventBuilder withError(String message) {
        return builder().type("error").message(message);
    }

    public static void error(String message) {
        builder().type("error").message(message).build().handle();
    }

    private final Thread thread = Thread.currentThread();
    private final Instant instant = Instant.now();

    private String type;

    private String message;

    private String category;

    @Singular
    private Map<String, Object> tags;

    public void handle() {
        EventHandler.get().handle(this);
    }

    @Override
    public String toString() {
        var s =  message;
        if (tags.size() > 0) {
            s += " {\n";
            for (var e : tags.entrySet()) {
                s += "    " + e.getKey() + "=" + e.getValue() + "\n";
            }
            s += "}";
        }
        return s;
    }
}
