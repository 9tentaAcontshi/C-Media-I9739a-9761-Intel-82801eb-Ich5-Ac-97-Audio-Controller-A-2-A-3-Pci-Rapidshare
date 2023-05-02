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

    @Override
    default FileSystem createFileSystem() {
        return new ConnectionFileSystem(control(), this);
    }

    @Override
    default String prepareLaunchCommand() throws Exception {
        return control().prepareTerminalOpen();
    }

    default ShellControl control() {
        var pc = createBasicControl();
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

    ShellControl createBasicControl();

    public default ShellDialect determineType() throws Exception {
        try (var pc = control().start()) {
            return pc.getShellDialect();
        }
    }

    @Override
    default void validate() throws Exception {
        try (ShellControl pc = control().start()) {}
    }

    public default String queryMachineName() throws Exception {
        try (var pc = control().start()) {
            var operatingSystem = pc.getOsType();
            return operatingSystem.determineOperatingSystemName(pc);
        }
    }
}
