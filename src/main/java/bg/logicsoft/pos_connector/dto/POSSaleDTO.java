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
    private String currency;

    private List<Item> items;
    private List<Payment> payments;
    private List<Tax> taxes;

    public POSSaleDTO() {
    }

    @Getter
    @Setter
    public static class Item {
        @JsonProperty("item_code")
        private String itemCode;
        private Double qty;
        private Double rate;

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

    @Getter
    @Setter
    public static class Tax {
        @JsonProperty("charge_type")
        private String chargeType;
        @JsonProperty("account_head")
        private String accountHead;
        private Double rate;

        public Tax() {
        }
    }
}
