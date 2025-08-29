// Java
package bg.logicsoft.pos_connector.exceptions;

import lombok.Getter;

@Getter
public class UpstreamServerException extends RuntimeException {
    private final int statusCode;

    public UpstreamServerException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
