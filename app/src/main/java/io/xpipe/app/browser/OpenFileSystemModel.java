/* SPDX-License-Identifier: MIT */

package io.xpipe.app.browser;

import io.xpipe.app.fxcomps.util.BindingsHelper;
import io.xpipe.app.issue.ErrorEvent;
import io.xpipe.app.util.BusyProperty;
import io.xpipe.app.util.TerminalHelper;
import io.xpipe.app.util.ThreadHelper;
import io.xpipe.core.store.ConnectionFileSystem;
import io.xpipe.core.store.FileSystem;
import io.xpipe.core.store.FileSystemStore;
import io.xpipe.core.store.ShellStore;
import javafx.beans.property.*;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Getter
final class OpenFileSystemModel {

    private Property<FileSystemStore> store = new SimpleObjectProperty<>();
    private FileSystem fileSystem;
    private List<String> roots;
    private final FileListModel fileList;
    private final ReadOnlyObjectWrapper<String> currentPath = new ReadOnlyObjectWrapper<>();
    private final NavigationHistory history = new NavigationHistory();
    private final BooleanProperty busy = new SimpleBooleanProperty();

    public OpenFileSystemModel() {
        fileList = new FileListModel(this);
    }

    public void refresh() {
        BusyProperty.execute(busy, () -> {
            cdSync(currentPath.get());
        });
    }

    private void refreshInternal() {
        cdSync(currentPath.get());
    }

    public FileSystem.FileEntry getCurrentDirectory() {
        return new FileSystem.FileEntry(fileSystem, currentPath.get(), Instant.now(), true, false, 0);
    }

    public void cd(String path) {
        ThreadHelper.runFailableAsync(() -> {
            cdSync(path);
        });
    }

    private boolean cdSync(String path) {
        try (var ignored = new BusyProperty(busy)) {
            if (!navigateTo(path)) {
                return false;
            }

            currentPath.set(path);
            if (!Objects.equals(history.getCurrent(), path)) {
                history.append(path);
            }
            return true;
        }
    }

    private boolean navigateTo(String dir) {
        try {
            List<FileSystem.FileEntry> newList;
            if (dir != null) {
                newList = getFileSystem().listFiles(dir).toList();
            } else {
                newList = getFileSystem().listRoots().stream()
                        .map(s -> new FileSystem.FileEntry(getFileSystem(), s, Instant.now(), true, false, 0))
                        .toList();
            }
            BindingsHelper.setContent(fileList.getAll(), newList);
            return true;
        } catch (Exception e) {
            ErrorEvent.fromThrowable(e).handle();
            return false;
        }
    }

    public void dropLocalFilesIntoAsync(FileSystem.FileEntry entry, List<Path> files) {
        ThreadHelper.runFailableAsync(() -> {
            BusyProperty.execute(busy, () -> {
                FileSystemHelper.dropLocalFilesInto(entry, files);
                refreshInternal();
            });
        });
    }

    public void dropFilesIntoAsync(FileSystem.FileEntry target, List<FileSystem.FileEntry> files, boolean explicitCopy) {
        ThreadHelper.runFailableAsync(() -> {
            BusyProperty.execute(busy, () -> {
                FileSystemHelper.dropFilesInto(target, files, explicitCopy);
                refreshInternal();
            });
        });
    }

    public void createDirectoryAsync(String path) {
        if (path.isBlank()) {
            return;
        }

        ThreadHelper.runFailableAsync(() -> {
            BusyProperty.execute(busy, () -> {
                fileSystem.mkdirs(path);
                refreshInternal();
            });
        });
    }

    public void createFileAsync(String path) {
        if (path.isBlank()) {
            return;
        }

        ThreadHelper.runFailableAsync(() -> {
            BusyProperty.execute(busy, () -> {
                fileSystem.touch(path);
                refreshInternal();
            });
        });
    }

    public void deleteAsync(String path) {
        ThreadHelper.runFailableAsync(() -> {
            BusyProperty.execute(busy, () -> {
                fileSystem.delete(path);
                refreshInternal();
            });
        });
    }

    void closeSync() {
        if (fileSystem == null) {
            return;
        }

        try {
            fileSystem.close();
        } catch (IOException e) {
            ErrorEvent.fromThrowable(e).handle();
        }
        fileSystem = null;
        store = null;
    }

    public void switchSync(FileSystemStore fileSystem) throws Exception {
        BusyProperty.execute(busy, () -> {
            closeSync();
            this.store.setValue(fileSystem);
            var fs = fileSystem.createFileSystem();
            fs.open();
            this.fileSystem = fs;

            var current = fs instanceof ConnectionFileSystem connectionFileSystem
                    ? connectionFileSystem
                            .getShellProcessControl()
                            .executeStringSimpleCommand(connectionFileSystem
                                    .getShellProcessControl()
                                    .getShellType()
                                    .getPrintWorkingDirectoryCommand())
                    : null;
            cdSync(current);
        });
    }

    public void switchAsync(FileSystemStore fileSystem) {
        ThreadHelper.runFailableAsync(() -> {
            switchSync(fileSystem);
        });
    }

    public void openTerminalAsync(String directory) {
        ThreadHelper.runFailableAsync(() -> {
            BusyProperty.execute(busy, () -> {
                if (store.getValue() instanceof ShellStore s) {
                    var connection = ((ConnectionFileSystem) fileSystem).getShellProcessControl();
                    var command = s.create()
                            .initWith(List.of(connection.getShellType().getCdCommand(directory)))
                            .prepareTerminalOpen();
                    TerminalHelper.open("", command);
                }
            });
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    public ReadOnlyObjectProperty<String> currentPathProperty() {
        return currentPath.getReadOnlyProperty();
    }

    public NavigationHistory getHistory() {
        return history;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void back() {
        history.back().ifPresent(currentPath::set);
    }

    public void forth() {
        history.forth().ifPresent(currentPath::set);
    }

    public void navigate(String path, boolean saveInHistory) {
        currentPath.set(path);
    }
}
