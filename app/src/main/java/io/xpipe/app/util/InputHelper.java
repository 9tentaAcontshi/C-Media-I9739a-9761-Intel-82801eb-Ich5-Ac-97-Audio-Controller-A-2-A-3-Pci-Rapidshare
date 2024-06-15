package io.xpipe.app.util;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.function.Consumer;

public class InputHelper {

    public static void onKeyCombination(EventTarget target, KeyCombination c, boolean filter, Consumer<KeyEvent> r) {
        EventHandler<KeyEvent> keyEventEventHandler = event -> {
            if (c.match(event)) {
                r.accept(event);
            }
        };
        if (filter) {
            target.addEventFilter(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        } else {
            target.addEventHandler(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        }
    }

    public static void onExactKeyCode(EventTarget target, KeyCode code, boolean filter, Consumer<KeyEvent> r) {
        EventHandler<KeyEvent> keyEventEventHandler = event -> {
            if (event.isAltDown() || event.isShiftDown() || event.isShortcutDown()) {
                return;
            }

            if (code == event.getCode()) {
                r.accept(event);
            }
        };
        if (filter) {
            target.addEventFilter(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        } else {
            target.addEventHandler(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        }
    }

    public static void onInput(EventTarget target, boolean filter, Consumer<KeyEvent> r) {
        EventHandler<KeyEvent> keyEventEventHandler = event -> {
            r.accept(event);
        };
        if (filter) {
            target.addEventFilter(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        } else {
            target.addEventHandler(KeyEvent.KEY_PRESSED, keyEventEventHandler);
        }
    }

    public static void onLeft(EventTarget target, boolean filter, Consumer<KeyEvent> r) {
        EventHandler<KeyEvent> e = event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.NUMPAD4) {
                r.accept(event);
            }
        };
        if (filter) {
            target.addEventFilter(KeyEvent.KEY_PRESSED, e);
        } else {
            target.addEventHandler(KeyEvent.KEY_PRESSED, e);
        }
    }

    public static void onRight(EventTarget target, boolean filter, Consumer<KeyEvent> r) {
        EventHandler<KeyEvent> e = event -> {
            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.NUMPAD6) {
                r.accept(event);
            }
        };
        if (filter) {
            target.addEventFilter(KeyEvent.KEY_PRESSED, e);
        } else {
            target.addEventHandler(KeyEvent.KEY_PRESSED, e);
        }
    }

    public static void onNavigationInput(EventTarget target, Consumer<Boolean> r) {
        target.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            var c = event.getCode();
            var list = List.of(KeyCode.SPACE, KeyCode.ENTER, KeyCode.SHIFT, KeyCode.TAB);
            r.accept(list.stream().anyMatch(keyCode -> keyCode == c)
                    || event.getCode().isNavigationKey());
        });
        target.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            r.accept(false);
        });
    }
}
