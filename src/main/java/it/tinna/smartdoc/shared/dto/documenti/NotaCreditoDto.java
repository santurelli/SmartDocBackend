package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public class NotaCreditoDto extends FatturaElettronicaDto implements HasContabilita
{

    @Expose
    private Long       idFattura;

    private Integer    idParametrizzazione;

    private BigDecimal imponibileContabilita;

    private BigDecimal impostaContabilita;

    private BigDecimal totaleContabilita;

    public Long getIdFattura()
    {
        return idFattura;
    }

    @Override
    public Integer getIdParametrizzazione()
    {
        return idParametrizzazione;
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

    @Override
    public BigDecimal getTotaleContabilita()
    {
        return totaleContabilita;
    }

    public void setIdFattura(Long idFattura)
    {
        this.idFattura = idFattura;
    }

    @Override
    public void setIdParametrizzazione(Integer idParametrizzazione)
    {
        this.idParametrizzazione = idParametrizzazione;
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

    @Override
    public void setTotaleContabilita(BigDecimal totaleContabilita)
    {
        this.totaleContabilita = totaleContabilita;
    }

}

