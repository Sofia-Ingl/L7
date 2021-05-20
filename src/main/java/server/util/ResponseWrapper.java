package server.util;

import shared.serializable.ClientRequest;
import shared.serializable.ServerResponse;

public class ResponseWrapper implements Runnable {

    private final ClientRequest requestToProcess;
    private final RequestProcessor requestProcessor;
    private final boolean isTechnical;
    private ServerResponse computedResponse;

    public ResponseWrapper(ClientRequest request, RequestProcessor requestProcessor, boolean isTechnical) {
        this.requestToProcess = request;
        this.requestProcessor = requestProcessor;
        this.isTechnical = isTechnical;
    }

    @Override
    public void run() {
        if (isTechnical) {
            computedResponse = requestProcessor.processTechnicalRequests(requestToProcess);
        } else {
            computedResponse = requestProcessor.processRequest(requestToProcess);
        }
    }

    public ServerResponse getComputedResponse() {
        return computedResponse;
    }
}
