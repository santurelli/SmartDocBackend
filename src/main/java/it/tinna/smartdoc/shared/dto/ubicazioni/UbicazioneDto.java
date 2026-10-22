package it.tinna.smartdoc.shared.dto.ubicazioni;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class UbicazioneDto extends BaseDto {

    @Expose
    private String codUbicazione;

    @Expose
    private String descrizione;

    @Expose
    private String corsia;

    @Expose
    private String ripiano;

    @Expose
    private String livello;

    @Expose
    private String note;

    @Expose
    private int totalePneumatici;
}
