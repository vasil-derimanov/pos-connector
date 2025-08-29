package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceDTO;
import bg.logicsoft.pos_connector.exceptions.UpstreamClientException;
import bg.logicsoft.pos_connector.exceptions.UpstreamServerException;
import bg.logicsoft.pos_connector.exceptions.UpstreamTimeoutException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class ERPNextService {

    @Getter
    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    public ERPNextService(AppProperties appProperties, RestTemplateBuilder restTemplateBuilder) {
        this.appProperties = appProperties;
        // Configure timeouts; tune as needed
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15)).build();
    }

    public Map<String, Object> sendSalesInvoiceToERPNext(ERPNextSalesInvoiceDTO invoice) {
        String url = appProperties.getErpNextUrl() + "/api/resource/Sales Invoice";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Use configured API credentials, do NOT log them
        headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

        HttpEntity<ERPNextSalesInvoiceDTO> request = new HttpEntity<>(invoice, headers);

        try {
            log.info("ERPNext POST start: path=/api/resource/Sales Invoice");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            log.info("ERPNext POST done: status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();
            String body = sanitizeForLog(ex.getResponseBodyAsString());

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response: status={}, body={}", status.value(), body);
                throw new UpstreamServerException("ERPNext server error", status.value());
            } else {
                log.warn("ERPNext 4xx response: status={}, body={}", status.value(), body);
                throw new UpstreamClientException("ERPNext rejected request", status.value());
            }
        } catch (ResourceAccessException ex) {
            // Typically timeouts / connection issues
            log.warn("ERPNext access error (timeout/connection): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext: {}", rootMessage(ex), ex);
            throw ex; // Will be handled by GlobalExceptionHandler
        }
    }

    private String sanitizeForLog(String s) {
        if (s == null) return null;
        String oneLine = s.replaceAll("[\\r\\n]+", " ");
        // Basic masking: avoid accidental leak of token-like values
        oneLine = oneLine.replaceAll("(?i)(token|secret|password)\\s*[:=]\\s*[^,\\s}{\"]+", "***");
        return oneLine.length() > 1000 ? oneLine.substring(0, 1000) + "..." : oneLine;
    }

    private String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage();
    }
}