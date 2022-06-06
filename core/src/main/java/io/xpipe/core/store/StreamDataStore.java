package io.xpipe.core.store;

import lombok.NonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A data store that can be accessed using InputStreams and/or OutputStreams.
 * These streams must support mark/reset.
 */
public interface StreamDataStore extends DataStore {

    static Optional<StreamDataStore> fromString(@NonNull String s) {
        try {
            var path = Path.of(s);
            return Optional.of(new LocalFileDataStore(path));
        } catch (InvalidPathException ignored) {
        }

        try {
            var path = new URL(s);
        } catch (MalformedURLException ignored) {
        }

        return Optional.empty();
    }

    /**
     * Opens an input stream. This input stream does not necessarily have to be a new instance.
     */
    default InputStream openInput() throws Exception {
        throw new UnsupportedOperationException("Can't open store input");
    }

    /**
     * Opens an output stream. This output stream does not necessarily have to be a new instance.
     */
    default OutputStream openOutput() throws Exception {
        throw new UnsupportedOperationException("Can't open store output");
    }

    default OutputStream openAppendingOutput() throws Exception {
        throw new UnsupportedOperationException("Can't open store output");
    }

    boolean exists();

    default boolean persistent() {
        return false;
    }
}
