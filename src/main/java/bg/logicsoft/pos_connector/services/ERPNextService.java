package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.ERPNextCustomersDTO;
import bg.logicsoft.pos_connector.dto.ERPNextItemsPriceDTO;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceDTO;
import bg.logicsoft.pos_connector.exceptions.UpstreamClientException;
import bg.logicsoft.pos_connector.exceptions.UpstreamServerException;
import bg.logicsoft.pos_connector.exceptions.UpstreamTimeoutException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Arrays;
import java.util.List;
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
        String url = org.springframework.web.util.UriComponentsBuilder.fromHttpUrl(appProperties.getErpNextUrl())
                .pathSegment("api", "method", "frappe.client.insert")
                .build()
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Use configured API credentials, do NOT log them
        headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

        // Send via method endpoint to avoid DocType-in-path issues with spaces
        Map<String, Object> body = Map.of(
                "doctype", "Sales Invoice",
                "doc", invoice
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            log.info("ERPNext POST start: method=frappe.client.insert, doctype=Sales Invoice");
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
            String bodyStr = sanitizeForLog(ex.getResponseBodyAsString());

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response: status={}, body={}", status.value(), bodyStr);
                throw new UpstreamServerException("ERPNext server error", status.value());
            } else {
                log.warn("ERPNext 4xx response: status={}, body={}", status.value(), bodyStr);
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

    public Map<String, Object> submitSalesInvoice(String invoiceName) {
        String url = org.springframework.web.util.UriComponentsBuilder.fromHttpUrl(appProperties.getErpNextUrl())
                .pathSegment("api", "method", "frappe.client.submit")
                .build()
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

        // Submit via method endpoint to avoid DocType-in-path issues
        Map<String, Object> body = Map.of(
                "doctype", "Sales Invoice",
                "name", invoiceName
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            log.info("ERPNext POST submit start: method=frappe.client.submit, doctype=Sales Invoice, invoice={}", invoiceName);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            log.info("ERPNext POST submit done: status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();
            String bodyStr = sanitizeForLog(ex.getResponseBodyAsString());

            if (status.is5xxServerError()) {
                log.error("ERPNext submit 5xx response: status={}, body={}", status.value(), bodyStr);
                throw new UpstreamServerException("ERPNext server error (submit)", status.value());
            } else {
                log.warn("ERPNext submit 4xx response: status={}, body={}", status.value(), bodyStr);
                throw new UpstreamClientException("ERPNext rejected submit", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext submit access error (timeout/connection): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (submit)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error submitting ERPNext invoice: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    /**
     * Fetch item prices for the given price list and fields, mapped to ERPNextItemsPriceListDTO.
     */
    public ERPNextItemsPriceDTO getItemPrices(String priceList) {
        List<String> fields = Arrays.asList("name", "item_code", "item_name", "uom", "price_list_rate", "currency", "packing_unit");
        try {
            String base = appProperties.getErpNextUrl();

            String fieldsJson = toJson(fields);
            String filtersJson = toJson(
                    List.of(List.of("price_list", "=", priceList))
            );

            // Build URL to match Postman: encoded path, raw JSON in query
            String url = appProperties.getErpNextUrl()
                    + "/api/resource/Item Price"
                    + "?fields=" + fieldsJson
                    + "&filters=" + filtersJson;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/resource/Item Price, price_list={}", priceList);
            ResponseEntity<ERPNextItemsPriceDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ERPNextItemsPriceDTO.class
            );
            log.info("ERPNext GET done: status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();
            String body = sanitizeForLog(ex.getResponseBodyAsString());

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Item Price): status={}, body={}", status.value(), body);
                throw new UpstreamServerException("ERPNext server error (Item Price)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Item Price): status={}, body={}", status.value(), body);
                throw new UpstreamClientException("ERPNext rejected Item Price request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (Item Price): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (Item Price)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext Item Price: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    /**
     * Fetch customers with selected fields, mapped to ERPNextCustomersDTO.
     */
    public ERPNextCustomersDTO getCustomers() {
        List<String> fields = Arrays.asList("name", "customer_name", "customer_type", "tax_id", "primary_address");
        try {
            String fieldsJson = toJson(fields);

            // Build URL to match ERPNext expectations: encoded path, raw JSON in query
            String url = appProperties.getErpNextUrl()
                    + "/api/resource/Customer"
                    + "?fields=" + fieldsJson;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/resource/Customer");
            ResponseEntity<ERPNextCustomersDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ERPNextCustomersDTO.class
            );
            log.info("ERPNext GET done (Customer): status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();
            String body = sanitizeForLog(ex.getResponseBodyAsString());

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Customer): status={}, body={}", status.value(), body);
                throw new UpstreamServerException("ERPNext server error (Customer)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Customer): status={}, body={}", status.value(), body);
                throw new UpstreamClientException("ERPNext rejected Customer request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (Customer): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (Customer)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext Customer: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    // Helper to serialize query objects without forcing callers to handle checked exceptions
    private String toJson(Object value) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize query parameter to JSON", e);
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