package it.tinna.smartdoc.shared.dto.documenti;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class OrdineDto extends DocumentoAcquistoDto {

    @Expose
    private String statoOrdine;

}

