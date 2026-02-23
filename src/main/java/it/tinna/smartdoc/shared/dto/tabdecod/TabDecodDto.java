package it.tinna.smartdoc.shared.dto.tabdecod;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class TabDecodDto extends BaseDto
{
    @Expose
    private String codice;

    @Expose
    private String descrizione;

    public String getCodice()
    {
        return codice;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public void setCodice(String codice)
    {
        this.codice = codice;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

}

