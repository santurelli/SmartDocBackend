package it.tinna.smartdoc.shared.dto.template;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RiepilogoIvaDto extends BaseDto
{

    private Double  aliquotaIva;

    private String  aliquotaIvaFormattata;

    private String  descrizioneAliquota;

    private Integer idAliquotaIva;

    private Double  imponibileMerce;

    private String  imponibileMerceFormattato;

    private Double  imponibileSpese;

    private String  imponibileSpeseFormattato;

    private Double  importoIva;

    private String  importoIvaFormattato;

    private String  tipologiaIva;

    private Double  totaleDetraibile;

    private String  totaleDetraibileFormattato;

    private Double  totaleImponibile;

    private String  totaleImponibileFormattato;

    private Double  totaleImposta;

    private String  totaleImpstaFormattato;

    private Double  totaleIndetraibile;

    private String  totaleIndetraibileFormattato;

    public boolean equals(Object obj)
    {
        if ( obj instanceof RiepilogoIvaDto )
        {
            // return ((RiepilogoIvaDto) obj).getAliquotaIva().equals(this.getAliquotaIva()) && ((RiepilogoIvaDto) obj).getTipologiaIva().equals(this.getTipologiaIva());
            return ((RiepilogoIvaDto) obj).getIdAliquotaIva().equals(this.getIdAliquotaIva());
        }
        return false;
    }

    public Double getAliquotaIva()
    {
        return aliquotaIva;
    }

    public String getAliquotaIvaFormattata()
    {
        return aliquotaIvaFormattata;
    }

    public String getDescrizioneAliquota()
    {
        return descrizioneAliquota;
    }

    public Integer getIdAliquotaIva()
    {
        return idAliquotaIva;
    }

    public Double getImponibileMerce()
    {
        return imponibileMerce;
    }

    public String getImponibileMerceFormattato()
    {
        return imponibileMerceFormattato;
    }

    public Double getImponibileSpese()
    {
        return imponibileSpese;
    }

    public String getImponibileSpeseFormattato()
    {
        return imponibileSpeseFormattato;
    }

    public Double getImportoIva()
    {
        return importoIva;
    }

    public String getImportoIvaFormattato()
    {
        return importoIvaFormattato;
    }

    public Double getTotaleDetraibile()
    {
        return totaleDetraibile;
    }

    public String getTotaleDetraibileFormattato()
    {
        return totaleDetraibileFormattato;
    }

    public Double getTotaleImponibile()
    {
        return totaleImponibile;
    }

    public String getTotaleImponibileFormattato()
    {
        return totaleImponibileFormattato;
    }

    public Double getTotaleImposta()
    {
        return totaleImposta;
    }

    public String getTotaleImpstaFormattato()
    {
        return totaleImpstaFormattato;
    }

    public Double getTotaleIndetraibile()
    {
        return totaleIndetraibile;
    }

    public String getTotaleIndetraibileFormattato()
    {
        return totaleIndetraibileFormattato;
    }

    public void setAliquotaIva(Double aliquotaIva)
    {
        this.aliquotaIva = aliquotaIva;
    }

    public void setAliquotaIvaFormattata(String aliquotaIvaFormattata)
    {
        this.aliquotaIvaFormattata = aliquotaIvaFormattata;
    }

    public void setDescrizioneAliquota(String descrizioneAliquota)
    {
        this.descrizioneAliquota = descrizioneAliquota;
    }

    public void setIdAliquotaIva(Integer idAliquotaIva)
    {
        this.idAliquotaIva = idAliquotaIva;
    }

    public void setImponibileMerce(Double imponibileMerce)
    {
        this.imponibileMerce = imponibileMerce;
    }

    public void setImponibileMerceFormattato(String imponibileMerceFormattato)
    {
        this.imponibileMerceFormattato = imponibileMerceFormattato;
    }

    public void setImponibileSpese(Double imponibileSpese)
    {
        this.imponibileSpese = imponibileSpese;
    }

    public void setImponibileSpeseFormattato(String imponibileSpeseFormattato)
    {
        this.imponibileSpeseFormattato = imponibileSpeseFormattato;
    }

    public void setImportoIva(Double importoIva)
    {
        this.importoIva = importoIva;
    }

    public void setImportoIvaFormattato(String importoIvaFormattato)
    {
        this.importoIvaFormattato = importoIvaFormattato;
    }

    public void setTotaleDetraibile(Double totaleDetraibile)
    {
        this.totaleDetraibile = totaleDetraibile;
    }

    public void setTotaleDetraibileFormattato(String totaleDetraibileFormattato)
    {
        this.totaleDetraibileFormattato = totaleDetraibileFormattato;
    }

    public void setTotaleImponibile(Double totaleImponibile)
    {
        this.totaleImponibile = totaleImponibile;
    }

    public void setTotaleImponibileFormattato(String totaleImponibileFormattato)
    {
        this.totaleImponibileFormattato = totaleImponibileFormattato;
    }

    public void setTotaleImposta(Double totaleImposta)
    {
        this.totaleImposta = totaleImposta;
    }

    public void setTotaleImpstaFormattato(String totaleImpstaFormattato)
    {
        this.totaleImpstaFormattato = totaleImpstaFormattato;
    }

    public void setTotaleIndetraibile(Double totaleIndetraibile)
    {
        this.totaleIndetraibile = totaleIndetraibile;
    }

    public void setTotaleIndetraibileFormattato(String totaleIndetraibileFormattato)
    {
        this.totaleIndetraibileFormattato = totaleIndetraibileFormattato;
    }

    public String getTipologiaIva()
    {
        return tipologiaIva;
    }

    public void setTipologiaIva(String tipologiaIva)
    {
        this.tipologiaIva = tipologiaIva;
    }

}
