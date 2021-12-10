package io.xpipe.beacon.exchange;

import io.xpipe.beacon.message.RequestMessage;
import io.xpipe.beacon.message.ResponseMessage;

import java.util.List;

public class ListCollectionsExchange implements MessageExchange<ListCollectionsExchange.Request, ListCollectionsExchange.Response> {

    @Override
    public String getId() {
        return "listCollections";
    }

    @Override
    public Class<Request> getRequestClass() {
        return Request.class;
    }

    @Override
    public Class<Response> getResponseClass() {
        return Response.class;
    }

    public static record Request() implements RequestMessage {

    }

    public static record Entry(String name, int count) {

    }

    public static record Response(List<Entry> entries) implements ResponseMessage {

    }
}
