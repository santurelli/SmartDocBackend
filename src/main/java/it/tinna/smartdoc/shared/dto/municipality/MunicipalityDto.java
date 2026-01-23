package it.tinna.smartdoc.shared.dto.municipality;

import java.io.Serializable;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MunicipalityDto extends BaseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dbName;
    private int fatturaElettronica;
    private String denominazione;
}
