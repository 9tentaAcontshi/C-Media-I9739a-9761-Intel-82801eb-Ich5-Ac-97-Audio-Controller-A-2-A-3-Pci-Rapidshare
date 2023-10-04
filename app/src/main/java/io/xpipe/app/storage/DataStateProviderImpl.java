package io.xpipe.app.storage;

import io.xpipe.core.store.StatefulDataStore;
import io.xpipe.core.store.DataStore;
import io.xpipe.core.store.DataStoreState;
import io.xpipe.core.util.DataStateProvider;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

public class DataStateProviderImpl extends DataStateProvider {

    @Override
    public void setState(DataStore store, Object value) {
        if (DataStorage.get() == null) {
            return;
        }

        var entry = DataStorage.get().getStoreEntryIfPresent(store);
        if (entry.isEmpty()) {
            return;
        }

        entry.get().setStorePersistentState(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DataStoreState> T getState(DataStore store, Supplier<T> def) {
        if (DataStorage.get() == null) {
            return def.get();
        }

        var entry = DataStorage.get().getStoreEntryIfPresent(store);
        if (entry.isEmpty()) {
            return def.get();
        }

        if (!(store instanceof StatefulDataStore<?> sds)) {
            return def.get();
        }

        var found = entry.get().getStorePersistentState();
        if (found == null) {
            entry.get().setStorePersistentState(def.get());
        }
        return (T) entry.get().getStorePersistentState();
    }

    @Override
    public void putCache(DataStore store, String key, Object value) {
        if (DataStorage.get() == null) {
            return;
        }

        var entry = DataStorage.get().getStoreEntryIfPresent(store);
        if (entry.isEmpty()) {
            return;
        }

        var old = entry.get().getStoreCache().put(key, value);
    }

    @Override
    public <T> T getCache(DataStore store, String key, Class<T> c, Supplier<T> def) {
        if (DataStorage.get() == null) {
            return def.get();
        }

        var entry = DataStorage.get().getStoreEntryIfPresent(store);
        if (entry.isEmpty()) {
            return def.get();
        }

        var result = entry.get().getStoreCache().computeIfAbsent(key, k -> def.get());
        return c.cast(result);
    }

    public boolean isInStorage(DataStore store) {
        var entry = DataStorage.get().getStoreEntryIfPresent(store);
        return entry.isPresent();
    }

    @Override
    public Path getInternalStreamStore(UUID id) {
        return DataStorage.get().getInternalStreamPath(id);
    }
}
