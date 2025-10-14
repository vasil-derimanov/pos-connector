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
public class ItemPricesDTO {

    private List<Item> data;

    @JsonProperty(value = "message")
    private void unpackMessage(List<Item> items) {
        this.data = items;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String uom;
        private String currency;

        @JsonProperty("item_code")
        private String itemCode;

        @JsonProperty("item_name")
        private String itemName;

        @JsonProperty("price_list_rate")
        private BigDecimal priceListRate;

        @JsonProperty("packing_unit")
        private Integer packingUnit;

        @JsonProperty("item_tax_template")
        private String itemTaxTemplate;

        @JsonProperty("tax_type")
        private String taxType;

        @JsonProperty("tax_rate")
        private BigDecimal taxRate;

        private String image;
    }
}
