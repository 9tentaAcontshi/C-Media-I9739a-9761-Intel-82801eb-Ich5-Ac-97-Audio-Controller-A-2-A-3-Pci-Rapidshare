package io.xpipe.app.comp;

import io.xpipe.app.comp.base.BackgroundImageComp;
import io.xpipe.app.comp.base.SideMenuBarComp;
import io.xpipe.app.core.AppFont;
import io.xpipe.app.core.AppImages;
import io.xpipe.app.core.AppLayoutModel;
import io.xpipe.app.fxcomps.Comp;
import io.xpipe.app.fxcomps.CompStructure;
import io.xpipe.app.fxcomps.SimpleCompStructure;
import io.xpipe.app.fxcomps.util.PlatformThread;
import io.xpipe.app.prefs.AppPrefs;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class AppLayoutComp extends Comp<CompStructure<StackPane>> {

    private final AppLayoutModel model = AppLayoutModel.get();

    @Override
    public CompStructure<StackPane> createBase() {
        var map = new HashMap<AppLayoutModel.Entry, Region>();
        getRegion(model.getEntries().get(0), map);
        getRegion(model.getEntries().get(1), map);

        var pane = new BorderPane();
        var sidebar = new SideMenuBarComp(model.getSelected(), model.getEntries());
        pane.setCenter(getRegion(model.getSelected().getValue(), map));
        pane.setRight(sidebar.createRegion());
        model.getSelected().addListener((c, o, n) -> {
            if (o != null && o.equals(model.getEntries().get(2))) {
                AppPrefs.get().save();
            }

            PlatformThread.runLaterIfNeeded(() -> {
                pane.setCenter(getRegion(n, map));
            });
        });
        AppFont.normal(pane);

        var bg = new BackgroundImageComp(AppImages.image("bg.png"))
                .styleClass("background")
                .hide(AppPrefs.get().performanceMode());

        return new SimpleCompStructure<>(new StackPane(bg.createRegion(), pane));
    }

    private Region getRegion(AppLayoutModel.Entry entry, Map<AppLayoutModel.Entry, Region> map) {
        if (map.containsKey(entry)) {
            return map.get(entry);
        }

        Region r = entry.comp().createRegion();
        map.put(entry, r);
        return r;
    }
}
