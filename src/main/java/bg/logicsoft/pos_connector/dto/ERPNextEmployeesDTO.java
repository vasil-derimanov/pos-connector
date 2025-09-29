package bg.logicsoft.pos_connector.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ERPNextEmployeesDTO {

    private List<Employee> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Employee {
        private String name;

        @JsonProperty("employee_name")
        private String employeeName;

        @JsonProperty("employee_number")
        private String employeeNumber;
    }
}
