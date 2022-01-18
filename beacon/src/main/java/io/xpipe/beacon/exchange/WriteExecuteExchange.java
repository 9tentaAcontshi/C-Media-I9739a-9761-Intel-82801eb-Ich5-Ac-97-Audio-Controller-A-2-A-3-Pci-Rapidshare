package io.xpipe.beacon.exchange;

import io.xpipe.beacon.message.RequestMessage;
import io.xpipe.beacon.message.ResponseMessage;
import io.xpipe.core.source.DataSourceConfigInstance;
import io.xpipe.core.source.DataSourceId;
import io.xpipe.core.store.DataStore;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public class WriteExecuteExchange implements MessageExchange<WriteExecuteExchange.Request, WriteExecuteExchange.Response> {

    @Override
    public String getId() {
        return "writeExecute";
    }

    @Override
    public Class<WriteExecuteExchange.Request> getRequestClass() {
        return WriteExecuteExchange.Request.class;
    }

    @Override
    public Class<WriteExecuteExchange.Response> getResponseClass() {
        return WriteExecuteExchange.Response.class;
    }

    @Jacksonized
    @Builder
    @Value
    public static class Request implements RequestMessage {
        @NonNull
        DataSourceId sourceId;
        @NonNull
        DataStore dataStore;
        @NonNull
        DataSourceConfigInstance config;
    }

    @Jacksonized
    @Builder
    @Value
    public static class Response implements ResponseMessage {
    }
}
