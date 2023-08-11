package io.xpipe.app.comp.storage.store;

import io.xpipe.app.comp.storage.StorageFilter;
import io.xpipe.app.fxcomps.Comp;
import io.xpipe.app.fxcomps.util.BindingsHelper;
import io.xpipe.app.storage.DataStorage;
import io.xpipe.app.storage.DataStoreEntry;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Value;

import java.util.Comparator;

@Value
public class StoreSection implements StorageFilter.Filterable {

    public static Comp<?> customSection(StoreSection e) {
        var prov = e.getWrapper().getEntry().getProvider();
        if (prov != null) {
            return prov.customContainer(e);
        } else {
            return new StoreSectionComp(e);
        }
    }

    StoreEntryWrapper wrapper;
    ObservableList<StoreSection> children;
    int depth;
    ObservableBooleanValue showDetails;


    static ObservableValue<Comparator<StoreSection>> sortMode = Bindings.createObjectBinding(() -> {
        return Comparator.<StoreSection>comparingInt(value -> value.getWrapper().getEntry().getState().isUsable() ? 1 : -1)
                .thenComparing(StoreViewState.get().getSortMode().getValue().comparator());
    }, StoreViewState.get().getSortMode());

    public StoreSection(StoreEntryWrapper wrapper, ObservableList<StoreSection> children, int depth) {
        this.wrapper = wrapper;
        this.children = children;
        this.depth = depth;
        if (wrapper != null) {
            this.showDetails = Bindings.createBooleanBinding(
                    () -> {
                        return wrapper.getExpanded().get() || children.size() == 0;
                    },
                    wrapper.getExpanded(),
                    children);
        } else {
            this.showDetails = new SimpleBooleanProperty(true);
        }
    }

    public static StoreSection createTopLevel() {
        var topLevel = BindingsHelper.cachedMappedContentBinding(
                StoreViewState.get().getAllEntries(), storeEntryWrapper -> create(storeEntryWrapper, 1));
        var filtered = BindingsHelper.filteredContentBinding(topLevel, section -> {
            return DataStorage.get()
                    .getParent(section.getWrapper().getEntry(), true)
                    .isEmpty();
        });
        var ordered = BindingsHelper.orderedContentBinding(filtered, sortMode);
        return new StoreSection(null, ordered, 0);
    }

    private static StoreSection create(StoreEntryWrapper e, int depth) {
        if (e.getEntry().getState() == DataStoreEntry.State.LOAD_FAILED) {
            return new StoreSection(e, FXCollections.observableArrayList(), depth);
        }

        var filtered =
                BindingsHelper.filteredContentBinding(StoreViewState.get().getAllEntries(), other -> {
                    return DataStorage.get()
                            .getParent(other.getEntry(), true)
                            .map(found -> found.equals(e.getEntry()))
                            .orElse(false);
                });
        var children = BindingsHelper.cachedMappedContentBinding(filtered, entry1 -> create(entry1, depth + 1));
        var ordered = BindingsHelper.orderedContentBinding(children, sortMode);
        return new StoreSection(e, ordered, depth);
    }

    @Override
    public boolean shouldShow(String filter) {
        return wrapper.shouldShow(filter)
                || children.stream().anyMatch(storeEntrySection -> storeEntrySection.shouldShow(filter));
    }
}
