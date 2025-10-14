package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.config.ERPNextRuntimeProperties;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceDTO;
import bg.logicsoft.pos_connector.dto.ERPNextSalesInvoiceResponseDTO;
import bg.logicsoft.pos_connector.dto.POSSaleDTO;
import bg.logicsoft.pos_connector.mappers.SalesMapper;
import bg.logicsoft.pos_connector.services.ERPNextService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class POSSaleController {
    private final ERPNextService erpNextService;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;
    private final ERPNextRuntimeProperties runtimeProperties;

    //private final FPGateService fpGateService;

    public POSSaleController(
            ERPNextService erpNextService,
            ObjectMapper objectMapper,
            AppProperties appProperties,
            ERPNextRuntimeProperties runtimeProperties
            /*, FPGateService fpGateService */) {
        this.erpNextService = erpNextService;
        this.objectMapper = objectMapper;
        this.appProperties = appProperties;
        this.runtimeProperties = runtimeProperties;
        /* this.fpGateService = fpGateService ;*/
    }

    @PostMapping("/pos-sale")
    public ResponseEntity<?> createERPNextInvoice(@RequestBody POSSaleDTO salePOS) {

        ERPNextSalesInvoiceDTO saleERPNext = new ERPNextSalesInvoiceDTO();
        SalesMapper.setPOSSaleToERPNextSalesInvoice(saleERPNext, salePOS, appProperties, runtimeProperties);

        // 1) Send invoice to ERPNext
        var erpResult = erpNextService.sendSalesInvoiceToERPNext(saleERPNext);

        // 2) Convert ERPNext response to typed DTO
        ERPNextSalesInvoiceResponseDTO invoiceResponseDTO =
                objectMapper.convertValue(erpResult, ERPNextSalesInvoiceResponseDTO.class);
        String invoiceName = invoiceResponseDTO.getData() != null ? invoiceResponseDTO.getData().getName() : null;

        // 3) send "Submit" command (change status of the invoice)
        Map<String, Object> submitResult = null;
        if (invoiceName != null && !invoiceName.isBlank()) {
            submitResult = erpNextService.submitSalesInvoice(invoiceName);
        }

        // 4) After success, send receipt to FPGate
        //var fpResult = fpGateService.sendReceipt(sale);

        // Return the typed DTO (or include both if you prefer)
        return ResponseEntity.ok(invoiceResponseDTO);
    }
}
