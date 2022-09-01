package io.xpipe.core.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.io.*;

/**
 * A store whose contents are stored in memory.
 */
@Value
@JsonTypeName("inMemory")
public class InMemoryStore implements StreamDataStore {

    @NonFinal
    byte[] value;

    public InMemoryStore() {
        this.value = new byte[0];
    }

    @JsonCreator
    public InMemoryStore(byte[] value) {
        this.value = value;
    }

    @Override
    public boolean isLocalToApplication() {
        return true;
    }

    @Override
    public InputStream openInput() throws Exception {
        return new ByteArrayInputStream(value);
    }

    @Override
    public OutputStream openOutput() throws Exception {
        return new ByteArrayOutputStream(){
            @Override
            public void close() throws IOException {
                super.close();
                InMemoryStore.this.value = this.toByteArray();
            }
        };
    }

    @Override
    public String toSummaryString() {
        return "inMemory";
    }
}
