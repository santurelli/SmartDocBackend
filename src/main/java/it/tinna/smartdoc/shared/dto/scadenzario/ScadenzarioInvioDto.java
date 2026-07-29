package it.tinna.smartdoc.shared.dto.scadenzario;

import java.math.BigDecimal;

public class ScadenzarioInvioDto {

    private Integer   id;
    private String    dtInvio;
    private String    oggetto;
    private String    destinatario;
    private String    esito;
    private String    dettaglioErrore;
    private String    numeroDocumento;
    private String    dataScadenza;
    private BigDecimal importo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDtInvio() { return dtInvio; }
    public void setDtInvio(String dtInvio) { this.dtInvio = dtInvio; }

    public String getOggetto() { return oggetto; }
    public void setOggetto(String oggetto) { this.oggetto = oggetto; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getEsito() { return esito; }
    public void setEsito(String esito) { this.esito = esito; }

    public String getDettaglioErrore() { return dettaglioErrore; }
    public void setDettaglioErrore(String dettaglioErrore) { this.dettaglioErrore = dettaglioErrore; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(String dataScadenza) { this.dataScadenza = dataScadenza; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }
}
