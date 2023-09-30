package io.xpipe.app.core;

import atlantafx.base.theme.*;
import com.jthemedetecor.OsThemeDetector;
import io.xpipe.app.ext.PrefsChoiceValue;
import io.xpipe.app.fxcomps.util.PlatformThread;
import io.xpipe.app.fxcomps.util.SimpleChangeListener;
import io.xpipe.app.issue.ErrorEvent;
import io.xpipe.app.issue.TrackEvent;
import io.xpipe.app.prefs.AppPrefs;
import io.xpipe.core.process.OsType;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class AppTheme {

    private static final PseudoClass LIGHT = PseudoClass.getPseudoClass("light");
    private static final PseudoClass DARK = PseudoClass.getPseudoClass("dark");
    private static final PseudoClass PRETTY = PseudoClass.getPseudoClass("pretty");
    private static final PseudoClass PERFORMANCE = PseudoClass.getPseudoClass("performance");

    public static void initTheme(Window stage) {
        var t = AppPrefs.get().theme.getValue();
        if (t == null) {
            return;
        }

        stage.getScene().getRoot().pseudoClassStateChanged(LIGHT, !t.isDark());
        stage.getScene().getRoot().pseudoClassStateChanged(DARK, t.isDark());
        SimpleChangeListener.apply(AppPrefs.get().performanceMode(),val -> {
            stage.getScene().getRoot().pseudoClassStateChanged(PRETTY, !val);
            stage.getScene().getRoot().pseudoClassStateChanged(PERFORMANCE, val);
        });
    }

    public static void init() {
        if (AppPrefs.get() == null) {
            Theme.getDefaultLightTheme().apply();
            return;
        }

        OsThemeDetector detector = OsThemeDetector.getDetector();
        if (AppPrefs.get().theme.getValue() == null) {
            try {
                setDefault(detector.isDark());
            } catch (Throwable ex) {
                ErrorEvent.fromThrowable(ex).omit().handle();
                setDefault(false);
            }
        }
        var t = AppPrefs.get().theme.getValue();

        t.apply();
        TrackEvent.debug("Set theme " + t.getId() + " for scene");

        detector.registerListener(dark -> {
            PlatformThread.runLaterIfNeeded(() -> {
                if (dark && !AppPrefs.get().theme.getValue().isDark()) {
                    AppPrefs.get().theme.setValue(Theme.getDefaultDarkTheme());
                }

                if (!dark && AppPrefs.get().theme.getValue().isDark()) {
                    AppPrefs.get().theme.setValue(Theme.getDefaultLightTheme());
                }
            });
        });

        AppPrefs.get().theme.addListener((c, o, n) -> {
            changeTheme(n);
        });

        Window.getWindows().addListener((ListChangeListener<? super Window>) c -> {
            c.getList().forEach(window -> {
                window.opacityProperty().bind(AppPrefs.get().windowOpacity());
            });
        });
    }

    private static void setDefault(boolean dark) {
        if (dark) {
            AppPrefs.get().theme.setValue(Theme.getDefaultDarkTheme());
        } else {
            AppPrefs.get().theme.setValue(Theme.getDefaultLightTheme());
        }
    }

    private static void changeTheme(Theme newTheme) {
        if (newTheme == null) {
            return;
        }

        PlatformThread.runLaterIfNeeded(() -> {
            for (Window window : Window.getWindows()) {
                var scene = window.getScene();
                Image snapshot = scene.snapshot(null);
                initTheme(window);
                Pane root = (Pane) scene.getRoot();

                ImageView imageView = new ImageView(snapshot);
                root.getChildren().add(imageView);

                // Animate!
                var transition = new Timeline(
                        new KeyFrame(
                                Duration.ZERO, new KeyValue(imageView.opacityProperty(), 1, Interpolator.EASE_OUT)),
                        new KeyFrame(
                                Duration.millis(1250),
                                new KeyValue(imageView.opacityProperty(), 0, Interpolator.EASE_OUT)));
                transition.setOnFinished(e -> {
                    root.getChildren().remove(imageView);
                });
                transition.play();
            }

            newTheme.apply();
            TrackEvent.debug("Set theme " + newTheme.getId() + " for scene");
        });
    }

    public static class DerivedTheme extends Theme {

        private final String name;

        public DerivedTheme(String id, String name, atlantafx.base.theme.Theme theme) {
            super(id, theme);
            this.name = name;
        }

        @Override
        @SneakyThrows
        public void apply() {
            var builder = new StringBuilder();
            AppResources.with(AppResources.XPIPE_MODULE, "theme/" + id + ".css", path->{
                var content = Files.readString(path);
                builder.append(content);
            });

            AppResources.with("atlantafx.base", theme.getUserAgentStylesheet(), path->{
                var baseStyleContent = Files.readString(path);
                builder.append("\n").append(baseStyleContent.lines().skip(builder.toString().lines().count()).collect(Collectors.joining("\n")));
            });

            var out = Files.createTempFile(id, ".css");
            Files.writeString(out, builder.toString());

            Application.setUserAgentStylesheet(out.toUri().toString());
        }


        @Override
        public String toTranslatedString() {
            return name;
        }
    };

    @AllArgsConstructor
    public static class Theme implements PrefsChoiceValue {

        public static final Theme PRIMER_LIGHT = new Theme("light", new PrimerLight());
        public static final Theme PRIMER_DARK = new Theme("dark", new PrimerDark());
        public static final Theme NORD_LIGHT = new Theme("nordLight", new NordLight());
        public static final Theme NORD_DARK = new Theme("nordDark", new NordDark());
        public static final Theme CUPERTINO_LIGHT = new Theme("cupertinoLight", new CupertinoLight());
        public static final Theme CUPERTINO_DARK = new Theme("cupertinoDark", new CupertinoDark());
        public static final Theme DRACULA = new Theme("dracula", new Dracula());

        // Adjust this to create your own theme
        public static final Theme CUSTOM = new DerivedTheme("custom", "Custom", new PrimerDark());

        // Also include your custom theme here
        public static final List<Theme> ALL = List.of(PRIMER_LIGHT, PRIMER_DARK, NORD_LIGHT, NORD_DARK, CUPERTINO_LIGHT, CUPERTINO_DARK, DRACULA);

        static Theme getDefaultLightTheme() {
            return switch (OsType.getLocal()) {
                case OsType.Windows windows -> PRIMER_LIGHT;
                case OsType.Linux linux -> NORD_LIGHT;
                case OsType.MacOs macOs -> CUPERTINO_LIGHT;
            };
        }

        static Theme getDefaultDarkTheme() {
            return switch (OsType.getLocal()) {
                case OsType.Windows windows -> PRIMER_DARK;
                case OsType.Linux linux -> NORD_DARK;
                case OsType.MacOs macOs -> CUPERTINO_DARK;
            };
        }

        protected final String id;
        protected final atlantafx.base.theme.Theme theme;
        
        public boolean isDark() {
            return theme.isDarkMode();
        }
        
        public void apply() {
            Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
        }

        @Override
        public String toTranslatedString() {
            return theme.getName();
        }

        @Override
        public String getId() {
            return id;
        }
    }
}
