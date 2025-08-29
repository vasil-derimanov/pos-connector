package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceDTO;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ERPNextService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate;

    public ERPNextService(AppProperties appProperties,RestTemplateBuilder restTemplateBuilder ) {
        this.appProperties = appProperties;
        this.restTemplate = restTemplateBuilder.build();
    }

    public AppProperties getAppProperties() {
        return appProperties;
    }

    public Map<String, Object> sendSalesInvoiceToERPNext(ERPNextSalesInvoiceDTO invoice) {
        String url = appProperties.getErpNextUrl() + "/api/resource/Sales Invoice";

        // Headers (ERPNext usually requires authentication: API Key + Secret or cookie)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token 0175434ef147c4b:aa736b8b7550d35");

        HttpEntity<ERPNextSalesInvoiceDTO> request = new HttpEntity<>(invoice, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody();
    }
}
