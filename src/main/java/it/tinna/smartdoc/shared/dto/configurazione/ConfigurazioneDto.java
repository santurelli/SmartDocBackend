package it.tinna.smartdoc.shared.dto.configurazione;

import java.io.Serializable;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfigurazioneDto extends BaseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String dominio;
    private String chiave;
    private String valore;
}
