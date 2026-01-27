package it.tinna.smartdoc.shared.dto.prodotti;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class MovimentiSearchCriteriaDto extends BaseDto {
    private String dtFrom;
    private String dtTo;
    private Integer idProdotto;
    private int start;
    private int length;
}
