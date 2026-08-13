package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class MovimentoContabileRigaDto extends BaseDto {

    @Expose
    private Integer registrazioneId;

    @Expose
    private Integer idConto;

    @Expose
    private String codiceConto;

    @Expose
    private String descrizioneConto;

    @Expose
    private BigDecimal importoDare;

    @Expose
    private BigDecimal importoAvere;

    @Expose
    private String descrizione;

    @Expose
    private Integer nProgr;

    public Integer getRegistrazioneId() {
        return registrazioneId;
    }

    public void setRegistrazioneId(Integer registrazioneId) {
        this.registrazioneId = registrazioneId;
    }

    public Integer getIdConto() {
        return idConto;
    }

    public void setIdConto(Integer idConto) {
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

    public BigDecimal getImportoDare() {
        return importoDare;
    }

    public void setImportoDare(BigDecimal importoDare) {
        this.importoDare = importoDare;
    }

    public BigDecimal getImportoAvere() {
        return importoAvere;
    }

    public void setImportoAvere(BigDecimal importoAvere) {
        this.importoAvere = importoAvere;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getnProgr() {
        return nProgr;
    }

    public void setnProgr(Integer nProgr) {
        this.nProgr = nProgr;
    }
}
