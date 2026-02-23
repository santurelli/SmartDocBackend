package it.tinna.smartdoc.shared.dto.causaliesigibilitadifferita;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class CausaleEsigibilitaDifferitaDto extends BaseDto
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

