package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.config.ERPNextRuntimeProperties;
import bg.logicsoft.pos_connector.dto.ItemPricesDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSItemPricesController {

    private final ERPNextService erpNextService;
    private final ERPNextRuntimeProperties runtimeProperties;

    @GetMapping("/item-prices")
    public ResponseEntity<ItemPricesDTO> getItemPrices() {
        String priceListName = runtimeProperties.getPriceList();
        ItemPricesDTO pl = erpNextService.getItemPrices(priceListName);

        return ResponseEntity.ok(pl);
    }
}
