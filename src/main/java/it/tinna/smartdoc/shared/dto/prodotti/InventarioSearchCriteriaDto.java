package it.tinna.smartdoc.shared.dto.prodotti;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class InventarioSearchCriteriaDto extends BaseDto {
    private String search;
    private Integer idMagazzino;
    private Integer idCategoria;
    private Integer idSottoCategoria;
    private Integer idFornitore;
    private Integer idArticolo;
    private String dataAl;
    private int start;
    private int length;
    private Integer orderColumn;
    private String orderDir;
}

