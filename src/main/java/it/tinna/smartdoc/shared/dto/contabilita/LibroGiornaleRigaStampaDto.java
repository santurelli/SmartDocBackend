package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;

/**
 * Riga "appiattita" (registrazione + singolo movimento) usata solo per la stampa PDF del Libro
 * Giornale - report_stampa/libro_giornale.jrxml raggruppa per idRegistrazione. Non e' esposto via REST.
 */
public class LibroGiornaleRigaStampaDto {

    private long       idRegistrazione;
    private String     dataRegistrazione;
    private String     descrizioneRegistrazione;
    private String     tipoDocumento;
    private String     numeroDocumento;
    private String     codiceConto;
    private String     descrizioneConto;
    private String     descrizioneRiga;
    private BigDecimal importoDare;
    private BigDecimal importoAvere;

    public long getIdRegistrazione() {
        return idRegistrazione;
    }

    public void setIdRegistrazione(long idRegistrazione) {
        this.idRegistrazione = idRegistrazione;
    }

    public String getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(String dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public String getDescrizioneRegistrazione() {
        return descrizioneRegistrazione;
    }

    public void setDescrizioneRegistrazione(String descrizioneRegistrazione) {
        this.descrizioneRegistrazione = descrizioneRegistrazione;
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

    public String getDescrizioneRiga() {
        return descrizioneRiga;
    }

    public void setDescrizioneRiga(String descrizioneRiga) {
        this.descrizioneRiga = descrizioneRiga;
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
}
