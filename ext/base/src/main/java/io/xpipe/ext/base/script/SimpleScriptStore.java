package io.xpipe.ext.base.script;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.xpipe.app.storage.DataStoreEntryRef;
import io.xpipe.app.util.ScriptHelper;
import io.xpipe.app.util.Validators;
import io.xpipe.core.process.ShellControl;
import io.xpipe.core.process.ShellDialect;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.LinkedHashSet;
import java.util.List;

@SuperBuilder
@Getter
@Jacksonized
@JsonTypeName("script")
public class SimpleScriptStore extends ScriptStore {

    public String prepareDumbScript(ShellControl shellControl) {
        return assemble(shellControl, ExecutionType.DUMB_ONLY);
    }

    public String prepareTerminalScript(ShellControl shellControl) {
        return assemble(shellControl, ExecutionType.TERMINAL_ONLY);
    }

    private String assemble(
            ShellControl shellControl, ExecutionType type) {
        if ((executionType == type || executionType == ExecutionType.BOTH)
                && minimumDialect.isCompatibleTo(shellControl.getShellDialect())) {
            var script = ScriptHelper.createExecScript(minimumDialect, shellControl, commands);
            return shellControl.getShellDialect().sourceScriptCommand(shellControl, script);
        }

        return null;
    }

    @Override
    public List<DataStoreEntryRef<ScriptStore>> getEffectiveScripts() {
        return scripts != null
                ? scripts.stream().filter(scriptStore -> scriptStore != null).toList()
                : List.of();
    }

    public void queryFlattenedScripts(LinkedHashSet<SimpleScriptStore> all) {
        getEffectiveScripts().stream().filter(scriptStoreDataStoreEntryRef -> !all.contains(scriptStoreDataStoreEntryRef.getStore())).forEach(scriptStoreDataStoreEntryRef -> {
            scriptStoreDataStoreEntryRef.getStore().queryFlattenedScripts(all);
        });
        all.add(this);
    }

    @Getter
    public enum ExecutionType {
        @JsonProperty("dumbOnly")
        DUMB_ONLY("dumbOnly"),
        @JsonProperty("terminalOnly")
        TERMINAL_ONLY("terminalOnly"),
        @JsonProperty("both")
        BOTH("both");

        private final String id;

        ExecutionType(String id) {
            this.id = id;
        }
    }

    private final ShellDialect minimumDialect;
    private final String commands;
    private final ExecutionType executionType;
    private final boolean requiresElevation;

    @Override
    public void checkComplete() throws Exception {
        super.checkComplete();
        Validators.nonNull(executionType);
        Validators.nonNull(minimumDialect);
    }
}
