package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("serial")
public class ScontrinoDto extends DocumentoDto implements HasContabilita
{

    private Integer       idParametrizzazione;

    private BigDecimal    imponibileContabilita;

    private BigDecimal    impostaContabilita;

    private BigDecimal    totaleContabilita;

    // private String sconto;
    private Integer       idTipoPagamento;

    private List<Integer> idDdt;

    private Double        subTotale;
    // private Double totale;

    public List<Integer> getIdDdt()
    {
        return idDdt;
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

    // public String getSconto() {
    // return sconto;
    // }

    public Double getSubTotale()
    {
        return subTotale;
    }

    // public Double getTotale() {
    // return totale;
    // }

    @Override
    public BigDecimal getTotaleContabilita()
    {
        return totaleContabilita;
    }

    public void setIdDdt(List<Integer> idDocDaAssociare)
    {
        this.idDdt = idDocDaAssociare;
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

    // public void setSconto(String sconto) {
    // this.sconto = sconto;
    // }

    public void setSubTotale(Double subTotale)
    {
        this.subTotale = subTotale;
    }

    // public void setTotale(Double totale) {
    // this.totale = totale;
    // }

    @Override
    public void setTotaleContabilita(BigDecimal totaleContabilita)
    {
        this.totaleContabilita = totaleContabilita;
    }

    public Integer getIdTipoPagamento()
    {
        return idTipoPagamento;
    }

    public void setIdTipoPagamento(Integer idTipoPagamento)
    {
        this.idTipoPagamento = idTipoPagamento;
    }

}

