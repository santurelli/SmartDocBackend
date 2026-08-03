package it.tinna.smartdoc.shared.dto.riconciliazione;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RiconciliazioneImportDto extends BaseDto
{

    private String  nomeFile;
    private String  formato; // MT940 | CSV
    private Integer idRisorsa;
    private String  stato; // COMPLETATO | ERRORE
    private String  dettaglioErrore;
    private Integer numMovimenti;
    private Integer numAbbinatiAuto;
    private String  dtImport;

    public String getNomeFile() { return nomeFile; }
    public void setNomeFile(String nomeFile) { this.nomeFile = nomeFile; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public Integer getIdRisorsa() { return idRisorsa; }
    public void setIdRisorsa(Integer idRisorsa) { this.idRisorsa = idRisorsa; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getDettaglioErrore() { return dettaglioErrore; }
    public void setDettaglioErrore(String dettaglioErrore) { this.dettaglioErrore = dettaglioErrore; }

    public Integer getNumMovimenti() { return numMovimenti; }
    public void setNumMovimenti(Integer numMovimenti) { this.numMovimenti = numMovimenti; }

    public Integer getNumAbbinatiAuto() { return numAbbinatiAuto; }
    public void setNumAbbinatiAuto(Integer numAbbinatiAuto) { this.numAbbinatiAuto = numAbbinatiAuto; }

    public String getDtImport() { return dtImport; }
    public void setDtImport(String dtImport) { this.dtImport = dtImport; }

}
