package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.dto.ERPNextEmployeesDTO;
import bg.logicsoft.pos_connector.dto.POSCashiersDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSCashiersController {

    private final ERPNextService erpNextService;

    @GetMapping("/cashiers")
    public ResponseEntity<POSCashiersDTO> getCashiers() {
        ERPNextEmployeesDTO erpNextEmployees = erpNextService.getCashiers();
        POSCashiersDTO result = toCashiersDTO(erpNextEmployees);
        return ResponseEntity.ok(result);
    }

    // Convert full DTO (ERPNextEmployeesDTO → POSCashiersDTO)
    public static POSCashiersDTO toCashiersDTO(ERPNextEmployeesDTO employeesDTO) {
        if (employeesDTO == null) {
            return null;
        }
        POSCashiersDTO posDTO = new POSCashiersDTO();
        posDTO.setData(toCahierList(employeesDTO.getData()));
        return posDTO;
    }

    // Convert list of Employees → list of Cahiers
    public static List<POSCashiersDTO.Cahier> toCahierList(List<ERPNextEmployeesDTO.Employee> employees) {
        if (employees == null) {
            return null;
        }
        return employees.stream()
                .map(POSCashiersController::toCahier)
                .collect(Collectors.toList());
    }

    // Convert single Employee → Cahier
    public static POSCashiersDTO.Cahier toCahier(ERPNextEmployeesDTO.Employee employee) {
        if (employee == null) {
            return null;
        }
        POSCashiersDTO.Cahier c = new POSCashiersDTO.Cahier();
        c.setName(employee.getName());
        c.setCahierName(employee.getEmployeeName());
        c.setPin(employee.getEmployeeNumber());
        return c;
    }

}
