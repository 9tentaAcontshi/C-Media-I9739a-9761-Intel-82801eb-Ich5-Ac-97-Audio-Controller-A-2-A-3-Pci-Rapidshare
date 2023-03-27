package io.xpipe.core.process;

import io.xpipe.core.util.FailableBiFunction;
import io.xpipe.core.util.FailableFunction;
import io.xpipe.core.util.SecretValue;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ShellControl extends ProcessControl {

    Semaphore getCommandLock();

    void onInit(Consumer<ShellControl> pc);

    String prepareTerminalOpen() throws Exception;

    String prepareIntermediateTerminalOpen(String content) throws Exception;

    String getTemporaryDirectory() throws Exception;

    public void checkRunning() throws Exception;

    default String executeStringSimpleCommand(String command) throws Exception {
        try (CommandControl c = command(command).start()) {
            return c.readOrThrow();
        }
    }

    default boolean executeBooleanSimpleCommand(String command) throws Exception {
        try (CommandControl c = command(command).start()) {
            return c.discardAndCheckExit();
        }
    }

    default void executeSimpleCommand(String command) throws Exception {
        try (CommandControl c = command(command).start()) {
            c.discardOrThrow();
        }
    }

    default void executeSimpleCommand(String command, String failMessage) throws Exception {
        try (CommandControl c = command(command).start()) {
            c.discardOrThrow();
        } catch (ProcessOutputException out) {
            throw ProcessOutputException.of(failMessage, out);
        }
    }

    default String executeStringSimpleCommand(ShellDialect type, String command) throws Exception {
        try (var sub = subShell(type).start()) {
            return sub.executeStringSimpleCommand(command);
        }
    }

    void restart() throws Exception;

    boolean isLocal();

    OsType getOsType();

    ShellControl elevated(FailableFunction<ShellControl, Boolean, Exception> elevationFunction);

    ShellControl elevationPassword(SecretValue value);

    ShellControl initWith(List<String> cmds);

    SecretValue getElevationPassword();

    default ShellControl subShell(@NonNull ShellDialect type) {
        return subShell(p -> type.getOpenCommand(), null).elevationPassword(getElevationPassword());
    }

    default ShellControl identicalSubShell() {
        return subShell(p -> p.getShellDialect().getOpenCommand(), null)
                .elevationPassword(getElevationPassword());
    }

    default ShellControl subShell(@NonNull String command) {
        return subShell(processControl -> command, null);
    }

    default <T> T enforceDialect(@NonNull ShellDialect type, Function<ShellControl, T> sc) throws Exception {
        if (isRunning() && getShellDialect().equals(type))  {
            return sc.apply(this);
        } else {
            try (var sub = subShell(type).start()) {
                return sc.apply(sub);
            }
        }
    }

    ShellControl subShell(
            FailableFunction<ShellControl, String, Exception> command,
            FailableBiFunction<ShellControl, String, String, Exception> terminalCommand);

    void executeLine(String command) throws Exception;

    void cd(String directory) throws Exception;

    @Override
    ShellControl start() throws Exception;

    CommandControl command(FailableFunction<ShellControl, String, Exception> command);

    CommandControl command(
            FailableFunction<ShellControl, String, Exception> command,
            FailableFunction<ShellControl, String, Exception> terminalCommand);

    default CommandControl command(String... command) {
        var c = Arrays.stream(command).filter(s -> s != null).toArray(String[]::new);
        return command(shellProcessControl -> String.join("\n", c));
    }

    default CommandControl command(List<String> command) {
        return command(
                shellProcessControl -> shellProcessControl.getShellDialect().flatten(command));
    }

    void exitAndWait() throws IOException;
}
