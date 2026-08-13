package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class MastrinoRigaDto extends BaseDto {

    @Expose
    private String     dataRegistrazione;

    @Expose
    private String     descrizione;

    @Expose
    private String     tipoDocumento;

    @Expose
    private String      numeroDocumento;

    @Expose
    private BigDecimal importoDare;

    @Expose
    private BigDecimal importoAvere;

    @Expose
    private BigDecimal saldoProgressivo;

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

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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

    public BigDecimal getSaldoProgressivo() {
        return saldoProgressivo;
    }

    public void setSaldoProgressivo(BigDecimal saldoProgressivo) {
        this.saldoProgressivo = saldoProgressivo;
    }
}
