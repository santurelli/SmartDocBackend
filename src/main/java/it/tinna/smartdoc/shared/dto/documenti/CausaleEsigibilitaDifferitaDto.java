package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class CausaleEsigibilitaDifferitaDto extends BaseDto {
    private String descrizione;
    private Long totalCount;
}

