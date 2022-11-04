package io.xpipe.extension.util;

import io.xpipe.api.DataSource;
import io.xpipe.api.util.XPipeDaemonController;
import io.xpipe.core.store.DataStore;
import io.xpipe.core.store.FileStore;
import io.xpipe.core.util.JacksonMapper;
import io.xpipe.extension.XPipeServiceProviders;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.nio.file.Path;

public class ExtensionTest {

    @SneakyThrows
    public static DataStore getResource(String name) {
        var url = ExtensionTest.class.getClassLoader().getResource(name);
        if (url == null) {
            throw new IllegalArgumentException(String.format("File %s does not exist", name));
        }
        var file = Path.of(url.toURI()).toString();
        return FileStore.local(Path.of(file));
    }

    public static DataSource getSource(String type, DataStore store) {
        return DataSource.create(null, type, store);
    }

    public static DataSource getSource(String type, String file) {
        return DataSource.create(null, type, getResource(file));
    }

    public static DataSource getSource(io.xpipe.core.source.DataSource<?> source) {
        return DataSource.create(null, source);
    }

    @BeforeAll
    public static void setup() throws Exception {
        JacksonMapper.initModularized(ModuleLayer.boot());
        XPipeServiceProviders.load(ModuleLayer.boot());
        XPipeDaemonController.start();
    }

    @AfterAll
    public static void teardown() throws Exception {
        XPipeDaemonController.stop();
    }
}
