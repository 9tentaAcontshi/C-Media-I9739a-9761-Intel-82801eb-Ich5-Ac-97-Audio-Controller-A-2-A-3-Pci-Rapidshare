package io.xpipe.core.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.xpipe.core.charsetter.Charsettable;
import io.xpipe.core.charsetter.NewLine;
import io.xpipe.core.charsetter.StreamCharset;
import io.xpipe.core.source.TextDataSource;
import io.xpipe.core.store.StreamDataStore;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@JsonTypeName("text")
@SuperBuilder
@Jacksonized
public final class TextSource extends TextDataSource<StreamDataStore> implements Charsettable {

    private final StreamCharset charset;
    private final NewLine newLine;

    @Override
    protected io.xpipe.core.source.TextWriteConnection newWriteConnection() {
        return new TextWriteConnection(this);
    }

    @Override
    protected io.xpipe.core.source.TextWriteConnection newPrependingWriteConnection() {
        return new PreservingTextWriteConnection(this, newWriteConnection(), false);
    }

    @Override
    protected io.xpipe.core.source.TextWriteConnection newAppendingWriteConnection() {
        return new PreservingTextWriteConnection(this, newWriteConnection(), true);
    }

    @Override
    protected io.xpipe.core.source.TextReadConnection newReadConnection() {
        return new TextReadConnection(this);
    }
}
