package bg.logicsoft.pos_connector.dto;

import java.util.List;

public class POSInvoiceDTO {
    private String customer;
    private Double totalAmount;
    private List<POSInvoiceItemDTO> items;

    public POSInvoiceDTO() {} // 👈 default constructor required

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<POSInvoiceItemDTO> getItems() { return items; }
    public void setItems(List<POSInvoiceItemDTO> items) { this.items = items; }

    public static class POSInvoiceItemDTO {
        private String itemCode;
        private Double qty;
        private Double rate;

        public POSInvoiceItemDTO() {}

        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }

        public Double getQty() { return qty; }
        public void setQty(Double qty) { this.qty = qty; }

        public Double getRate() { return rate; }
        public void setRate(Double rate) { this.rate = rate; }
    }
}
