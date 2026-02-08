package it.tinna.smartdoc.shared.dto.tipipagamento;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TipoPagamentoDto extends BaseDto {
    @Expose
    private String descrizione;
    @Expose
    private String modalita;
    @Expose
    private Integer predefinito;
    @Expose
    private Integer saldaSubito;
    @Expose
    private Integer giornoPagamentoMeseSuccessivo;
    @Expose
    private Integer idSpeseIncasso;
    @Expose
    private Integer spostaScadenze;
    @Expose
    private List<ScadenzaPagamentoDto> scadenze;
}
