package bg.logicsoft.pos_connector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    @Value("${erpnext.url}")
    private String erpNextUrl;

    @Value("${fpgate.url}")
    private String fpGateUrl;

    @Value("${erpnext.api-key}")
    private String erpNextApiKey;

    @Value("${erpnext.api-secret}")
    private String erpNextApiSecret;

    public String getERPNextUrl() { return erpNextUrl;}
    public String getERPNextApiKey() { return erpNextApiKey; }
    public String getERPNextApiSecret() { return erpNextApiSecret; }

    public String getFPGateUrl() {
        return fpGateUrl;
    }
}
