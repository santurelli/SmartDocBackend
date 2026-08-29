package it.tinna.smartdoc.shared.dto.cassa;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Previsione di cassa: saldo attuale + proiezione a 30/60/90 giorni basata sulle scadenze
 * NON ancora saldate. Le scadenze scadute e non saldate NON entrano nella proiezione
 * (approccio prudente): sono elencate a parte come rischio, non come certezza.
 */
@Getter
@Setter
@NoArgsConstructor
public class PrevisioneCassaDto {

    @Expose
    private BigDecimal saldoAttualeTotale;
    @Expose
    private BigDecimal saldoPrevisto30;
    @Expose
    private BigDecimal saldoPrevisto60;
    @Expose
    private BigDecimal saldoPrevisto90;

    @Expose
    private List<PuntoSerieCassaDto> serieGiornaliera;

    @Expose
    private List<PrevisioneCassaContoDto> perConto;

    @Expose
    private List<ScadenzaScadutaDto> scadute;
    @Expose
    private BigDecimal totaleScadutoDaIncassare;
    @Expose
    private BigDecimal totaleScadutoDaPagare;
}
