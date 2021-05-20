package server.util;

import shared.serializable.ClientRequest;
import shared.serializable.ServerResponse;

public class ResponseWrapper implements Runnable {

    private final ClientRequest requestToProcess;
    private final CommandWrapper commandWrapper;
    private final boolean isTechnical;
    private ServerResponse computedResponse;

    public ResponseWrapper(ClientRequest request, CommandWrapper commandWrapper, boolean isTechnical) {
        this.requestToProcess = request;
        this.commandWrapper = commandWrapper;
        this.isTechnical = isTechnical;
    }

    @Override
    public void run() {
        computedResponse = commandWrapper.processRequest(requestToProcess, isTechnical);
    }

    public ServerResponse getComputedResponse() {
        return computedResponse;
    }
}
