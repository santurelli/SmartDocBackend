package it.tinna.smartdoc.shared.dto.documenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class EsitoSdiDto extends BaseDto
{

    @Expose
    private String descrizioneScarto;

    @Expose
    private String esito;

    @Expose
    private String xml;

}

