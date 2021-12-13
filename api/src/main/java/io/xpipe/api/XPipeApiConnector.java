package io.xpipe.api;

import io.xpipe.beacon.*;
import io.xpipe.beacon.BeaconClient;

public abstract class XPipeApiConnector extends BeaconConnector {

    public void execute() {
        try {
            var socket = constructSocket();
            handle(socket);
        } catch (ConnectorException ce) {
            throw new XPipeException("Connection error: " + ce.getMessage());
        } catch (ClientException ce) {
            throw new XPipeException("Client error: " + ce.getMessage());
        } catch (ServerException se) {
            throw new XPipeException("Server error: " + se.getMessage());
        } catch (Throwable t) {
            throw new XPipeException("Unexpected error", t);
        }
    }

    protected abstract void handle(BeaconClient sc) throws Exception;

    @Override
    protected void waitForStartup() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public static interface Handler {

        void handle(BeaconClient sc) throws ClientException, ServerException;
    }
}
