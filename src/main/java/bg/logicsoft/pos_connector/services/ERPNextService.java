package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.config.ERPNextRuntimeProperties;
import bg.logicsoft.pos_connector.dto.*;
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
    private final ERPNextRuntimeProperties runtimeProperties;

    public ERPNextService(AppProperties appProperties,
                          RestTemplateBuilder restTemplateBuilder,
                          ERPNextRuntimeProperties runtimeProperties) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15)).build();

        this.runtimeProperties = runtimeProperties;
    }

    public ERPNextPOSProfileDTO getPOSProfile(String posProfileName) {
        try {
            String url = appProperties.getErpNextUrl()
                    + "/api/resource/POS Profile/" + posProfileName;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/resource/POS Profile/{}", posProfileName);
            ResponseEntity<ERPNextPOSProfileDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ERPNextPOSProfileDTO.class
            );
            log.info("ERPNext GET done (POS Profile): status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (POS Profile): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (POS Profile)", status.value());
            } else {
                log.warn("ERPNext 4xx response (POS Profile): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamClientException("ERPNext rejected POS Profile request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (POS Profile): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (POS Profile)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext POS Profile: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    public Map<String, Object> sendSalesInvoiceToERPNext(ERPNextSalesInvoiceDTO invoice) {
        String url = org.springframework.web.util.UriComponentsBuilder.fromHttpUrl(appProperties.getErpNextUrl())
                .pathSegment("api", "method", "frappe.client.insert")
                .build()
                .encode()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

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

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response: status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error", status.value());
            } else {
                log.warn("ERPNext 4xx response: status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamClientException("ERPNext rejected request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (timeout/connection): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext: {}", rootMessage(ex), ex);
            throw ex;
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

            if (status.is5xxServerError()) {
                log.error("ERPNext submit 5xx response: status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (submit)", status.value());
            } else {
                log.warn("ERPNext submit 4xx response: status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
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

    public ItemPricesDTO getItemPrices(String priceList) {
        try {
            String url = appProperties.getErpNextUrl()
                + "/api/method/custom_app.pos-api.get_item_prices.get_item_prices_with_vat"
                + "?price_list=" + priceList
                + "&company=" + runtimeProperties.getCompany();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/method/custom_app.pos-api.get_item_prices.get_item_prices_with_vat, price_list={}", priceList);
            ResponseEntity<ItemPricesDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ItemPricesDTO.class
            );
            log.info("ERPNext GET (Item Price) done: status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Item Price): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (Item Price)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Item Price): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
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

    public ERPNextItemTaxTemplatesDTO getItemTaxTemplates(String priceList) {
        try {
            String url = appProperties.getErpNextUrl()
                    + "/api/method/custom_app.pos-api.get_item_prices.get_item_tax_templates"
                    + "&company=" + runtimeProperties.getCompany();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/method/custom_app.pos-api.get_item_prices_with_vat.get_item_prices_with_vat, price_list={}", priceList);
            ResponseEntity<ERPNextItemTaxTemplatesDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ERPNextItemTaxTemplatesDTO.class
            );
            log.info("ERPNext GET (Item Tax Templates) done: status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Item Tax Templates): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (Item Tax Templates)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Item Tax Templates): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamClientException("ERPNext rejected Item Tax Templates request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (Item Tax Templates): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (Item Tax Templates)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext Item Price: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    public CustomersDTO getCustomers() {
        List<String> fields = Arrays.asList("name", "customer_name", "customer_type", "tax_id", "primary_address");
        try {
            String fieldsJson = toJson(fields);

            String url = appProperties.getErpNextUrl()
                    + "/api/resource/Customer"
                    + "?fields=" + fieldsJson;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/resource/Customer");
            ResponseEntity<CustomersDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    CustomersDTO.class
            );
            log.info("ERPNext GET done (Customer): status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Customer): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (Customer)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Customer): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
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

    public ERPNextEmployeesDTO getCashiers() {
        String designation = appProperties.getErpNextEmployeeDesignation();
        List<String> fields = Arrays.asList("name", "employee_name", "employee_number");
        try {
            String fieldsJson = toJson(fields);
            String filtersJson =
                toJson(
                    List.of(
                        List.of("designation", "=", designation),
                        List.of("status", "=", "active"),
                        List.of("company", "=", runtimeProperties.getCompany())
                    )
                );

            String url = appProperties.getErpNextUrl()
                    + "/api/resource/Employee"
                    + "?filters=" + filtersJson
                    + "&fields=" + fieldsJson;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/resource/Employee, designation={}", designation);
            ResponseEntity<ERPNextEmployeesDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ERPNextEmployeesDTO.class
            );
            log.info("ERPNext GET done (Employee): status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Employee): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (Employee)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Employee): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamClientException("ERPNext rejected Employee request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (Employee): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (Employee)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext Employee: {}", rootMessage(ex), ex);
            throw ex;
        }
    }

    public ModeOfPaymentDTO getModeOfPayment() {
        List<String> fields = Arrays.asList("name", "type");
        try {
            String fieldsJson = toJson(fields);
            String filtersJson = toJson(List.of(List.of("custom_used_by_pos", "=", "1")));

            String url = appProperties.getErpNextUrl()
                    + "/api/resource/Mode of Payment"
                    + "?filters=" + filtersJson
                    + "&fields=" + fieldsJson;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("ERPNext GET start: path=/api/resource/Mode of Payment");
            ResponseEntity<ModeOfPaymentDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    ModeOfPaymentDTO.class
            );
            log.info("ERPNext GET done (Mode of Payment): status={}", response.getStatusCode().value());
            return response.getBody();
        } catch (RestClientResponseException ex) {
            org.springframework.http.HttpStatusCode status = ex.getStatusCode();

            if (status.is5xxServerError()) {
                log.error("ERPNext 5xx response (Mode of Payment): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamServerException("ERPNext server error (Employee)", status.value());
            } else {
                log.warn("ERPNext 4xx response (Mode of Payment): status={}, ERPNext Message:{}", status.value(), rootMessage(ex));
                throw new UpstreamClientException("ERPNext rejected Mode of Payment request", status.value());
            }
        } catch (ResourceAccessException ex) {
            log.warn("ERPNext access error (Mode of Payment): {}", rootMessage(ex));
            throw new UpstreamTimeoutException("Timeout or connection issue to ERPNext (Mode of Payment)", ex);
        } catch (Exception ex) {
            log.error("Unexpected error calling ERPNext Mode of Payment: {}", rootMessage(ex), ex);
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

//    private String sanitizeForLog(String s) {
//        if (s == null) return null;
//        String oneLine = s.replaceAll("[\\r\\n]+", " ");
//        // Basic masking: avoid accidental leak of token-like values
//        oneLine = oneLine.replaceAll("(?i)(token|secret|password)\\s*[:=]\\s*[^,\\s}{\"]+", "***");
//        return oneLine.length() > 1000 ? oneLine.substring(0, 1000) + "..." : oneLine;
//    }

    private String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage();
    }
}
