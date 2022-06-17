package io.xpipe.beacon.exchange;

import io.xpipe.beacon.RequestMessage;
import io.xpipe.beacon.ResponseMessage;
import io.xpipe.core.dialog.DialogReference;
import io.xpipe.core.store.DataStore;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Prepares a client to send stream-based data to a daemon.
 */
public class ReadPreparationExchange implements MessageExchange {

    @Override
    public String getId() {
        return "readPreparation";
    }

    @Jacksonized
    @Builder
    @Value
    public static class Request implements RequestMessage {
        String provider;

        @NonNull
        DataStore store;

        boolean configureAll;
    }

    @Jacksonized
    @Builder
    @Value
    public static class Response implements ResponseMessage {
        String determinedType;

        @NonNull
        DialogReference config;
    }
}
