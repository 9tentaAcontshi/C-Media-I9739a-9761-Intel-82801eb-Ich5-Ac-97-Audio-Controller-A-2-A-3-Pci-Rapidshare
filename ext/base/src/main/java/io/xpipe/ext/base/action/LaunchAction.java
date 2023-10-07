package io.xpipe.ext.base.action;

import io.xpipe.app.core.AppI18n;
import io.xpipe.app.ext.ActionProvider;
import io.xpipe.app.storage.DataStorage;
import io.xpipe.app.storage.DataStoreEntry;
import io.xpipe.app.util.TerminalHelper;
import io.xpipe.core.store.LaunchableStore;
import io.xpipe.core.store.ShellStore;
import io.xpipe.ext.base.script.ScriptStore;
import javafx.beans.value.ObservableValue;
import lombok.Value;

public class LaunchAction implements ActionProvider {

    @Value
    static class Action implements ActionProvider.Action {

        DataStoreEntry entry;

        @Override
        public boolean requiresJavaFXPlatform() {
            return false;
        }

        @Override
        public void execute() throws Exception {
            var storeName = entry.getName();
            if (entry.getStore() instanceof ShellStore s) {
                TerminalHelper.open(entry, storeName, ScriptStore.controlWithDefaultScripts(s.control()));
                return;
            }

            if (entry.getStore() instanceof LaunchableStore s) {
                var command = s.prepareLaunchCommand();
                if (command == null) {
                    return;
                }

                TerminalHelper.open(entry, storeName, command);
            }
        }
    }

    @Override
    public DataStoreCallSite<?> getDataStoreCallSite() {
        return new DataStoreCallSite<LaunchableStore>() {

            @Override
            public boolean canLinkTo() {
                return true;
            }

            @Override
            public boolean isApplicable(LaunchableStore o) {
                return DataStorage.get()
                        .getStoreEntryIfPresent(o)
                        .orElseThrow()
                        .getValidity()
                        .isUsable() && o.canLaunch();
            }

            @Override
            public ActionProvider.Action createAction(LaunchableStore store) {
                return new Action(
                        DataStorage.get().getStoreEntryIfPresent(store).orElseThrow());
            }

            @Override
            public Class<LaunchableStore> getApplicableClass() {
                return LaunchableStore.class;
            }

            @Override
            public ObservableValue<String> getName(LaunchableStore store) {
                return AppI18n.observable("launch");
            }

            @Override
            public String getIcon(LaunchableStore store) {
                return "mdi2p-play";
            }
        };
    }

    @Override
    public String getId() {
        return "launch";
    }

    @Override
    public DefaultDataStoreCallSite<?> getDefaultDataStoreCallSite() {
        return new DefaultDataStoreCallSite<LaunchableStore>() {

            @Override
            public boolean isApplicable(LaunchableStore o) {
                return DataStorage.get()
                        .getStoreEntryIfPresent(o)
                        .orElseThrow()
                        .getValidity()
                        .isUsable() && o.canLaunch();
            }

            @Override
            public ActionProvider.Action createAction(LaunchableStore store) {
                return new Action(DataStorage.get().getStoreEntryIfPresent(store).orElseThrow());
            }

            @Override
            public Class<LaunchableStore> getApplicableClass() {
                return LaunchableStore.class;
            }
        };
    }
}
