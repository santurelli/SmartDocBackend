package it.tinna.smartdoc.shared.dto.tipipagamento;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;
import it.tinna.smartdoc.shared.dto.documenti.HasContabilita;

@SuppressWarnings("serial")
public class ScadenzaPagamentoDocumentoDto extends ScadenzaPagamentoDto implements HasContabilita
{

    @Expose
    private Integer    acconto;

    @Expose
    private String     descDocumento;

    @Expose
    private String     descModalitaPagamento;

    @Expose
    private String     descRisorsa;

    @Expose
    private String     dtPagamento;

    @Expose
    private String     dtScadenza;

    private long       idDocumento;

    private Integer    idParametrizzazione;

    @Expose
    private Integer    idRisorsa;

    private BigDecimal imponibileContabilita;

    @Expose
    private Double     importo;

    private String     importoFormattato;

    private Double     importoSpeseIncasso;

    private BigDecimal impostaContabilita;

    private Double     ivaSpeseIncasso;

    @Expose
    private String     modalitaPagamento;

    @Expose
    private String     note;

    @Expose
    private String     rifPagamento;

    @Expose
    private int        saldato;

    private BigDecimal totaleContabilita;

    private String     tipoScadenza;         // D: Dare | A: Avere

    private String     tipoDocumento;        // rif. DocumentoDto.TipoDoc

    private String     dtDocumento;

    private Double     totaleDocumento;

    private String     numeroDocumento;

    private Integer    idSoggetto;

    private String     denominazioneSoggetto;

    private Integer    flScaduta;

    public Integer getAcconto()
    {
        return acconto;
    }

    public String getDenominazioneSoggetto()
    {
        return denominazioneSoggetto;
    }

    public String getDescDocumento()
    {
        return descDocumento;
    }

    public String getDescModalitaPagamento()
    {
        return descModalitaPagamento;
    }

    public String getDescRisorsa()
    {
        return descRisorsa;
    }

    public String getDtDocumento()
    {
        return dtDocumento;
    }

    public String getDtPagamento()
    {
        return dtPagamento;
    }

    public String getDtScadenza()
    {
        return dtScadenza;
    }

    public Integer getFlScaduta()
    {
        return flScaduta;
    }

    public long getIdDocumento()
    {
        return idDocumento;
    }

    @Override
    public Integer getIdParametrizzazione()
    {
        return idParametrizzazione;
    }

    public Integer getIdRisorsa()
    {
        return idRisorsa;
    }

    public Integer getIdSoggetto()
    {
        return idSoggetto;
    }

    @Override
    public BigDecimal getImponibileContabilita()
    {
        imponibileContabilita = totaleContabilita;
        return imponibileContabilita;
    }

    public Double getImporto()
    {
        return importo;
    }

    public String getImportoFormattato()
    {
        return importoFormattato;
    }

    public Double getImportoSpeseIncasso()
    {
        return importoSpeseIncasso;
    }

    @Override
    public BigDecimal getImpostaContabilita()
    {
        return impostaContabilita;
    }

    public Double getIvaSpeseIncasso()
    {
        return ivaSpeseIncasso;
    }

    public String getModalitaPagamento()
    {
        return modalitaPagamento;
    }

    public String getNote()
    {
        return note;
    }

    public String getNumeroDocumento()
    {
        return numeroDocumento;
    }

    public String getRifPagamento()
    {
        return rifPagamento;
    }

    public int getSaldato()
    {
        return saldato;
    }

    public String getTipoDocumento()
    {
        return tipoDocumento;
    }

    public String getTipoScadenza()
    {
        return tipoScadenza;
    }

    @Override
    public BigDecimal getTotaleContabilita()
    {
        return totaleContabilita;
    }

    public Double getTotaleDocumento()
    {
        return totaleDocumento;
    }

    public void setAcconto(Integer acconto)
    {
        this.acconto = acconto;
    }

    public void setDenominazioneSoggetto(String denominazioneSoggetto)
    {
        this.denominazioneSoggetto = denominazioneSoggetto;
    }

    public void setDescDocumento(String descDocumento)
    {
        this.descDocumento = descDocumento;
    }

    public void setDescModalitaPagamento(String descModalitaPagamento)
    {
        this.descModalitaPagamento = descModalitaPagamento;
    }

    public void setDescRisorsa(String descRisorsa)
    {
        this.descRisorsa = descRisorsa;
    }

    public void setDtDocumento(String dtDocumento)
    {
        this.dtDocumento = dtDocumento;
    }

    public void setDtPagamento(String dtPagamento)
    {
        this.dtPagamento = dtPagamento;
    }

    public void setDtScadenza(String dtScadenza)
    {
        this.dtScadenza = dtScadenza;
    }

    public void setFlScaduta(Integer flScaduta)
    {
        this.flScaduta = flScaduta;
    }

    public void setIdDocumento(long idDocumento)
    {
        this.idDocumento = idDocumento;
    }

    @Override
    public void setIdParametrizzazione(Integer idParametrizzazione)
    {
        this.idParametrizzazione = idParametrizzazione;
    }

    public void setIdRisorsa(Integer idRisorsa)
    {
        this.idRisorsa = idRisorsa;
        setIdParametrizzazione(idRisorsa);
    }

    public void setIdSoggetto(Integer idSoggetto)
    {
        this.idSoggetto = idSoggetto;
    }

    @Override
    public void setImponibileContabilita(BigDecimal imponibileContabilita)
    {
        this.imponibileContabilita = imponibileContabilita;
    }

    public void setImporto(Double importo)
    {
        this.importo = importo;
        setTotaleContabilita(importo == null ? null : BigDecimal.valueOf(importo));
    }

    public void setImportoFormattato(String importoFormattato)
    {
        this.importoFormattato = importoFormattato;
    }

    public void setImportoSpeseIncasso(Double importoSpesaIncasso)
    {
        this.importoSpeseIncasso = importoSpesaIncasso;
    }

    @Override
    public void setImpostaContabilita(BigDecimal impostaContabilita)
    {
        impostaContabilita = totaleContabilita;
        this.impostaContabilita = impostaContabilita;
    }

    public void setIvaSpeseIncasso(Double ivaSpesaIncasso)
    {
        this.ivaSpeseIncasso = ivaSpesaIncasso;
    }

    public void setModalitaPagamento(String modalitaPagamento)
    {
        this.modalitaPagamento = modalitaPagamento;
        if ( StringUtils.isBlank(modalitaPagamento) )
        {
            setDescModalitaPagamento("");
        }
        else
        {
            setDescModalitaPagamento(ModalitaPagamentoEnum.valueOf(modalitaPagamento).getDescrizione());
        }
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public void setNumeroDocumento(String numeroDocumento)
    {
        this.numeroDocumento = numeroDocumento;
    }

    public void setRifPagamento(String rifPagamento)
    {
        this.rifPagamento = rifPagamento;
    }

    public void setSaldato(int saldato)
    {
        this.saldato = saldato;
    }

    public void setTipoDocumento(String tipoDocumento)
    {
        this.tipoDocumento = tipoDocumento;
    }

    public void setTipoScadenza(String tipoScadenza)
    {
        this.tipoScadenza = tipoScadenza;
    }

    @Override
    public void setTotaleContabilita(BigDecimal totaleContabilita)
    {
        this.totaleContabilita = totaleContabilita;
    }

    public void setTotaleDocumento(Double totaleDocumento)
    {
        this.totaleDocumento = totaleDocumento;
    }

}

