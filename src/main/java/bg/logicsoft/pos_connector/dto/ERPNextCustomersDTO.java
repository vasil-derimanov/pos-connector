package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ERPNextCustomersDTO {

    private List<Item> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String name;

        @JsonProperty("customer_name")
        private String customerName;

        @JsonProperty("customer_type")
        private String customerType;

        @JsonProperty("tax_id")
        private String taxId;

        @JsonProperty("primary_address")
        private String primaryAddress;
    }
}
