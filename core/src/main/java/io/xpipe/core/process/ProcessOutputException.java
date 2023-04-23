package io.xpipe.core.process;

import lombok.Getter;

@Getter
public class ProcessOutputException extends Exception {

    public static ProcessOutputException of(String customPrefix, ProcessOutputException ex) {
        var messageSuffix = ex.getOutput() != null && ! ex.getOutput().isBlank()?": " +  ex.getOutput() : "";
        var message = customPrefix + messageSuffix;
        return new ProcessOutputException(message, ex.getExitCode(), ex.getOutput());
    }

    public static ProcessOutputException of(int exitCode, String output, String accumulatedError) {
        var combinedError = (accumulatedError != null ? accumulatedError.trim() + "\n" : "") + (output != null ? output.trim() : "");
        var message = switch (exitCode) {
            case CommandControl.KILLED_EXIT_CODE -> "Process timed out" + combinedError;
            case CommandControl.TIMEOUT_EXIT_CODE -> "Process timed out" + combinedError;
            default -> "Process returned with exit code " + combinedError;
        };
        return new ProcessOutputException(message, exitCode, combinedError);
    }

    private final int exitCode;
    private final String output;

    private  ProcessOutputException(String message, int exitCode, String output) {
        super(message);
        this.exitCode = exitCode;
        this.output = output;
    }

    public boolean isTimeOut() {
        return exitCode  == CommandControl.TIMEOUT_EXIT_CODE;
    }

    public boolean isKill() {
        return exitCode  == CommandControl.KILLED_EXIT_CODE;
    }
}
