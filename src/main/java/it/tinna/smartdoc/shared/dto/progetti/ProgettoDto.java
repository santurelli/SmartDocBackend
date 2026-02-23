package it.tinna.smartdoc.shared.dto.progetti;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProgettoDto extends BaseDto {
    private String descrizione;
    private String codice;
    private String note;
}

