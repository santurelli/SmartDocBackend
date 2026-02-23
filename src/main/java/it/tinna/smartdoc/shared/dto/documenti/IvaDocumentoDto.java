package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;

@SuppressWarnings("serial")
public class IvaDocumentoDto extends BaseDto
{

    private String                         categoriaDocumento;

    @Expose
    private String                         dataDocumento;

    @Expose
    private String                         descrTipoDocumento;

    @Expose
    private int                            esigibilitaDifferita;

    @Expose
    private String                         gruppoDocumento;

    private Integer                        idSoggetto;

    @Expose
    private BigDecimal                     importoPagato;

    @Expose
    private double                         iva;

    @Expose
    private BigDecimal                     ivaCredito;

    private String                         ivaCreditoFormattato;

    @Expose
    private BigDecimal                     ivaDebito;

    private String                         ivaDebitoFormattato;

    private List<DettaglioIvaDocumentoDto> listDettaglio;

    private Integer                        nProgr;

    @Expose
    private String                         numeroDocumento;

    @Expose
    private BigDecimal                     percentualePagamento;

    private List<RiepilogoIvaDto>          riepilogoIva;

    @Expose
    private String                         soggetto;

    @Expose
    private String                         tipoDocumento;

    @Expose
    private String                         tipoFattura;

    @Expose
    private BigDecimal                     totale;

    private String                         totaleFormattato;

    public String getCategoriaDocumento()
    {
        return categoriaDocumento;
    }

    public String getDataDocumento()
    {
        return dataDocumento;
    }

    public String getDescrTipoDocumento()
    {
        return descrTipoDocumento;
    }

    public int getEsigibilitaDifferita()
    {
        return esigibilitaDifferita;
    }

    public String getGruppoDocumento()
    {
        return gruppoDocumento;
    }

    public Integer getIdSoggetto()
    {
        return idSoggetto;
    }

    public BigDecimal getImportoPagato()
    {
        return importoPagato;
    }

    public double getIva()
    {
        return iva;
    }

    public BigDecimal getIvaCredito()
    {
        return ivaCredito;
    }

    public String getIvaCreditoFormattato()
    {
        return ivaCreditoFormattato;
    }

    public BigDecimal getIvaDebito()
    {
        return ivaDebito;
    }

    public String getIvaDebitoFormattato()
    {
        return ivaDebitoFormattato;
    }

    public List<DettaglioIvaDocumentoDto> getListDettaglio()
    {
        return listDettaglio;
    }

    public Integer getnProgr()
    {
        return nProgr;
    }

    public String getNumeroDocumento()
    {
        return numeroDocumento;
    }

    public BigDecimal getPercentualePagamento()
    {
        return percentualePagamento;
    }

    public List<RiepilogoIvaDto> getRiepilogoIva()
    {
        return riepilogoIva;
    }

    public String getSoggetto()
    {
        return soggetto;
    }

    public String getTipoDocumento()
    {
        return tipoDocumento;
    }

    public String getTipoFattura()
    {
        return tipoFattura;
    }

    public BigDecimal getTotale()
    {
        return totale;
    }

    public String getTotaleFormattato()
    {
        return totaleFormattato;
    }

    public void setCategoriaDocumento(String categoriaDocumento)
    {
        this.categoriaDocumento = categoriaDocumento;
    }

    public void setDataDocumento(String dataDocumento)
    {
        this.dataDocumento = dataDocumento;
    }

    public void setDescrTipoDocumento(String descrTipoDocumento)
    {
        this.descrTipoDocumento = descrTipoDocumento;
    }

    public void setEsigibilitaDifferita(int esigibilitaDifferita)
    {
        this.esigibilitaDifferita = esigibilitaDifferita;
    }

    public void setGruppoDocumento(String gruppoDocumento)
    {
        this.gruppoDocumento = gruppoDocumento;
    }

    public void setIdSoggetto(Integer idSoggetto)
    {
        this.idSoggetto = idSoggetto;
    }

    public void setImportoPagato(BigDecimal importoPagato)
    {
        this.importoPagato = importoPagato;
    }

    public void setIva(double iva)
    {
        this.iva = iva;
    }

    public void setIvaCredito(BigDecimal ivaCredito)
    {
        this.ivaCredito = ivaCredito;
    }

    public void setIvaCreditoFormattato(String ivaCreditoFormattato)
    {
        this.ivaCreditoFormattato = ivaCreditoFormattato;
    }

    public void setIvaDebito(BigDecimal ivaDebito)
    {
        this.ivaDebito = ivaDebito;
    }

    public void setIvaDebitoFormattato(String ivaDebitoFormattato)
    {
        this.ivaDebitoFormattato = ivaDebitoFormattato;
    }

    public void setListDettaglio(List<DettaglioIvaDocumentoDto> listDettaglio)
    {
        this.listDettaglio = listDettaglio;
    }

    public void setnProgr(Integer nProgr)
    {
        this.nProgr = nProgr;
    }

    public void setNumeroDocumento(String numeroDocumento)
    {
        this.numeroDocumento = numeroDocumento;
    }

    public void setPercentualePagamento(BigDecimal percentualePagamento)
    {
        this.percentualePagamento = percentualePagamento;
    }

    public void setRiepilogoIva(List<RiepilogoIvaDto> riepilogoIva)
    {
        this.riepilogoIva = riepilogoIva;
    }

    public void setSoggetto(String soggetto)
    {
        this.soggetto = soggetto;
    }

    public void setTipoDocumento(String tipoDocumento)
    {
        this.tipoDocumento = tipoDocumento;
    }

    public void setTipoFattura(String tipoFattura)
    {
        this.tipoFattura = tipoFattura;
    }

    public void setTotale(BigDecimal totale)
    {
        this.totale = totale;
    }

    public void setTotaleFormattato(String totaleFormattato)
    {
        this.totaleFormattato = totaleFormattato;
    }

}

