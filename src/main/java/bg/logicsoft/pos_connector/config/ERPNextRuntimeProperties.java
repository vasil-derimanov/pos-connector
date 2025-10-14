package bg.logicsoft.pos_connector.config;

import bg.logicsoft.pos_connector.dto.ERPNextItemTaxTemplatesDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ERPNextRuntimeProperties {
    private String company;
    private String warehouse;
    private String currency;
    private String priceList;

    private ERPNextItemTaxTemplatesDTO itemTaxTemplates;
}
