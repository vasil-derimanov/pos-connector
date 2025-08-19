package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.POSInvoiceDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ERPNextService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    public ERPNextService(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> sendInvoiceToERPNext(POSInvoiceDTO invoice) {
        String url = appProperties.getERPNextUrl() + "/api/resource/POS Invoice";

        // Headers (ERPNext usually requires authentication: API Key + Secret or cookie)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token <api_key>:<api_secret>");

        HttpEntity<POSInvoiceDTO> request = new HttpEntity<>(invoice, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody();
    }
}
