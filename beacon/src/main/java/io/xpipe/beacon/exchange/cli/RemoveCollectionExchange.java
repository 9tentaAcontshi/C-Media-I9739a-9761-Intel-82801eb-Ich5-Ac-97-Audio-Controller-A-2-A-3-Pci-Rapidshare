package io.xpipe.beacon.exchange.cli;

import io.xpipe.beacon.exchange.MessageExchange;
import io.xpipe.beacon.message.RequestMessage;
import io.xpipe.beacon.message.ResponseMessage;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public class RemoveCollectionExchange implements MessageExchange {

    @Override
    public String getId() {
        return "removeCollection";
    }

    @Jacksonized
    @Builder
    @Value
    public static class Request implements RequestMessage {
        @NonNull
        String collectionName;
    }

    @Jacksonized
    @Builder
    @Value
    public static class Response implements ResponseMessage {
    }
}
