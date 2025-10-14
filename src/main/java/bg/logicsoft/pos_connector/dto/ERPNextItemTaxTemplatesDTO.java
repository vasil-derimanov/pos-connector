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
public class ERPNextItemTaxTemplatesDTO {

    private List<ItemTaxTemplate> message;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemTaxTemplate {
        private String name;
        private String title;
        private String company;

        @JsonProperty("custom_tax_category")
        private String customTaxCategory;

        private List<Tax> taxes;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tax {
        @JsonProperty("tax_type")
        private String taxType;

        @JsonProperty("tax_rate")
        private BigDecimal taxRate;
    }
}
