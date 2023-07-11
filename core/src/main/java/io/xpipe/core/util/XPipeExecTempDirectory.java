package io.xpipe.core.util;

import io.xpipe.core.impl.FileNames;
import io.xpipe.core.process.OsType;
import io.xpipe.core.process.ShellControl;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

public class XPipeExecTempDirectory {

    private static final Set<UUID> usedSystems = new CopyOnWriteArraySet<>();

    public static String getSystemTempDirectory(ShellControl proc) throws Exception {
        return proc.getOsType().getTempDirectory(proc);
    }

    public static synchronized String initXPipeTempDirectory(ShellControl proc) throws Exception {
        var base = proc.getOsType().getTempDirectory(proc);
        var arr = Stream.of(base, "xpipe", "exec").toArray(String[]::new);
        var dir = FileNames.join(arr);

        var d = proc.getShellDialect();
        if (proc.getOsType() == OsType.LINUX
                && !proc.executeSimpleBooleanCommand("test -r %s && test -w %s && test -x %s"
                        .formatted(d.fileArgument(base), d.fileArgument(base), d.fileArgument(base)))) {
            dir = FileNames.join(Stream.of(proc.getOsType().getHomeDirectory(proc), "xpipe", "exec")
                    .toArray(String[]::new));
        }

        // We don't want to modify the temp directory if it is possibly in use
        if (usedSystems.contains(proc.getSystemId())) {
            return dir;
        }

        var existsCommand = d.directoryExists(proc, dir);
        if (existsCommand.executeAndCheck() && !usedSystems.contains(proc.getSystemId())) {
            proc.executeSimpleCommand(d.getFileDeleteCommand(dir));
        }

        d.prepareTempDirectory(proc, dir).execute();
        usedSystems.add(proc.getSystemId());

        return dir;
    }

    public static synchronized void occupyXPipeTempDirectory(ShellControl proc) {
        usedSystems.add(proc.getSystemId());
    }

    public static String getSubDirectory(ShellControl proc, String... sub) throws Exception {
        var base = proc.getOsType().getTempDirectory(proc);
        var arr = Stream.concat(Stream.of(base, "xpipe", "exec"), Arrays.stream(sub))
                .toArray(String[]::new);
        var dir = FileNames.join(arr);

        var existsCommand = proc.getShellDialect().createFileExistsCommand(proc, dir);
        if (!existsCommand.executeAndCheck()) {
            proc.getShellDialect().prepareTempDirectory(proc, dir).execute();
        }

        return dir;
    }
}
