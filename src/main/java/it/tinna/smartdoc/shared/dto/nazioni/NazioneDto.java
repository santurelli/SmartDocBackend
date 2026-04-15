package it.tinna.smartdoc.shared.dto.nazioni;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NazioneDto extends BaseDto {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String codiceIso;
}
