package it.tinna.smartdoc.shared.dto.causalicontabili;

import java.util.List;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class CausaleContabileDto extends BaseDto
{

    private String                codice;

    private String                descrizione;

    private String                partiteAperte;

    private boolean               flSistema;

    private List<ContoCausaleDto> elencoContiCausale;

    public String getCodice()
    {
        return codice;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public List<ContoCausaleDto> getElencoContiCausale()
    {
        return elencoContiCausale;
    }

    public String getPartiteAperte()
    {
        return partiteAperte;
    }

    public boolean isFlSistema()
    {
        return flSistema;
    }

    public void setCodice(String codice)
    {
        this.codice = codice;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setElencoContiCausale(List<ContoCausaleDto> elencoContiCausale)
    {
        this.elencoContiCausale = elencoContiCausale;
    }

    public void setFlSistema(boolean flSistema)
    {
        this.flSistema = flSistema;
    }

    public void setPartiteAperte(String partiteAperte)
    {
        this.partiteAperte = partiteAperte;
    }

}

