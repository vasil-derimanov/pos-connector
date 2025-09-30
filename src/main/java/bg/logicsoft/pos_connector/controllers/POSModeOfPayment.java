package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.dto.POSModeOfPaymentDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSModeOfPayment {
    private final ERPNextService erpNextService;

    @GetMapping("/mode-of-payment")
    public ResponseEntity<POSModeOfPaymentDTO> getModeOfPayment() {
        POSModeOfPaymentDTO modeOfPayment = erpNextService.getModeOfPayment();
        return ResponseEntity.ok(modeOfPayment);
    }
}
