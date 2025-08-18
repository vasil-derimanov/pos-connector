package bg.logicsoft.pos_connector.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class POSItemsController {
    @GetMapping("/pos-items")
    public ResponseEntity<Map<String, Object>> health() {

        // TODO: make implementation
        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("pos-items", "TEST pos-items");
        healthStatus.put("version", "0.0.1-SNAPSHOT");

        return ResponseEntity.ok(healthStatus);
    }
}
