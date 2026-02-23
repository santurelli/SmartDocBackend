package it.tinna.smartdoc.shared.dto.avvisi;

import it.tinna.smartdoc.shared.dto.BaseDto;

public class AvvisoDto extends BaseDto {
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}

