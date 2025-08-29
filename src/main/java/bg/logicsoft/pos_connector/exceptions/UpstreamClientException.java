package bg.logicsoft.pos_connector.exceptions;

public class UpstreamClientException extends RuntimeException {
    private final int statusCode;
    public UpstreamClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    public int getStatusCode() { return statusCode; }
}
