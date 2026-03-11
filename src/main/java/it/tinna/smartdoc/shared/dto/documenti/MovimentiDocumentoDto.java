package it.tinna.smartdoc.shared.dto.documenti;

import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class MovimentiDocumentoDto extends BaseDto
{

    @Expose
    private String                     agente;

    @Expose
    private String                     denominazioneCliente;

    private boolean                    checked;

    @Expose
    private String                     dataDocumento;

    @Expose
    private String                     descrizioneStatoFatturaElettronica;

    private String                     descrTipoDocumento;

    @Expose
    private String                     erroreConsegna;

    @Expose
    private String                     erroreXml;

    @Expose
    private int                        flFatturaElettronica;

    @Expose
    private Integer                    idDocumento;

    private Integer                    idDocumentoCollegato;

    @Expose
    private String                     numeroDocumento;

    @Expose
    private String                     numDocumento;

    @Expose
    private List<ProdottoDocumentoDto> prodotti;

    @Expose
    private String                     soggetto;                          // cliente/fornitore

    @Expose
    private int                        splitPayment;

    @Expose
    private String                     stato;

    @Expose
    private StatoFatturaElettronica    statoFatturaElettronica;

    @Expose
    private String                     tipoDocumento;

    @Expose
    /* Usato soltanto quando il documento è una fattura */
    private TipoFattura                tipoFattura;

    @Expose
    private Double                     totale;

    @Expose
    private Double                     totaleDaPagare;

    @Expose
    private Double                     totalePagato;

    public String getAgente()
    {
        return agente;
    }

    public String getDataDocumento()
    {
        return dataDocumento;
    }

    public String getDenominazioneCliente()
    {
        return denominazioneCliente;
    }

    public String getDescrizioneStatoFatturaElettronica()
    {
        return descrizioneStatoFatturaElettronica;
    }

    public String getDescrTipoDocumento()
    {
        return descrTipoDocumento;
    }

    public String getErroreConsegna()
    {
        return erroreConsegna;
    }

    public String getErroreXml()
    {
        return erroreXml;
    }

    public int getFlFatturaElettronica()
    {
        return flFatturaElettronica;
    }

    public Integer getIdDocumento()
    {
        return idDocumento;
    }

    public Integer getIdDocumentoCollegato()
    {
        return idDocumentoCollegato;
    }

    public String getNumeroDocumento()
    {
        return numeroDocumento;
    }

    public String getNumDocumento()
    {
        return numDocumento;
    }

    public List<ProdottoDocumentoDto> getProdotti()
    {
        return prodotti;
    }

    public String getSoggetto()
    {
        return soggetto;
    }

    public int getSplitPayment()
    {
        return splitPayment;
    }

    public String getStato()
    {
        return stato;
    }

    public StatoFatturaElettronica getStatoFatturaElettronica()
    {
        return statoFatturaElettronica;
    }

    public String getTipoDocumento()
    {
        return tipoDocumento;
    }

    public TipoFattura getTipoFattura()
    {
        return tipoFattura;
    }

    public Double getTotale()
    {
        return totale;
    }

    public Double getTotaleDaPagare()
    {
        return totaleDaPagare;
    }

    public Double getTotalePagato()
    {
        return totalePagato;
    }

    @Override
    public boolean isChecked()
    {
        return checked;
    }

    public void setAgente(String agente)
    {
        this.agente = agente;
    }

    @Override
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public void setDataDocumento(String dataDocumento)
    {
        this.dataDocumento = dataDocumento;
    }

    public void setDenominazioneCliente(String denominazioneCliente)
    {
        this.denominazioneCliente = denominazioneCliente;
    }

    public void setDescrizioneStatoFatturaElettronica(String descrizioneStatoFatturaElettronica)
    {
        this.descrizioneStatoFatturaElettronica = descrizioneStatoFatturaElettronica;
    }

    public void setDescrTipoDocumento(String descrTipoDocumento)
    {
        this.descrTipoDocumento = descrTipoDocumento;
    }

    public void setErroreConsegna(String erroreConsegna)
    {
        this.erroreConsegna = erroreConsegna;
    }

    public void setErroreXml(String erroreXml)
    {
        this.erroreXml = erroreXml;
    }

    public void setFlFatturaElettronica(int flFatturaElettronica)
    {
        this.flFatturaElettronica = flFatturaElettronica;
    }

    public void setIdDocumento(Integer idDocumento)
    {
        this.idDocumento = idDocumento;
    }

    public void setIdDocumentoCollegato(Integer idDocumentoCollegato)
    {
        this.idDocumentoCollegato = idDocumentoCollegato;
    }

    public void setNumeroDocumento(String numeroDocumento)
    {
        this.numeroDocumento = numeroDocumento;
    }

    public void setNumDocumento(String numDocumento)
    {
        this.numDocumento = numDocumento;
    }

    public void setProdotti(List<ProdottoDocumentoDto> prodotti)
    {
        this.prodotti = prodotti;
    }

    public void setSoggetto(String soggetto)
    {
        this.soggetto = soggetto;
    }

    public void setSplitPayment(int splitPayment)
    {
        this.splitPayment = splitPayment;
    }

    public void setStato(String stato)
    {
        this.stato = stato;
    }

    public void setStatoFatturaElettronica(StatoFatturaElettronica statoFatturaElettronica)
    {
        this.statoFatturaElettronica = statoFatturaElettronica;
        this.descrizioneStatoFatturaElettronica = statoFatturaElettronica == null ? null : statoFatturaElettronica.getDescrizione();
    }

    public void setTipoDocumento(String tipoDocumento)
    {
        this.tipoDocumento = tipoDocumento;
    }

    public void setTipoFattura(TipoFattura tipoFattura)
    {
        this.tipoFattura = tipoFattura;
    }

    public void setTotale(Double totale)
    {
        this.totale = totale;
    }

    public void setTotaleDaPagare(Double totaleDaPagare)
    {
        this.totaleDaPagare = totaleDaPagare;
    }

    public void setTotalePagato(Double totalePagato)
    {
        this.totalePagato = totalePagato;
    }

}

