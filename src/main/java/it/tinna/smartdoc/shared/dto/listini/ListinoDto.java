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
    private BigDecimal aggiunta;
    private BigDecimal sottrazione;
    private BigDecimal importoFisso;
}

