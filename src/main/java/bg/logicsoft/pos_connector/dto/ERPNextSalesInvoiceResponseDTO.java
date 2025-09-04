// Java
package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ERPNextSalesInvoiceResponseDTO {

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

        private Integer docstatus;
        private Integer idx;
        private String title;

        @JsonProperty("naming_series")
        private String namingSeries;

        private String customer;
        @JsonProperty("customer_name")
        private String customerName;

        private String company;
        @JsonProperty("company_tax_id")
        private String companyTaxId;

        @JsonProperty("posting_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate postingDate;

        @JsonProperty("posting_time")
        private String postingTime;

        @JsonProperty("set_posting_time")
        private Integer setPostingTime;

        @JsonProperty("due_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate dueDate;

        @JsonProperty("is_pos")
        private Integer isPos;

        @JsonProperty("pos_profile")
        private String posProfile;

        private String currency;

        @JsonProperty("conversion_rate")
        private BigDecimal conversionRate;

        @JsonProperty("price_list_currency")
        private String priceListCurrency;

        @JsonProperty("set_warehouse")
        private String setWarehouse;

        @JsonProperty("update_stock")
        private Integer updateStock;

        @JsonProperty("total_qty")
        private BigDecimal totalQty;

        @JsonProperty("total")
        private BigDecimal total;

        @JsonProperty("net_total")
        private BigDecimal netTotal;

        @JsonProperty("base_total_taxes_and_charges")
        private BigDecimal baseTotalTaxesAndCharges;

        @JsonProperty("total_taxes_and_charges")
        private BigDecimal totalTaxesAndCharges;

        @JsonProperty("base_grand_total")
        private BigDecimal baseGrandTotal;

        @JsonProperty("grand_total")
        private BigDecimal grandTotal;

        @JsonProperty("rounded_total")
        private BigDecimal roundedTotal;

        @JsonProperty("in_words")
        private String inWords;

        @JsonProperty("outstanding_amount")
        private BigDecimal outstandingAmount;

        @JsonProperty("debit_to")
        private String debitTo;

        @JsonProperty("party_account_currency")
        private String partyAccountCurrency;

        private String status;
        private String doctype;

        private List<Payment> payments;
        private List<Item> items;
        private List<Tax> taxes;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payment {
        private String name;
        private Integer idx;

        @JsonProperty("mode_of_payment")
        private String modeOfPayment;

        private BigDecimal amount;

        private String account;
        private String type;

        @JsonProperty("base_amount")
        private BigDecimal baseAmount;

        private String doctype;
        private String parent;
        private String parentfield;
        private String parenttype;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String name;
        private Integer idx;

        @JsonProperty("item_code")
        private String itemCode;

        @JsonProperty("item_name")
        private String itemName;

        private String description;
        private String image;

        private BigDecimal qty;
        private String uom;

        private BigDecimal rate;
        private BigDecimal amount;

        @JsonProperty("net_rate")
        private BigDecimal netRate;

        @JsonProperty("net_amount")
        private BigDecimal netAmount;

        private String warehouse;

        @JsonProperty("income_account")
        private String incomeAccount;

        @JsonProperty("expense_account")
        private String expenseAccount;

        private String doctype;
        private String parent;
        private String parentfield;
        private String parenttype;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tax {
        private String name;
        private Integer idx;

        @JsonProperty("charge_type")
        private String chargeType;

        @JsonProperty("account_head")
        private String accountHead;

        private String description;

        private BigDecimal rate;

        @JsonProperty("tax_amount")
        private BigDecimal taxAmount;

        private BigDecimal total;

        @JsonProperty("base_tax_amount")
        private BigDecimal baseTaxAmount;

        @JsonProperty("tax_amount_after_discount_amount")
        private BigDecimal taxAmountAfterDiscountAmount;

        @JsonProperty("base_tax_amount_after_discount_amount")
        private BigDecimal baseTaxAmountAfterDiscountAmount;

        // The payload shows this as a JSON string; keep it as String unless you want to parse it.
        @JsonProperty("item_wise_tax_detail")
        private String itemWiseTaxDetail;

        private String doctype;
        private String parent;
        private String parentfield;
        private String parenttype;
    }
}
