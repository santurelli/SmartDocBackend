package it.tinna.smartdoc.shared.dto.external.fastorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PagamentoComandaDto extends BaseDto {
    private static final long serialVersionUID = 1L;

    private Integer idComanda;
    private Double contanti;
    private Double cartaCredito;
    private String tipoCarta;
    private Double buoniPasto;
    private Double sconto;
    private Double servizio;
    private Double totale;
    private Double totalePersona;
}
