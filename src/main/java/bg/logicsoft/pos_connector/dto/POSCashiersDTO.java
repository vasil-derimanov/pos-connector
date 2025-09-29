package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class POSCashiersDTO {

    private List<Cahier> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cahier {
        private String name;

        @JsonProperty("cahier_name")
        private String cahierName;

        private String pin;
    }
}
