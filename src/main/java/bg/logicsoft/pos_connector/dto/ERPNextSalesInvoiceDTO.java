package bg.logicsoft.pos_connector.dto;

import bg.logicsoft.pos_connector.config.AppProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ERPNextSalesInvoiceDTO {

    private static final String CURRENCY_BGN = "BGN";
    private static final String CURRENCY_EUR = "EUR";
    private static final String DOCTYPE_SALES_INVOICE = "Sales Invoice";

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
    @JsonProperty("debit_to")
    private String debitTo;
    @JsonProperty("is_pos")
    private int isPos;
    @JsonProperty("update_stock")
    private int updateStock;

    private List<Item> items;
    private List<Payment> payments;
    private List<Tax> taxes;

    public ERPNextSalesInvoiceDTO() {
    }

    private static String resolvePosProfileForCurrency(String currency, AppProperties appProperties) {
        if (currency == null) return null;
        return switch (currency) {
            case CURRENCY_BGN -> sanitizePropertyValue(appProperties.getErpNextPOSProfileBGN());
            case CURRENCY_EUR -> sanitizePropertyValue(appProperties.getErpNextPOSProfileEUR());
            default -> null;
        };
    }

    private static String resolveDebitToForCurrency(String currency, AppProperties appProperties) {
        if (currency == null) return null;
        return switch (currency) {
            case CURRENCY_BGN -> sanitizePropertyValue(appProperties.getErpNextDebitToBGN());
            case CURRENCY_EUR -> sanitizePropertyValue(appProperties.getErpNextDebitToEUR());
            default -> null;
        };
    }

    // trims whitespace and removes surrounding single/double quotes, if present
    private static String sanitizePropertyValue(String value) {
        if (value == null) return null;
        String trimmed = value.strip();
        if (trimmed.length() >= 2) {
            char first = trimmed.charAt(0);
            char last = trimmed.charAt(trimmed.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return trimmed.substring(1, trimmed.length() - 1);
            }
        }
        return trimmed;
    }

    // Maps a POSSaleDTO to an ERPNextSalesInvoiceDTO
    public void setFromPOSSale(POSSaleDTO sale, AppProperties appProperties) {
        if (sale == null) {
            return;
        }

        // 1. Simple fields
        this.setDoctype(DOCTYPE_SALES_INVOICE);
        this.setCustomer(sale.getCustomer());
        this.setCompany(sanitizePropertyValue(appProperties.getErpNextCompany()));
        this.setPostingDate(sale.getPostingDate());
        this.setCurrency(sanitizePropertyValue(sale.getCurrency()));

        final String currency = this.getCurrency();
        this.setPosProfile(resolvePosProfileForCurrency(currency, appProperties));
        this.setDebitTo(resolveDebitToForCurrency(currency, appProperties));
        this.setSetWarehouse(sanitizePropertyValue(appProperties.getErpNextPOSWarehouse()));
        this.setUpdateStock(1);
        this.setIsPos(1);

        // 2. Items
        if (sale.getItems() != null) {
            List<Item> mappedItems = new java.util.ArrayList<>();
            for (POSSaleDTO.Item srcItem : sale.getItems()) {
                if (srcItem == null) continue;
                Item item = new Item();
                item.setItemCode(sanitizePropertyValue(srcItem.getItemCode()));
                item.setQty(srcItem.getQty() != null ? srcItem.getQty() : 0.0);
                item.setRate(srcItem.getRate() != null ? srcItem.getRate() : 0.0);
                item.setWarehouse(sanitizePropertyValue(appProperties.getErpNextPOSWarehouse()));
                mappedItems.add(item);
            }
            this.setItems(mappedItems);
        } else {
            this.setItems(null);
        }

        // 3. Payments
        if (sale.getPayments() != null) {
            List<Payment> mappedPayments = new java.util.ArrayList<>();
            for (POSSaleDTO.Payment srcPay : sale.getPayments()) {
                if (srcPay == null) continue;
                Payment payment = new Payment();
                payment.setModeOfPayment(srcPay.getModeOfPayment());
                payment.setAmount(srcPay.getAmount() != null ? srcPay.getAmount() : 0.0);
                mappedPayments.add(payment);
            }
            this.setPayments(mappedPayments);
        } else {
            this.setPayments(null);
        }

        // 4. Taxes
        if (sale.getTaxes() != null) {
            List<Tax> mappedTaxes = new java.util.ArrayList<>();
            for (POSSaleDTO.Tax srcTax : sale.getTaxes()) {
                if (srcTax == null) continue;
                Tax tax = new Tax();
                tax.setChargeType(sanitizePropertyValue(srcTax.getChargeType()));
                tax.setAccountHead(sanitizePropertyValue(srcTax.getAccountHead()));
                tax.setDescription(sanitizePropertyValue(srcTax.getAccountHead()));
                tax.setRate(srcTax.getRate() != null ? srcTax.getRate() : 0.0);
                mappedTaxes.add(tax);
            }
            this.setTaxes(mappedTaxes);
        } else {
            this.setTaxes(null);
        }
    }

    // Nested classes
    @Getter
    @Setter
    public static class Item {
        @JsonProperty("item_code")
        private String itemCode;
        private double qty;
        private double rate;
        private String warehouse;
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
        private double rate;
    }
}