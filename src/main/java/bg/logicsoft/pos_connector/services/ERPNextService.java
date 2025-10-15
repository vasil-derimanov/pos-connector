package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.config.ERPNextRuntimeProperties;
import bg.logicsoft.pos_connector.dto.*;
import bg.logicsoft.pos_connector.exceptions.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ERPNextService {

    @Getter
    private final AppProperties appProperties;
    private final RestTemplate restTemplate;
    private final ERPNextRuntimeProperties runtimeProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ERPNextService(AppProperties appProperties,
                          RestTemplateBuilder restTemplateBuilder,
                          ERPNextRuntimeProperties runtimeProperties) {
        this.appProperties = appProperties;
        this.runtimeProperties = runtimeProperties;
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15))
                .build();
    }

    // region === PUBLIC METHODS ===

//    public ERPNextPOSProfileDTO getPOSProfile(String posProfileName) {
//        String url = buildUrl("/api/resource/POS Profile/" + posProfileName);
//        return get(url, ERPNextPOSProfileDTO.class, "POS Profile");
//    }

    public Map<String, Object> sendSalesInvoiceToERPNext(ERPNextSalesInvoiceDTO invoice) {
        String url = buildUrl("/api/method/frappe.client.insert");
        Map<String, Object> body = Map.of("doctype", "Sales Invoice", "doc", invoice);
        return post(url, body, "Sales Invoice");
    }

    public Map<String, Object> submitSalesInvoice(String invoiceName) {
        String url = buildUrl("/api/method/frappe.client.submit");
        Map<String, Object> body = Map.of("doctype", "Sales Invoice", "name", invoiceName);
        return post(url, body, "Sales Invoice Submit");
    }

    public ItemPricesDTO getItemPrices(String priceList) {
        String url = buildUrl("/api/method/custom_app.pos-api.get_item_prices.get_item_prices_with_vat")
                + "?price_list=" + priceList
                + "&company=" + runtimeProperties.getCompany();

        ItemPricesDTO result = get(url, ItemPricesDTO.class, "Item Prices");
        if (result != null && result.getData() != null) {
            result.getData().forEach(item -> {
                if (item.getImage() != null) {
                    item.setImage(appProperties.getErpNextUrl() + item.getImage());
                }
            });
        }
        return result;
    }

//    public ERPNextItemTaxTemplatesDTO getItemTaxTemplates(String priceList) {
//        String url = buildUrl("/api/method/custom_app.pos-api.get_item_prices.get_item_tax_templates")
//                + "&company=" + runtimeProperties.getCompany();
//        return get(url, ERPNextItemTaxTemplatesDTO.class, "Item Tax Templates");
//    }

    public CustomersDTO getCustomers() {
        String fieldsJson = toJson(List.of("name", "customer_name", "customer_type", "tax_id", "primary_address"));
        String url = buildUrl("/api/resource/Customer") + "?fields=" + fieldsJson;
        return get(url, CustomersDTO.class, "Customer");
    }

    public ERPNextEmployeesDTO getCashiers() {
        List<String> fields = List.of("name", "employee_name", "employee_number");
        String designation = appProperties.getErpNextEmployeeDesignation();
        String filtersJson = toJson(List.of(
                List.of("designation", "=", designation),
                List.of("status", "=", "active"),
                List.of("company", "=", runtimeProperties.getCompany())
        ));

        String url = buildUrl("/api/resource/Employee")
                + "?filters=" + filtersJson
                + "&fields=" + toJson(fields);
        return get(url, ERPNextEmployeesDTO.class, "Employee");
    }

    public ModeOfPaymentDTO getModeOfPayment() {
        String fieldsJson = toJson(List.of("name", "type"));
        String filtersJson = toJson(List.of(List.of("custom_used_by_pos", "=", "1")));

        String url = buildUrl("/api/resource/Mode of Payment")
                + "?filters=" + filtersJson
                + "&fields=" + fieldsJson;

        return get(url, ModeOfPaymentDTO.class, "Mode of Payment");
    }

    // endregion

    // region === PRIVATE HELPERS ===

    private <T> T get(String url, Class<T> responseType, String context) {
        HttpEntity<Void> request = new HttpEntity<>(buildHeaders());
        try {
            log.info("ERPNext GET start [{}]: {}", context, url);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, request, responseType);
            log.info("ERPNext GET done [{}]: status={}", context, response.getStatusCode());
            return response.getBody();
        } catch (Exception ex) {
            handleException(ex, context);
            return null; // Unreachable, but required for compilation
        }
    }

    private <T> T post(String url, Object body, String context) {
        HttpEntity<Object> request = new HttpEntity<>(body, buildHeaders());
        try {
            log.info("ERPNext POST start [{}]: {}", context, url);
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            log.info("ERPNext POST done [{}]: status={}", context, response.getStatusCode());
            return response.getBody();
        } catch (Exception ex) {
            handleException(ex, context);
            return null; // Unreachable
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION,
                "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());
        return headers;
    }

    private String buildUrl(String path) {
        return appProperties.getErpNextUrl() + path;
    }

    private void handleException(Exception ex, String context) {
        if (ex instanceof RestClientResponseException rcre) {
            int status = rcre.getStatusCode().value();
            String message = rootMessage(rcre);
            if (status >= 500) {
                log.error("ERPNext 5xx [{}]: status={}, message={}", context, status, message);
                throw new UpstreamServerException("ERPNext server error (" + context + ")", status);
            } else {
                log.warn("ERPNext 4xx [{}]: status={}, message={}", context, status, message);
                throw new UpstreamClientException("ERPNext rejected request (" + context + ")", status);
            }
        } else if (ex instanceof ResourceAccessException rae) {
            log.warn("ERPNext access error [{}]: {}", context, rootMessage(rae));
            throw new UpstreamTimeoutException("Timeout/connection issue (" + context + ")", rae);
        } else {
            log.error("Unexpected ERPNext error [{}]: {}", context, rootMessage(ex), ex);
            throw new RuntimeException("Unexpected ERPNext error (" + context + ")", ex);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize query parameter", e);
        }
    }

    private String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage();
    }

    // endregion
}
