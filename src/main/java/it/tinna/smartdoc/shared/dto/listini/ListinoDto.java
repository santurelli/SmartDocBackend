package it.tinna.smartdoc.shared.dto.listini;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ListinoDto extends BaseDto {
    @Expose
    private String descrizione;
    private Integer flDefault;
    private Long idParent;
    private String derivationSource; // LISTINO, ULTIMO_ACQUISTO, MEDIO_ACQUISTO
    private String derivationType;   // NONE, PERCENTAGE, FIXED_MARKUP
    private BigDecimal derivationValue;
    private BigDecimal roundingRule;
    private BigDecimal aggiunta;
    private BigDecimal sottrazione;
    private BigDecimal importoFisso;
}

