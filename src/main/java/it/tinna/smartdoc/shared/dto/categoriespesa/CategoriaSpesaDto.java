package it.tinna.smartdoc.shared.dto.categoriespesa;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class CategoriaSpesaDto extends BaseDto {
    private String descrizione;
    private Integer predefinita;
}

