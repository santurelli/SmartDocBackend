package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

@SuppressWarnings("serial")
public class NotaCreditoFornitoreDto extends DocumentoAcquistoDto implements HasContabilita
{

    private String     descrizioneFatturaCollegata;

    @Expose
    private Long       idFatturaFornitore;

    private Integer    idParametrizzazione;

    private BigDecimal imponibileContabilita;

    private BigDecimal impostaContabilita;

    private BigDecimal totaleContabilita;

    public String getDescrizioneFatturaCollegata()
    {
        return descrizioneFatturaCollegata;
    }

    public Long getIdFatturaFornitore()
    {
        return idFatturaFornitore;
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

    public void setDescrizioneFatturaCollegata(String descrizioneFatturaCollegata)
    {
        this.descrizioneFatturaCollegata = descrizioneFatturaCollegata;
    }

    public void setIdFatturaFornitore(Long idFatturaFornitore)
    {
        this.idFatturaFornitore = idFatturaFornitore;
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

