package io.xpipe.core.process;

import io.xpipe.core.charsetter.Charsetter;
import io.xpipe.core.util.FailableFunction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;

public interface CommandControl extends ProcessControl {

    public static final int UNASSIGNED_EXIT_CODE = -1;
    public static final int EXIT_TIMEOUT_EXIT_CODE = -2;
    public static final int START_FAILED_EXIT_CODE = -3;

    static enum TerminalExitMode {
        KEEP_OPEN,
        CLOSE
    }

    CommandControl withMessageFormatter(Function<String, String> formatter);

    CommandControl terminalExitMode(TerminalExitMode mode);

    public CommandControl doesNotObeyReturnValueConvention();

    @Override
    public CommandControl sensitive();

    CommandControl complex();

    CommandControl notComplex();

    CommandControl workingDirectory(String directory);

    default void execute() throws Exception {
        try (var c = start()) {
            c.discardOrThrow();
        }
    }

    default boolean executeAndCheck() throws Exception {
        try (var c = start()) {
            return c.discardAndCheckExit();
        }
    }

    ShellControl getParent();

    InputStream startExternalStdout() throws Exception;

    OutputStream startExternalStdin() throws Exception;

    public boolean waitFor();

    CommandControl customCharset(Charset charset);

    int getExitCode();

    default CommandControl elevated(String message) {
        return elevated(message, (v) -> true);
    }

    CommandControl elevated(String message, FailableFunction<ShellControl, Boolean, Exception> elevationFunction);

    @Override
    CommandControl start() throws Exception;

    CommandControl exitTimeout(Integer timeout);

    public void withStdoutOrThrow(Charsetter.FailableConsumer<InputStreamReader, Exception> c) throws Exception;
    String readStdoutDiscardErr() throws Exception;

    public void discardOrThrow() throws Exception;

    void accumulateStdout(Consumer<String> con);

    void accumulateStderr(Consumer<String> con);

    public byte[] readRawBytesOrThrow() throws Exception;

    public String readOrThrow() throws Exception;

    public default boolean discardAndCheckExit() throws ProcessOutputException {
        try {
            discardOrThrow();
            return true;
        }  catch (ProcessOutputException ex) {
            if (ex.isTimeOut()) {
                throw ex;
            }

            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    void discardOut();

    void discardErr();
}
