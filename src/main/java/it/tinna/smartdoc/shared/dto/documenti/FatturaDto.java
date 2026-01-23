package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public class FatturaDto extends FatturaElettronicaDto implements HasContabilita
{

    @Expose
    private String        dataScontrino;

    private List<Integer> idConfOrdine;

    private List<Integer> idDdt;

    @Expose
    private long          idFatturaCollegata;

    private Integer       idParametrizzazione;

    private List<Integer> idPreventivi;

    private BigDecimal    imponibileContabilita;

    private BigDecimal    impostaContabilita;

    @Expose
    private Integer       numeroScontrino;

    @Expose
    private TipoFattura   tipoFattura;

    private BigDecimal    totaleContabilita;

    public String getDataScontrino()
    {
        return dataScontrino;
    }

    public List<Integer> getIdConfOrdine()
    {
        return idConfOrdine;
    }

    public List<Integer> getIdDdt()
    {
        return idDdt;
    }

    public long getIdFatturaCollegata()
    {
        return idFatturaCollegata;
    }

    @Override
    public Integer getIdParametrizzazione()
    {
        return idParametrizzazione;
    }

    public List<Integer> getIdPreventivi()
    {
        return idPreventivi;
    }

    @Override
    public BigDecimal getImponibileContabilita()
    {
        return imponibileContabilita;
    }

    @Override
    public BigDecimal getImpostaContabilita()
    {
        return impostaContabilita;
    }

    public Integer getNumeroScontrino()
    {
        return numeroScontrino;
    }

    public TipoFattura getTipoFattura()
    {
        return tipoFattura;
    }

    @Override
    public BigDecimal getTotaleContabilita()
    {
        return totaleContabilita;
    }

    public void setDataScontrino(String dataScontrino)
    {
        this.dataScontrino = dataScontrino;
    }

    public void setIdConfOrdine(List<Integer> idConfOrdine)
    {
        this.idConfOrdine = idConfOrdine;
    }

    public void setIdDdt(List<Integer> idDocDaAssociare)
    {
        this.idDdt = idDocDaAssociare;
    }

    public void setIdFatturaCollegata(long idFatturaCollegata)
    {
        this.idFatturaCollegata = idFatturaCollegata;
    }

    @Override
    public void setIdParametrizzazione(Integer idParametrizzazione)
    {
        this.idParametrizzazione = idParametrizzazione;
    }

    public void setIdPreventivi(List<Integer> idPreventivi)
    {
        this.idPreventivi = idPreventivi;
    }

    @Override
    public void setImponibileContabilita(BigDecimal imponibileContabilita)
    {
        this.imponibileContabilita = imponibileContabilita;
    }

    @Override
    public void setImpostaContabilita(BigDecimal impostaContabilita)
    {
        this.impostaContabilita = impostaContabilita;
    }

    public void setNumeroScontrino(Integer numeroScontrino)
    {
        this.numeroScontrino = numeroScontrino;
    }

    public void setTipoFattura(TipoFattura tipoFattura)
    {
        this.tipoFattura = tipoFattura;
    }

    @Override
    public void setTotaleContabilita(BigDecimal totaleContabilita)
    {
        this.totaleContabilita = totaleContabilita;
    }

}
