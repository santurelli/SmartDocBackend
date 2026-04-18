package it.tinna.smartdoc.shared.dto.primanota;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaNotaSearchCriteriaDto {
    private Integer tipoPagamento;
    private String idSoggetto;
    private String dataDa;
    private String dataA;
    private Integer idRisorsa;
    private String tipologia;
    private long idDivisione;
    private Integer length;
    private Integer start;
    private Integer orderColumn;
    private String orderDir;
}
