package io.xpipe.ext.base.actions;

import io.xpipe.core.store.StreamDataStore;
import io.xpipe.extension.I18n;
import io.xpipe.extension.event.ErrorEvent;
import io.xpipe.extension.util.ActionProvider;
import io.xpipe.extension.util.DesktopHelper;
import io.xpipe.extension.util.ThreadHelper;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import lombok.Value;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class StreamExportAction implements ActionProvider {

    @Value
    static class Action implements ActionProvider.Action {

        StreamDataStore store;

        @Override
        public boolean requiresPlatform() {
            return false;
        }

        @Override
        public void execute() throws Exception {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18n.get("browseFileTitle"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(I18n.get("anyFile"), "."));
            var outputFile = fileChooser.showSaveDialog(null);
            if (outputFile == null) {
                return;
            }

            ThreadHelper.runAsync(() -> {
                try (InputStream inputStream = store.openInput()) {
                    try (OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {
                        inputStream.transferTo(outputStream);
                    }
                    DesktopHelper.browseFileInDirectory(outputFile.toPath());
                } catch (Exception e) {
                    ErrorEvent.fromThrowable(e).handle();
                }
            });
        }
    }

    @Override
    public DataStoreCallSite<?> getDataStoreCallSite() {
        return new DataStoreCallSite<StreamDataStore>() {

            @Override
            public Action createAction(StreamDataStore store) {
                return new Action(store);
            }

            @Override
            public boolean isApplicable(StreamDataStore o) throws Exception {
                return o.getFlow() != null && o.getFlow().hasInput();
            }

            @Override
            public Class<StreamDataStore> getApplicableClass() {
                return StreamDataStore.class;
            }

            @Override
            public ObservableValue<String> getName(StreamDataStore store) {
                return I18n.observable("base.exportStream");
            }

            @Override
            public String getIcon(StreamDataStore store) {
                return "mdi2f-file-export-outline";
            }
        };
    }
}