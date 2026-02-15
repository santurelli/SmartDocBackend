package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
public class NotaCreditoDto extends FatturaElettronicaDto implements HasContabilita {

    @Expose
    private Long idFattura;

    private Integer idParametrizzazione;

    @Expose
    private BigDecimal imponibileContabilita;

    @Expose
    private BigDecimal impostaContabilita;

    @Expose
    private BigDecimal totaleContabilita;
}
