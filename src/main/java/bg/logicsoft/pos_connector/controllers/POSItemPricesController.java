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
        //String priceListBGN = appProperties.getErpNextPriceListBGN();
        String priceListEUR = appProperties.getErpNextPriceListEUR();

        //ERPNextItemsPriceDTO bgn = erpNextService.getItemPrices(priceListBGN);
        ERPNextItemsPriceDTO plEUR = erpNextService.getItemPrices(priceListEUR);

        List<POSItemPricesDTO.PriceList> priceLists = new ArrayList<>();

        //addPriceListIfAny(priceLists, priceListBGN, "BGN", bgn);
        addPriceListIfAny(priceLists, priceListEUR,  plEUR);

        POSItemPricesDTO result = new POSItemPricesDTO();
        result.setData(priceLists);
        return ResponseEntity.ok(result);
    }

    private void addPriceListIfAny(List<POSItemPricesDTO.PriceList> target,
                                   String priceListName,
                                   ERPNextItemsPriceDTO source) {
        if (source == null || source.getMessage() == null || source.getMessage().isEmpty()) {
            return;
        }
        POSItemPricesDTO.PriceList pl = new POSItemPricesDTO.PriceList();
        pl.setName(priceListName);

        pl.setItems(mapItems(source.getMessage()));
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
        d.setTaxName(s.getTaxName());
        d.setTaxRate(s.getTaxRate());
        return d;
    }
}
