package io.xpipe.core.store;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

@JsonTypeName("local")
public class LocalFileDataInput extends FileDataInput {

    private final Path file;

    @JsonCreator
    public LocalFileDataInput(Path file) {
        this.file = file;
    }

    @Override
    public Optional<String> determineDefaultName() {
        return Optional.of(FilenameUtils.getBaseName(file.toString()));
    }

    @Override
    public Optional<Instant> getLastModified() {
        try {
            var l = Files.getLastModifiedTime(file);
            return Optional.of(l.toInstant());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public String getName() {
        return file.getFileName().toString();
    }

    @Override
    @JsonIgnore
    public boolean isLocal() {
        return true;
    }

    @Override
    public LocalFileDataInput getLocal() {
        return this;
    }

    @Override
    public RemoteFileDataInput getRemote() {
        throw new UnsupportedOperationException();
    }

    public Path getFile() {
        return file;
    }

    @Override
    public InputStream openInput() throws Exception {
        return Files.newInputStream(file);
    }

    @Override
    public OutputStream openOutput() throws Exception {
        return Files.newOutputStream(file);
    }
}
