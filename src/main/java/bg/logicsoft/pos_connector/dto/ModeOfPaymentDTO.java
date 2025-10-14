package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModeOfPaymentDTO {
    private List<ModeOfPayment> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModeOfPayment {
        private String name;
        private String type;
    }
}
