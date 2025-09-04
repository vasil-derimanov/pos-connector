package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcPrintRequestDTO {

    private String jsonrpc = "2.0";
    private String method;

    private Params params;

    private Object id;

    @Getter
    @Setter
    public static class Params {
        @JsonProperty("Printer")
        private Printer printer;

        @JsonProperty("Command")
        private String command;

        @JsonProperty("Arguments")
        private List<String> arguments;
    }

    @Getter
    @Setter
    public static class Printer {
        @JsonProperty("ID")
        private String id;
    }
}