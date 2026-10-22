package it.tinna.smartdoc.shared.dto.marche;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class MarcaDto extends BaseDto {

    @Expose
    private String codice;

    @Expose
    private String descrizione;

    @Expose
    private String note;
}
