package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.POSInvoiceDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class POSInvoiceController {
    private final ERPNextService erpNextService;

    public POSInvoiceController(ERPNextService erpNextService) {
        this.erpNextService = erpNextService;
    }

    @PostMapping("/pos-invoice")
    public ResponseEntity<?> createPOSInvoice(@RequestBody POSInvoiceDTO invoice) {
        return ResponseEntity.ok(erpNextService.sendInvoiceToERPNext(invoice));
    }
}
