package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunMethodRequestDTO {
    @JsonProperty("run_method")
    private String runMethod;

    public RunMethodRequestDTO(String runMethod) {
        this.runMethod = runMethod;
    }
}
