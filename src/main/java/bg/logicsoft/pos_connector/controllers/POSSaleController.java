package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceDTO;
import bg.logicsoft.pos_connector.dto.POSSaleDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class POSSaleController {
    private final ERPNextService erpNextService;

    public POSSaleController(ERPNextService erpNextService) {
        this.erpNextService = erpNextService;
    }

    @PostMapping("/pos-sale")
    public ResponseEntity<?> createPOSInvoice(@RequestBody POSSaleDTO sale) {

        ERPNextSalesInvoiceDTO erpNextSalesInvoiceDTO = new ERPNextSalesInvoiceDTO();
        erpNextSalesInvoiceDTO.setFromPOSSale(sale, this.erpNextService.getAppProperties());

        return ResponseEntity.ok(erpNextService.sendSalesInvoiceToERPNext(erpNextSalesInvoiceDTO));
    }
}
