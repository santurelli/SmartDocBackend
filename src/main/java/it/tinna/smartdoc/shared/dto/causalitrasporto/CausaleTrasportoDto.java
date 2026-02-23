package it.tinna.smartdoc.shared.dto.causalitrasporto;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class CausaleTrasportoDto extends BaseDto
{

    @Expose
    private String descrizione;

    @Expose
    private int    predefinita;

    public String getDescrizione()
    {
        return descrizione;
    }

    public int getPredefinita()
    {
        return predefinita;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setPredefinita(int predefinita)
    {
        this.predefinita = predefinita;
    }

}

