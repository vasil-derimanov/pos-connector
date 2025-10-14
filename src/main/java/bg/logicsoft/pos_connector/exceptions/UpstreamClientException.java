package bg.logicsoft.pos_connector.exceptions;

import lombok.Getter;

@Getter
public class UpstreamClientException extends RuntimeException {
    private final int statusCode;

    public UpstreamClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
