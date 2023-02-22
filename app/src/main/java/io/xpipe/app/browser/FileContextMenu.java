/* SPDX-License-Identifier: MIT */

package io.xpipe.app.browser;

import io.xpipe.app.util.*;
import io.xpipe.core.process.OsType;
import io.xpipe.core.process.ShellProcessControl;
import io.xpipe.core.store.FileSystem;
import javafx.beans.property.Property;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.apache.commons.io.FilenameUtils;

import java.util.List;

final class FileContextMenu extends ContextMenu {

    public boolean isExecutable(FileSystem.FileEntry e) {
        if (e.isDirectory()) {
            return false;
        }

        if (e.getExecutable() != null && e.getExecutable()) {
            return true;
        }

        var shell = e.getFileSystem().getShell();
        if (shell.isEmpty()) {
            return false;
        }

        var os = shell.get().getOsType();
        var ending = FilenameUtils.getExtension(e.getPath()).toLowerCase();
        if (os.equals(OsType.WINDOWS) && List.of("exe", "bat", "ps1", "cmd").contains(ending)) {
            return true;
        }

        if (List.of("sh", "command").contains(ending)) {
            return true;
        }

        return false;
    }

    private final OpenFileSystemModel model;
    private final FileSystem.FileEntry entry;
    private final Property<String> editing;

    public FileContextMenu(OpenFileSystemModel model, FileSystem.FileEntry entry, Property<String> editing) {
        super();
        this.model = model;
        this.entry = entry;
        this.editing = editing;
        createMenu();
    }

    private void createMenu() {
        if (entry.isDirectory()) {
            var terminal = new MenuItem("Open terminal");
            terminal.setOnAction(event -> {
                event.consume();
                model.openTerminalAsync(entry.getPath());
            });
            getItems().add(terminal);
        } else {
            if (isExecutable(entry)) {
                var execute = new MenuItem("Run in terminal");
                execute.setOnAction(event -> {
                    ThreadHelper.runFailableAsync(() -> {
                        ShellProcessControl pc =
                                model.getFileSystem().getShell().orElseThrow();
                        pc.executeSimpleCommand(pc.getShellDialect().getMakeExecutableCommand(entry.getPath()));
                        var cmd = pc.command("\"" + entry.getPath() + "\"").prepareTerminalOpen();
                        TerminalHelper.open(FilenameUtils.getBaseName(entry.getPath()), cmd);
                    });
                    event.consume();
                });
                getItems().add(execute);

                var executeInBackground = new MenuItem("Run in background");
                executeInBackground.setOnAction(event -> {
                    ThreadHelper.runFailableAsync(() -> {
                        ShellProcessControl pc =
                                model.getFileSystem().getShell().orElseThrow();
                        pc.executeSimpleCommand(pc.getShellDialect().getMakeExecutableCommand(entry.getPath()));
                        var cmd = ScriptHelper.createDetachCommand(pc, "\"" + entry.getPath() + "\"");
                        pc.executeBooleanSimpleCommand(cmd);
                    });
                    event.consume();
                });
                getItems().add(executeInBackground);
            } else {
                var open = new MenuItem("Open default");
                open.setOnAction(event -> {
                    ThreadHelper.runFailableAsync(() -> {
                        FileOpener.openInDefaultApplication(entry);
                    });
                    event.consume();
                });
                getItems().add(open);
            }

            var edit = new MenuItem("Edit");
            edit.setOnAction(event -> {
                FileOpener.openInTextEditor(entry);
                event.consume();
            });
            getItems().add(edit);
        }

        var cut = new MenuItem("Delete");
        cut.setOnAction(event -> {
            event.consume();
            model.deleteAsync(entry.getPath());
        });
        cut.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        var rename = new MenuItem("Rename");
        rename.setOnAction(event -> {
            event.consume();
            editing.setValue(entry.getPath());
        });
        rename.setAccelerator(new KeyCodeCombination(KeyCode.F2));

        getItems().addAll(new SeparatorMenuItem(), cut, rename);
    }
}
