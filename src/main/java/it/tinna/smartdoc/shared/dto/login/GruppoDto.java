package it.tinna.smartdoc.shared.dto.login;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GruppoDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String value;
}
