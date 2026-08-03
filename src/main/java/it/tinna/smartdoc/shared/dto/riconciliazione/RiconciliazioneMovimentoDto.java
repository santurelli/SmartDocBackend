package it.tinna.smartdoc.shared.dto.riconciliazione;

import java.math.BigDecimal;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RiconciliazioneMovimentoDto extends BaseDto
{

    private Integer   idImport;
    private String    dataValuta;
    private String    dataContabile;
    private BigDecimal importo;
    private String    causaleBanca;
    private String    controparte;
    private String    ibanControparte;
    private String    stato; // NON_ABBINATO | ABBINATO_AUTO | ABBINATO_MANUALE | IGNORATO
    private String    tipoScadenzaAbbinata; // INCASSO | PAGAMENTO
    private Integer   idScadenzaAbbinata;
    private BigDecimal scoreMatching;
    private String    dtAbbinamento;

    // Solo per la risposta di dettaglio: candidati proposti (non persistito)
    private java.util.List<MatchCandidatoDto> candidati;

    public Integer getIdImport() { return idImport; }
    public void setIdImport(Integer idImport) { this.idImport = idImport; }

    public String getDataValuta() { return dataValuta; }
    public void setDataValuta(String dataValuta) { this.dataValuta = dataValuta; }

    public String getDataContabile() { return dataContabile; }
    public void setDataContabile(String dataContabile) { this.dataContabile = dataContabile; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public String getCausaleBanca() { return causaleBanca; }
    public void setCausaleBanca(String causaleBanca) { this.causaleBanca = causaleBanca; }

    public String getControparte() { return controparte; }
    public void setControparte(String controparte) { this.controparte = controparte; }

    public String getIbanControparte() { return ibanControparte; }
    public void setIbanControparte(String ibanControparte) { this.ibanControparte = ibanControparte; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getTipoScadenzaAbbinata() { return tipoScadenzaAbbinata; }
    public void setTipoScadenzaAbbinata(String tipoScadenzaAbbinata) { this.tipoScadenzaAbbinata = tipoScadenzaAbbinata; }

    public Integer getIdScadenzaAbbinata() { return idScadenzaAbbinata; }
    public void setIdScadenzaAbbinata(Integer idScadenzaAbbinata) { this.idScadenzaAbbinata = idScadenzaAbbinata; }

    public BigDecimal getScoreMatching() { return scoreMatching; }
    public void setScoreMatching(BigDecimal scoreMatching) { this.scoreMatching = scoreMatching; }

    public String getDtAbbinamento() { return dtAbbinamento; }
    public void setDtAbbinamento(String dtAbbinamento) { this.dtAbbinamento = dtAbbinamento; }

    public java.util.List<MatchCandidatoDto> getCandidati() { return candidati; }
    public void setCandidati(java.util.List<MatchCandidatoDto> candidati) { this.candidati = candidati; }

}
