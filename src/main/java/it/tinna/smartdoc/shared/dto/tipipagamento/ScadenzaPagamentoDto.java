package it.tinna.smartdoc.shared.dto.tipipagamento;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScadenzaPagamentoDto extends BaseDto {
    @Expose
    private Long idTipoPagamento;
    @Expose
    private Integer giorni;
    @Expose
    private Double percTotale;
    @Expose
    private Integer fineMese;
}
