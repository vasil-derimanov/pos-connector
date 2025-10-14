package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.dto.CustomersDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSCustomersController {

    private final ERPNextService erpNextService;

    @GetMapping("/customers")
    public ResponseEntity<CustomersDTO> getCustomers() {
        CustomersDTO customers = erpNextService.getCustomers();
        return ResponseEntity.ok(customers);
    }
}

