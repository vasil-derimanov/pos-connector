package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.dto.EmployeesDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSCasherByNumber {

    private final ERPNextService erpNextService;

    @GetMapping("/casher-by-number")
    public ResponseEntity<EmployeesDTO> getCasherByNumber(@RequestParam("employee_number") String employeeNumber) {
        EmployeesDTO result = erpNextService.getCasherByNumber(employeeNumber);
        return ResponseEntity.ok(result);
    }
}
