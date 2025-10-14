package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.services.ERPNextInitDataLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSLoadInitData {
    private final ERPNextInitDataLoaderService initDataLoaderService;

    @GetMapping("/reload-init-data")
    public ResponseEntity<String> reloadInitData() {
        initDataLoaderService.loadInitData();
        return ResponseEntity.ok("Initialization data was reloaded.");
    }
}
