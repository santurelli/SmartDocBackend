package it.tinna.smartdoc.shared.dto.divisioni;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class DivisioneDto extends BaseDto
{

    @Expose
    private String descrizione;

    public String getDescrizione()
    {
        return descrizione;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

}

