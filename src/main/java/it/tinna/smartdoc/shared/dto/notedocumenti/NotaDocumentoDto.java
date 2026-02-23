package it.tinna.smartdoc.shared.dto.notedocumenti;

import it.tinna.smartdoc.shared.dto.BaseDto;

public class NotaDocumentoDto extends BaseDto {
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}

