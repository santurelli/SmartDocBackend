package it.tinna.smartdoc.batch.dto.fastorder.comande;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.fastorder.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class PagamentoComandaDto extends BaseDto
{

    private Integer idComanda;

    private Double  contanti;

    private Double  cartaCredito;

    private String  tipoCarta;

    private Double  buoniPasto;

    @Expose
    private Double  sconto;

    private Double  servizio;

    @Expose
    private Double  totale;

    private Double  totalePersona;

}

