package it.tinna.smartdoc.shared.dto.scadenzario;

import java.math.BigDecimal;

public class ScadenzaPromemoriaDto {

    private Integer idScadenza;
    private String numeroDocumento;
    private String dataDocumento;
    private String dataScadenza;
    private BigDecimal importo;
    private BigDecimal totaleFattura;
    private String nomeSoggetto;
    private String emailDestinatario;

    public Integer getIdScadenza() { return idScadenza; }
    public void setIdScadenza(Integer idScadenza) { this.idScadenza = idScadenza; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getDataDocumento() { return dataDocumento; }
    public void setDataDocumento(String dataDocumento) { this.dataDocumento = dataDocumento; }

    public String getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(String dataScadenza) { this.dataScadenza = dataScadenza; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public BigDecimal getTotaleFattura() { return totaleFattura; }
    public void setTotaleFattura(BigDecimal totaleFattura) { this.totaleFattura = totaleFattura; }

    public String getNomeSoggetto() { return nomeSoggetto; }
    public void setNomeSoggetto(String nomeSoggetto) { this.nomeSoggetto = nomeSoggetto; }

    public String getEmailDestinatario() { return emailDestinatario; }
    public void setEmailDestinatario(String emailDestinatario) { this.emailDestinatario = emailDestinatario; }
}
