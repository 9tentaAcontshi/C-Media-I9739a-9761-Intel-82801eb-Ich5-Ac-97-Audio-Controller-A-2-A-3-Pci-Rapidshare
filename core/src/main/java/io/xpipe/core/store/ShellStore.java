package io.xpipe.core.store;

import io.xpipe.core.impl.LocalStore;
import io.xpipe.core.process.OsType;
import io.xpipe.core.process.ShellDialect;
import io.xpipe.core.process.ShellControl;

import java.nio.charset.Charset;

public interface ShellStore extends DataStore, StatefulDataStore, LaunchableStore, FileSystemStore {

    public static ShellStore createLocal() {
        return new LocalStore();
    }

    static boolean isLocal(ShellStore s) {
        return s instanceof LocalStore;
    }

    default boolean canHaveSubs() {
        return true;
    }

    @Override
    default FileSystem createFileSystem() {
        return new ConnectionFileSystem(create(), this);
    }

    @Override
    default String prepareLaunchCommand() throws Exception {
        return create().prepareTerminalOpen();
    }

    default ShellControl create() {
        var pc = createControl();
        pc.onInit(processControl -> {
            setState("type", processControl.getShellDialect());
            setState("os", processControl.getOsType());
            setState("charset", processControl.getCharset());
        });
        return pc;
    }

    default ShellDialect getShellType() {
        return getState("type", ShellDialect.class, null);
    }

    default OsType getOsType() {
        return getState("os", OsType.class, null);
    }

    default Charset getCharset() {
        return getState("charset", Charset.class, null);
    }

    ShellControl createControl();

    public default ShellDialect determineType() throws Exception {
        try (var pc = create().start()) {
            return pc.getShellDialect();
        }
    }

    @Override
    default void validate() throws Exception {
        try (ShellControl pc = create().start()) {}
    }

    public default String queryMachineName() throws Exception {
        try (var pc = create().start()) {
            var operatingSystem = pc.getOsType();
            return operatingSystem.determineOperatingSystemName(pc);
        }
    }
}
