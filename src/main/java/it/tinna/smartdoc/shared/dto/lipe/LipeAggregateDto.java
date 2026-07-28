package it.tinna.smartdoc.shared.dto.lipe;

import java.math.BigDecimal;

public class LipeAggregateDto {

    private BigDecimal imponibileVendite;
    private BigDecimal ivaEsigibile;
    private BigDecimal imponibileAcquisti;
    private BigDecimal ivaDetratta;

    public BigDecimal getImponibileVendite() { return imponibileVendite; }
    public void setImponibileVendite(BigDecimal v) { this.imponibileVendite = v; }

    public BigDecimal getIvaEsigibile() { return ivaEsigibile; }
    public void setIvaEsigibile(BigDecimal v) { this.ivaEsigibile = v; }

    public BigDecimal getImponibileAcquisti() { return imponibileAcquisti; }
    public void setImponibileAcquisti(BigDecimal v) { this.imponibileAcquisti = v; }

    public BigDecimal getIvaDetratta() { return ivaDetratta; }
    public void setIvaDetratta(BigDecimal v) { this.ivaDetratta = v; }
}
