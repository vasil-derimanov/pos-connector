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

import java.util.ArrayList;
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

        ERPNextItemsPriceDTO bgn = erpNextService.getItemPrices(priceListBGN);
        ERPNextItemsPriceDTO eur = erpNextService.getItemPrices(priceListEUR);

        List<POSItemPricesDTO.PriceList> priceLists = new ArrayList<>();

        addPriceListIfAny(priceLists, priceListBGN, bgn);
        addPriceListIfAny(priceLists, priceListEUR, eur);

        POSItemPricesDTO result = new POSItemPricesDTO();
        result.setData(priceLists);
        return ResponseEntity.ok(result);
    }

    private void addPriceListIfAny(List<POSItemPricesDTO.PriceList> target,
                                   String priceListName,
                                   ERPNextItemsPriceDTO source) {
        if (source == null || source.getData() == null || source.getData().isEmpty()) {
            return;
        }
        POSItemPricesDTO.PriceList pl = new POSItemPricesDTO.PriceList();
        pl.setName(priceListName);

        // Determine currency from first item if present, otherwise try to infer from price list name
        String currency = source.getData().get(0).getCurrency();
        if (currency == null || currency.isBlank()) {
            currency = deriveCurrencyFromName(priceListName);
        }
        pl.setCurrency(currency);

        pl.setItems(mapItems(source.getData()));
        target.add(pl);
    }

    private List<POSItemPricesDTO.Item> mapItems(List<ERPNextItemsPriceDTO.Item> src) {
        return src.stream().map(this::mapItem).collect(Collectors.toList());
    }

    private POSItemPricesDTO.Item mapItem(ERPNextItemsPriceDTO.Item s) {
        POSItemPricesDTO.Item d = new POSItemPricesDTO.Item();
        d.setUom(s.getUom());
        d.setCurrency(s.getCurrency());
        d.setItemCode(s.getItemCode());
        d.setItemName(s.getItemName());
        d.setPriceListRate(s.getPriceListRate());
        d.setPackingUnit(s.getPackingUnit());
        return d;
    }

    private String deriveCurrencyFromName(String priceListName) {
        String nameLower = priceListName == null ? "" : priceListName.toLowerCase();
        if (nameLower.contains("eur")) return "EUR";
        if (nameLower.contains("bgn")) return "BGN";
        return ""; // Unknown; leave blank if not derivable
        // Alternatively, default to appProperties.getErpNextPriceListCurrency... if you have it
    }
}
