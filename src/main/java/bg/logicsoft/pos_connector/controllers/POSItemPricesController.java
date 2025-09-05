package bg.logicsoft.pos_connector.controllers;

import bg.logicsoft.pos_connector.config.AppProperties;
import bg.logicsoft.pos_connector.dto.ERPNextItemsPriceDTO;
import bg.logicsoft.pos_connector.dto.POSItemPricesDTO;
import bg.logicsoft.pos_connector.services.ERPNextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class POSItemPricesController {

    private final ERPNextService erpNextService;
    private final AppProperties appProperties;

    @GetMapping("/item-prices")
    public ResponseEntity<POSItemPricesDTO> getItemPrices() {
        String priceListBGN = appProperties.getErpNextPriceListBGN();
        String priceListEUR = appProperties.getErpNextPriceListEUR();

        // Fetch from ERPNext
        ERPNextItemsPriceDTO bgn = erpNextService.getItemPrices(priceListBGN);
        ERPNextItemsPriceDTO eur = erpNextService.getItemPrices(priceListEUR);

        // Map into POSItemPricesDTO
        POSItemPricesDTO result = new POSItemPricesDTO();

        // Put mapped lists by price list name from configuration
        if (bgn != null && !bgn.getData().isEmpty()) {
            result.put(priceListBGN, mapItems(bgn.getData()));
        }
        if (eur != null && !eur.getData().isEmpty()) {
            result.put(priceListEUR, mapItems(eur.getData()));
        }

        return ResponseEntity.ok(result);
    }

    // Convert ERPNext DTO items to POS DTO items
    private List<POSItemPricesDTO.Item> mapItems(List<ERPNextItemsPriceDTO.Item> src) {
        return src.stream().map(this::mapItem).collect(Collectors.toList());
    }

    private POSItemPricesDTO.Item mapItem(ERPNextItemsPriceDTO.Item erpNextItem) {
        POSItemPricesDTO.Item posItem = new POSItemPricesDTO.Item();
        posItem.setItemCode(erpNextItem.getItemCode());
        posItem.setItemName(erpNextItem.getItemName());
        posItem.setUom(erpNextItem.getUom());
        posItem.setPriceListRate(erpNextItem.getPriceListRate());
        posItem.setCurrency(erpNextItem.getCurrency());
        posItem.setPackingUnit(erpNextItem.getPackingUnit());
        return posItem;
    }
}
