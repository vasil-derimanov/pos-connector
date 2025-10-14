package bg.logicsoft.pos_connector.mappers;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.config.ERPNextRuntimeProperties;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceDTO;
import bg.logicsoft.pos_connector.dto.POSSaleDTO;

import java.util.List;

public class SalesMapper {
    private static final String DOCTYPE_SALES_INVOICE = "Sales Invoice";

    // Maps a POSSaleDTO to an ERPNextSalesInvoiceDTO
    static public void setPOSSaleToERPNextSalesInvoice(
            ERPNextSalesInvoiceDTO saleERPNext,
            POSSaleDTO salePOS,
            AppProperties appProperties,
            ERPNextRuntimeProperties runtimeProperties) {

        if (salePOS == null || saleERPNext == null) {
            return;
        }

        // 1. Simple fields
        saleERPNext.setDoctype(DOCTYPE_SALES_INVOICE);
        saleERPNext.setCustomer(salePOS.getCustomer());
        saleERPNext.setCompany(sanitizePropertyValue(runtimeProperties.getCompany()));
        saleERPNext.setPostingDate(salePOS.getPostingDate());
        saleERPNext.setCurrency(runtimeProperties.getCurrency());

        saleERPNext.setPosProfile(sanitizePropertyValue(appProperties.getErpNextPOSProfile()));
        saleERPNext.setSetWarehouse(sanitizePropertyValue(runtimeProperties.getWarehouse()));
        saleERPNext.setUpdateStock(1);
        saleERPNext.setIsPos(1);

        // 2. Items
        if (salePOS.getItems() != null) {
            List<ERPNextSalesInvoiceDTO.Item> mappedItems = new java.util.ArrayList<>();
            for (POSSaleDTO.Item srcItem : salePOS.getItems()) {
                if (srcItem == null) continue;
                ERPNextSalesInvoiceDTO.Item item = new ERPNextSalesInvoiceDTO.Item();
                item.setItemCode(sanitizePropertyValue(srcItem.getItemCode()));
                item.setUom(sanitizePropertyValue(srcItem.getUom()));
                item.setQty(srcItem.getQty() != null ? srcItem.getQty() : 0.0);
                item.setRate(srcItem.getRate() != null ? srcItem.getRate() : 0.0);
                item.setWarehouse(sanitizePropertyValue(runtimeProperties.getWarehouse()));
                item.setItemTaxTemplate(sanitizePropertyValue(srcItem.getItemTaxTemplate()));
                mappedItems.add(item);

                populateTaxes(saleERPNext, srcItem, appProperties);
            }
            saleERPNext.setItems(mappedItems);
        } else {
            saleERPNext.setItems(null);
        }

        // 3. Payments
        if (salePOS.getPayments() != null) {
            List<ERPNextSalesInvoiceDTO.Payment> mappedPayments = new java.util.ArrayList<>();
            for (POSSaleDTO.Payment srcPay : salePOS.getPayments()) {
                if (srcPay == null) continue;
                ERPNextSalesInvoiceDTO.Payment payment = new ERPNextSalesInvoiceDTO.Payment();
                payment.setModeOfPayment(srcPay.getModeOfPayment());
                payment.setAmount(srcPay.getAmount() != null ? srcPay.getAmount() : 0.0);
                mappedPayments.add(payment);
            }
            saleERPNext.setPayments(mappedPayments);
        } else {
            saleERPNext.setPayments(null);
        }
    }

    static private void populateTaxes(
            ERPNextSalesInvoiceDTO saleERPNext,
            POSSaleDTO.Item item,
            AppProperties appProperties) {

        if (saleERPNext.getTaxes() == null) {
            saleERPNext.setTaxes(new java.util.ArrayList<>());
        }
        if (item != null) {
            boolean exists =
                    saleERPNext.getTaxes().stream().anyMatch(tax -> sanitizePropertyValue(item.getTaxType())
                            .equals(tax.getAccountHead()));
            if (!exists) {
                ERPNextSalesInvoiceDTO.Tax tax = new ERPNextSalesInvoiceDTO.Tax();
                tax.setChargeType(sanitizePropertyValue(appProperties.getErpNextChargeType()));
                tax.setAccountHead(sanitizePropertyValue(item.getTaxType()));
                tax.setDescription(sanitizePropertyValue(item.getTaxType()));
                tax.setRate(0);
                tax.setIncludedInPrintRate(1);
                saleERPNext.getTaxes().add(tax);
            }
        }
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
}
