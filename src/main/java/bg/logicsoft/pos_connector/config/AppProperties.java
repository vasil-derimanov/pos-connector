package bg.logicsoft.pos_connector.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppProperties {

    /* ERPNext configuration */
    @Value("${erpnext.url}")
    private String erpNextUrl;

    @Value("${erpnext.api-key}")
    private String erpNextApiKey;

    @Value("${erpnext.api-secret}")
    private String erpNextApiSecret;

    /* ERPNext "Sales Invoice" details */
    @Value("${erpnext.pos-warehouse}")
    private String erpNextPOSWarehouse;

    @Value("${erpnext.pos-profile-BGN}")
    private String erpNextPOSProfileBGN;

    @Value("${erpnext.pos-profile-EUR}")
    private String erpNextPOSProfileEUR;

    @Value("${erpnext.company}")
    private String erpNextCompany;

    @Value("${erpnext.debit-to-BGN}")
    private String erpNextDebitToBGN;

    @Value("${erpnext.debit-to-EUR}")
    private String erpNextDebitToEUR;

    @Value("${erpnext.mode-of-payment-BGN}")
    private String erpNextModeOfPaymentBGN;

    @Value("${erpnext.mode-of-payment-EUR}")
    private String erpNextModeOfPaymentEUR;

    @Value("${erpnext.price-list-BGN}")
    private String erpNextPriceListBGN;

    @Value("${erpnext.price-list-EUR}")
    private String erpNextPriceListEUR;

    @Value("${erpnext.charge-type}")
    private String erpNextChargeType;

    /* Fiscal Printer Gate configuration */
    @Value("${fpgate.url}")
    private String fpGateUrl;
}
