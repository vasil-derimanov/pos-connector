package bg.logicsoft.pos_connector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pos-connector")
public class AppProperties {

    private ERPNextProperties erpnext = new ERPNextProperties();
    private FPGateProperties fpgate = new FPGateProperties();

    public ERPNextProperties getERPNext() {
        return erpnext;
    }
    public void setERPNext(ERPNextProperties erpnext) {
        this.erpnext = erpnext;
    }

    public FPGateProperties getFPGate() {
        return fpgate;
    }
    public void setFPGate(FPGateProperties fpgate) {
        this.fpgate = fpgate;
    }

    // Nested static classes for sub-properties
    public static class ERPNextProperties {
        private String url;

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class FPGateProperties {
        private String url;

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }
}
