package bg.logicsoft.pos_connector.services;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.config.ERPNextRuntimeProperties;
import bg.logicsoft.pos_connector.dto.ERPNextItemTaxTemplatesDTO;
import bg.logicsoft.pos_connector.dto.ERPNextPOSProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class ERPNextInitDataLoaderService {

    private final RestTemplate restTemplate;
    private final ERPNextRuntimeProperties runtimeProperties;
    private final AppProperties appProperties;

    public void loadInitData() {
        /* gets initial settings from ERPNext (if there is URL) */
        String erpNextURL = appProperties.getErpNextUrl();
        if (erpNextURL == null) {
            return;
        }

        System.out.println("🚀 Loading Initialization data began. Fetching data from ERPNext API...");
        // Get "POS Profile"
        System.out.println("Requesting 'POS Profile' ...");
        String posProfileName = appProperties.getErpNextPOSProfile();
        try {
            ERPNextPOSProfileDTO posProfileDTO = this.getPOSProfile(erpNextURL);
            if (posProfileDTO == null || posProfileDTO.getData() == null) {
                System.out.println("POS Profile response is empty for name= " + posProfileName);
                System.exit(0);
            }

            ERPNextPOSProfileDTO.Data data = posProfileDTO.getData();
            runtimeProperties.setCompany(data.getCompany());
            runtimeProperties.setWarehouse(data.getWarehouse());
            runtimeProperties.setCurrency(data.getCurrency());
            runtimeProperties.setPriceList(data.getSellingPriceList());

            log.info("POS Profile '{}' loaded. company='{}', warehouse='{}', currency='{}', price_list='{}'",
                    posProfileName,
                    runtimeProperties.getCompany(),
                    runtimeProperties.getWarehouse(),
                    runtimeProperties.getCurrency(),
                    runtimeProperties.getPriceList());
            System.out.println("Getting 'POS Profile': OK");
        } catch (Exception ex) {
            System.out.println("POS Profile error: " + ex.getMessage());
            System.exit(0);
        }

        // Get "Item Tax Templates"
        System.out.println("Requesting 'Item Tax Templates' ...");
        try {
            ERPNextItemTaxTemplatesDTO itemTaxTemplates = this.getItemTaxTemplates(erpNextURL);
            if (itemTaxTemplates == null || itemTaxTemplates.getMessage() == null) {
                System.out.println("Item Tax Templates response is empty!");
                System.exit(0);
            }
            runtimeProperties.setItemTaxTemplates(itemTaxTemplates);
            System.out.println("Getting 'Item Tax Templates': OK");
        } catch (Exception ex) {
            System.out.println("Item Tax Templates error: " + ex.getMessage());
            System.exit(0);
        }
    }

    private ERPNextPOSProfileDTO getPOSProfile(String erpNextURL) {
        String posProfileName = appProperties.getErpNextPOSProfile();

        String url = erpNextURL
                + "/api/resource/POS Profile/" + posProfileName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ERPNextPOSProfileDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                ERPNextPOSProfileDTO.class
        );
        return response.getBody();
    }

    private ERPNextItemTaxTemplatesDTO getItemTaxTemplates(String erpNextURL) {
        String url = erpNextURL
                + "/api/method/custom_app.pos-api.get_item_tax_templates.get_item_tax_templates"
                + "?company=" + runtimeProperties.getCompany();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "token " + appProperties.getErpNextApiKey() + ":" + appProperties.getErpNextApiSecret());

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ERPNextItemTaxTemplatesDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                ERPNextItemTaxTemplatesDTO.class
        );
        return response.getBody();
    }
}
