package io.xpipe.extension;

import io.xpipe.core.dialog.Dialog;
import io.xpipe.core.source.DataSource;
import io.xpipe.core.store.DataStore;

import java.lang.reflect.InvocationTargetException;

public interface UniformDataSourceProvider<T extends DataSource<?>> extends DataSourceProvider<T> {

    @Override
    default Dialog configDialog(T source, boolean all) {
        return Dialog.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    default T createDefaultSource(DataStore input) {
        try {
            return (T) getSourceClass().getDeclaredConstructors()[0].newInstance(input);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }
}
