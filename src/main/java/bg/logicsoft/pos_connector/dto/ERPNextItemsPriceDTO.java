// Java
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
public class ERPNextItemsPriceDTO {

    private List<Item> message;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String name;

        @JsonProperty("item_code")
        private String itemCode;

        @JsonProperty("item_name")
        private String itemName;

        private String uom;

        @JsonProperty("price_list_rate")
        private BigDecimal priceListRate;

        private String currency;

        @JsonProperty("packing_unit")
        private Integer packingUnit;

        @JsonProperty("tax_name")
        private String taxName;

        @JsonProperty("tax_rate")
        private BigDecimal taxRate;
    }
}
