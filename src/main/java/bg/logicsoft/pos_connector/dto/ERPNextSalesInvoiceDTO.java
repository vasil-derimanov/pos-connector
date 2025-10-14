package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ERPNextSalesInvoiceDTO {

    private String doctype;
    private String customer;
    @JsonProperty("pos_profile")
    private String posProfile;
    private String company;
    @JsonProperty("posting_date")
    private Date postingDate;
    private String currency;
    @JsonProperty("set_warehouse")
    private String setWarehouse;
    @JsonProperty("is_pos")
    private int isPos;
    @JsonProperty("update_stock")
    private int updateStock;

    private List<Item> items;
    private List<Payment> payments;
    private List<Tax> taxes;

    public ERPNextSalesInvoiceDTO() {
    }

    // Nested classes
    @Getter
    @Setter
    public static class Item {
        @JsonProperty("item_code")
        private String itemCode;
        private String uom;
        private double qty;
        private double rate;
        private String warehouse;           // TODO : Probably not needed anymore
        @JsonProperty("item_tax_template")
        private String itemTaxTemplate;
    }

    @Getter
    @Setter
    public static class Payment {
        @JsonProperty("mode_of_payment")
        private String modeOfPayment;
        private double amount;
    }

    @Getter
    @Setter
    public static class Tax {
        @JsonProperty("charge_type")
        private String chargeType;
        @JsonProperty("account_head")
        private String accountHead;
        private String description;
        @JsonProperty("included_in_print_rate")
        private int includedInPrintRate;
        private double rate;
    }
}