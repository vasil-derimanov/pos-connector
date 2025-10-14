package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class POSSaleDTO {
    private String customer;
    @JsonProperty("posting_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date postingDate;

    private List<Item> items;
    private List<Payment> payments;
    //private List<Tax> taxes;

    public POSSaleDTO() {
    }

    @Getter
    @Setter
    public static class Item {
        @JsonProperty("item_code")
        private String itemCode;
        private Double qty;
        private Double rate;
        private String uom;
        @JsonProperty("item_tax_template")
        private String itemTaxTemplate;
        @JsonProperty("tax_type")
        private String taxType;
        @JsonProperty("tax_rate")
        private Double taxRate;

        public Item() {
        }
    }

    @Getter
    @Setter
    public static class Payment {
        @JsonProperty("mode_of_payment")
        private String modeOfPayment;
        private Double amount;

        public Payment() {
        }
    }
}
