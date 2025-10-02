package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ERPNextPOSProfileDTO {

    private Data data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String name;
        private String owner;
        private String creation;
        private String modified;

        @JsonProperty("modified_by")
        private String modifiedBy;

        private Integer docStatus;
        private Integer idx;
        private String company;
        private String country;
        private Integer disabled;
        private String warehouse;

        @JsonProperty("hide_images")
        private Integer hideImages;

        @JsonProperty("hide_unavailable_items")
        private Integer hideUnavailableItems;

        @JsonProperty("auto_add_item_to_cart")
        private Integer autoAddItemToCart;

        @JsonProperty("validate_stock_on_save")
        private Integer validateStockOnSave;

        @JsonProperty("print_receipt_on_order_complete")
        private Integer printReceiptOnOrderComplete;

        @JsonProperty("update_stock")
        private Integer updateStock;

        @JsonProperty("ignore_pricing_rule")
        private Integer ignorePricingRule;

        @JsonProperty("allow_rate_change")
        private Integer allowRateChange;

        @JsonProperty("allow_discount_change")
        private Integer allowDiscountChange;

        @JsonProperty("disable_grand_total_to_default_mop")
        private Integer disableGrandTotalToDefaultMop;

        @JsonProperty("allow_partial_payment")
        private Integer allowPartialPayment;

        @JsonProperty("letter_head")
        private String letterHead;

        @JsonProperty("selling_price_list")
        private String sellingPriceList;

        private String currency;

        @JsonProperty("write_off_account")
        private String writeOffAccount;

        @JsonProperty("write_off_cost_center")
        private String writeOffCostCenter;

        @JsonProperty("write_off_limit")
        private BigDecimal writeOffLimit;

        @JsonProperty("account_for_change_amount")
        private String accountForChangeAmount;

        @JsonProperty("disable_rounded_total")
        private Integer disableRoundedTotal;

        @JsonProperty("income_account")
        private String incomeAccount;

        @JsonProperty("expense_account")
        private String expenseAccount;

        @JsonProperty("taxes_and_charges")
        private String taxesAndCharges;

        @JsonProperty("apply_discount_on")
        private String applyDiscountOn;

        @JsonProperty("cost_center")
        private String costCenter;

        private String doctype;

        @JsonProperty("applicable_for_users")
        private List<ApplicableUser> applicableForUsers;

        @JsonProperty("item_groups")
        private List<Object> itemGroups;

        private List<Payment> payments;

        @JsonProperty("customer_groups")
        private List<Object> customerGroups;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApplicableUser {
        private String name;
        private String owner;
        private String creation;
        private String modified;

        @JsonProperty("modified_by")
        private String modifiedBy;

        private Integer docStatus;
        private Integer idx;

        @JsonProperty("default")
        private Integer defaultValue; // 'default' is reserved in Java

        private String user;
        private String parent;
        private String parentField;
        private String parentType;
        private String doctype;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payment {
        private String name;
        private String owner;
        private String creation;
        private String modified;

        @JsonProperty("modified_by")
        private String modifiedBy;

        private Integer docStatus;
        private Integer idx;

        @JsonProperty("default")
        private Integer defaultValue;

        @JsonProperty("allow_in_returns")
        private Integer allowInReturns;

        @JsonProperty("mode_of_payment")
        private String modeOfPayment;

        private String parent;
        private String parentField;
        private String parentType;
        private String doctype;
    }
}
