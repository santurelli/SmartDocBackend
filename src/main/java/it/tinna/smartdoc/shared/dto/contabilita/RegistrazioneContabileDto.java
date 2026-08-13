package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RegistrazioneContabileDto extends BaseDto {

    @Expose
    private String dataRegistrazione;

    @Expose
    private String descrizione;

    @Expose
    private String tipoDocumento;

    @Expose
    private Integer idDocumento;

    @Expose
    private String numeroDocumento;

    @Expose
    private BigDecimal totaleDare;

    @Expose
    private BigDecimal totaleAvere;

    @Expose
    private List<MovimentoContabileRigaDto> righe;

    public String getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(String dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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

    public List<MovimentoContabileRigaDto> getRighe() {
        return righe;
    }

    public void setRighe(List<MovimentoContabileRigaDto> righe) {
        this.righe = righe;
    }
}
