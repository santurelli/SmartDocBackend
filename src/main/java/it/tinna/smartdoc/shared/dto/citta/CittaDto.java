package it.tinna.smartdoc.shared.dto.citta;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CittaDto extends BaseDto {
    private static final long serialVersionUID = 1L;

    private String cap;
    private String nome;
    private String provincia;
}
