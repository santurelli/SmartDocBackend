package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

/**
 * Una riga dell'anteprima/esecuzione di chiusura esercizio: il saldo di un conto a fine anno
 * (convenzione Dare positivo, Avere negativo) e cosa gli succede alla chiusura.
 */
@SuppressWarnings("serial")
public class ChiusuraEsercizioRigaDto extends BaseDto {

    @Expose
    private Long       idConto;

    @Expose
    private String     codiceConto;

    @Expose
    private String     descrizioneConto;

    @Expose
    private String     tipoConto;

    @Expose
    private BigDecimal saldo;

    public Long getIdConto() {
        return idConto;
    }

    public void setIdConto(Long idConto) {
        this.idConto = idConto;
    }

    public String getCodiceConto() {
        return codiceConto;
    }

    public void setCodiceConto(String codiceConto) {
        this.codiceConto = codiceConto;
    }

    public String getDescrizioneConto() {
        return descrizioneConto;
    }

    public void setDescrizioneConto(String descrizioneConto) {
        this.descrizioneConto = descrizioneConto;
    }

    public String getTipoConto() {
        return tipoConto;
    }

    public void setTipoConto(String tipoConto) {
        this.tipoConto = tipoConto;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
