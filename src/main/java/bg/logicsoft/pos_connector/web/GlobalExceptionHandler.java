// Java
package bg.logicsoft.pos_connector.web;

import bg.logicsoft.pos_connector.exceptions.UpstreamClientException;
import bg.logicsoft.pos_connector.exceptions.UpstreamServerException;
import bg.logicsoft.pos_connector.exceptions.UpstreamTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation failed: {} errors", ex.getBindingResult().getErrorCount());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", "Request validation failed",
                "details", ex.getBindingResult().getAllErrors().stream()
                        .map(err -> err.getDefaultMessage()).toList(),
                "original_message", ex.getMessage()
        ));
    }

    @ExceptionHandler(UpstreamClientException.class)
    public ResponseEntity<Map<String, Object>> handleUpstream4xx(UpstreamClientException ex) {
        log.warn("Upstream client error: status={}, message={}", ex.getStatusCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "error", "UPSTREAM_CLIENT_ERROR",
                "message", "Request was rejected by upstream service",
                "original_message", ex.getMessage()
        ));
    }

    @ExceptionHandler(UpstreamServerException.class)
    public ResponseEntity<Map<String, Object>> handleUpstream5xx(UpstreamServerException ex) {
        log.error("Upstream server error: status={}, message={}", ex.getStatusCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                "error", "UPSTREAM_SERVER_ERROR",
                "message", "Upstream service failed",
                "source","GlobalExceptionHandler",
                "original_message", ex.getMessage()
        ));
    }

    @ExceptionHandler(UpstreamTimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(UpstreamTimeoutException ex) {
        log.warn("Upstream timeout: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(Map.of(
                "error", "UPSTREAM_TIMEOUT",
                "message", "Upstream service timed out",
                "source","GlobalExceptionHandler",
                "original_message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "BAD_REQUEST",
                "message", ex.getMessage(),
                "source","GlobalExceptionHandler",
                "original_message", ex.getMessage()
        ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(
                Map.of(
                        "error", "MISSING_PARAMETER",
                        "message", "Required parameter '" + ex.getParameterName() + "' is missing",
                        "source","GlobalExceptionHandler",
                        "original_message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Unhandled exception in 'GlobalExceptionHandler'", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Unhandled exception in 'GlobalExceptionHandler'",
                "source","GlobalExceptionHandler",
                "original_message", ex.getMessage()
        ));
    }
}
