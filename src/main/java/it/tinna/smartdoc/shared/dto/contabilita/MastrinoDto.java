package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

/**
 * Mastrino di un conto: elenco cronologico dei movimenti che lo riguardano con saldo progressivo.
 * Il saldo di apertura e' sempre zero (non e' ancora gestita l'apertura di un nuovo esercizio con
 * riporto dei saldi patrimoniali) - vedi RegistrazioneContabileDelegate per il dettaglio.
 */
@SuppressWarnings("serial")
public class MastrinoDto extends BaseDto {

    @Expose
    private Long                    idConto;

    @Expose
    private String                  codiceConto;

    @Expose
    private String                  descrizioneConto;

    @Expose
    private String                  tipoConto;

    @Expose
    private List<MastrinoRigaDto>   righe;

    @Expose
    private BigDecimal              totaleDare;

    @Expose
    private BigDecimal              totaleAvere;

    @Expose
    private BigDecimal              saldoFinale;

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

    public List<MastrinoRigaDto> getRighe() {
        return righe;
    }

    public void setRighe(List<MastrinoRigaDto> righe) {
        this.righe = righe;
    }

    public BigDecimal getTotaleDare() {
        return totaleDare;
    }

    public void setTotaleDare(BigDecimal totaleDare) {
        this.totaleDare = totaleDare;
    }

    public BigDecimal getTotaleAvere() {
        return totaleAvere;
    }

    public void setTotaleAvere(BigDecimal totaleAvere) {
        this.totaleAvere = totaleAvere;
    }

    public BigDecimal getSaldoFinale() {
        return saldoFinale;
    }

    public void setSaldoFinale(BigDecimal saldoFinale) {
        this.saldoFinale = saldoFinale;
    }
}
