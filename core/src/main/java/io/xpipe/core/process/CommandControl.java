package io.xpipe.core.process;

import io.xpipe.core.charsetter.Charsetter;
import io.xpipe.core.util.FailableFunction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public interface CommandControl extends ProcessControl {

    public CommandControl doesNotObeyReturnValueConvention();

    @Override
    public CommandControl sensitive();

    CommandControl complex();

    CommandControl workingDirectory(String directory);

    ShellControl getParent();

    InputStream startExternalStdout() throws Exception;

    OutputStream startExternalStdin() throws Exception;

    public boolean waitFor();

    CommandControl customCharset(Charset charset);

    int getExitCode();

    default CommandControl elevated() {
        return elevated((v) -> true);
    }

    CommandControl elevated(FailableFunction<ShellControl, Boolean, Exception> elevationFunction);

    @Override
    CommandControl start() throws Exception;

    CommandControl exitTimeout(Integer timeout);

    public void withStdoutOrThrow(Charsetter.FailableConsumer<InputStreamReader, Exception> c) throws Exception;
    String readOnlyStdout() throws Exception;

    public void discardOrThrow() throws Exception;

    void accumulateStdout(Consumer<String> con);

    void accumulateStderr(Consumer<String> con);

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
