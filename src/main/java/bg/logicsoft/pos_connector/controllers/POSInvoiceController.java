package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.config.AppProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class POSInvoiceController {
    private final AppProperties appProperties;

    public POSInvoiceController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/pos-invoice")
    public ResponseEntity<Map<String, Object>> posInvoice() {

        // TODO: make implementation
        Map<String, Object> posInvoiceTestResp = new LinkedHashMap<>();
        posInvoiceTestResp.put("pos-invoice", "TEST pos-invoice");
        posInvoiceTestResp.put("version", "0.0.1-SNAPSHOT");
        posInvoiceTestResp.put("erpNextProp.getUrl()", appProperties.getERPNext().getUrl());

        return ResponseEntity.ok(posInvoiceTestResp);
    }
}
