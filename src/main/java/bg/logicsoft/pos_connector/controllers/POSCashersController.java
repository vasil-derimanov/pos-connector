package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.EmployeesDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSCashersController {

    private final ERPNextService erpNextService;
    private final AppProperties appProperties;

    @GetMapping("/cashers")
    public ResponseEntity<EmployeesDTO> getCashers() {
        String employeeDesignation = appProperties.getErpNextEmployeeDesignation();
        EmployeesDTO result = erpNextService.getCashers(employeeDesignation);
        return ResponseEntity.ok(result);
    }
}
