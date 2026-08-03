package it.tinna.smartdoc.shared.dto.riconciliazione;

import java.math.BigDecimal;

/**
 * Candidato di abbinamento proposto per un movimento bancario: una scadenza aperta
 * (fattura attiva o fornitore) con relativo punteggio di affidabilita'.
 */
public class MatchCandidatoDto
{

    private Integer   idScadenza;
    private String    tipo; // INCASSO | PAGAMENTO
    private String    numeroDocumento;
    private String    dataScadenza;
    private BigDecimal importo;
    private String    soggetto;
    private BigDecimal score;

    public Integer getIdScadenza() { return idScadenza; }
    public void setIdScadenza(Integer idScadenza) { this.idScadenza = idScadenza; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(String dataScadenza) { this.dataScadenza = dataScadenza; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public String getSoggetto() { return soggetto; }
    public void setSoggetto(String soggetto) { this.soggetto = soggetto; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

}
