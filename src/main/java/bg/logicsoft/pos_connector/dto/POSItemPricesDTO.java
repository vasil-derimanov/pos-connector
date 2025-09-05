package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class POSItemPricesDTO {

    // Key: price list name (e.g., "Standard Selling BGN"), Value: list of items
    private final Map<String, List<Item>> priceLists = new HashMap<>();

    @JsonAnySetter
    public void put(String priceListName, List<Item> items) {
        priceLists.put(priceListName, items);
    }

    @JsonAnyGetter
    public Map<String, List<Item>> getPriceLists() {
        return priceLists;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
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
    }
}
