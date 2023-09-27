package io.xpipe.app.util;

import io.xpipe.app.comp.storage.store.StoreCategoryWrapper;
import io.xpipe.app.comp.storage.store.StoreViewState;
import io.xpipe.app.fxcomps.SimpleComp;
import io.xpipe.app.fxcomps.util.PlatformThread;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import lombok.EqualsAndHashCode;
import lombok.Value;

public class DataStoreCategoryChoiceComp extends SimpleComp {

    private final Property<StoreCategoryWrapper> selected;

    public DataStoreCategoryChoiceComp(Property<StoreCategoryWrapper> selected) {
        this.selected = selected;
    }

    @Override
    protected Region createSimple() {
        var box = new ComboBox<>(StoreViewState.get().getSortedCategories());
        box.setValue(selected.getValue());
        box.valueProperty().addListener((observable, oldValue, newValue) -> {
            selected.setValue(newValue);
        });
        selected.addListener((observable, oldValue, newValue) -> {
            PlatformThread.runLaterIfNeeded(() -> box.setValue(newValue));
        });
        box.setCellFactory(param -> {
            return new Cell(true);
        });
        box.setButtonCell(new Cell(false));
        return box;
    }


    @EqualsAndHashCode(callSuper = true)
    @Value
    private static class Cell extends ListCell<StoreCategoryWrapper> {

        boolean indent;

        @Override
        protected void updateItem(StoreCategoryWrapper w, boolean empty) {
            super.updateItem(w, empty);
            textProperty().unbind();
            if (w != null) {
                textProperty().bind(w.nameProperty());
                setPadding(new Insets(6, 6, 6, 8 + (indent ? w.getDepth() * 6 : 0)));
            }
        }
    }
}
