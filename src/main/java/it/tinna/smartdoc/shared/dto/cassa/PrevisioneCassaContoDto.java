package it.tinna.smartdoc.shared.dto.cassa;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrevisioneCassaContoDto {

    @Expose
    private Integer idRisorsa;
    @Expose
    private String descrizioneConto;
    @Expose
    private BigDecimal saldoIniziale;
    @Expose
    private BigDecimal saldoAttuale;
    @Expose
    private BigDecimal saldoPrevisto30;
    @Expose
    private BigDecimal saldoPrevisto60;
    @Expose
    private BigDecimal saldoPrevisto90;
}
