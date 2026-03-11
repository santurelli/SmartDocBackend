package it.tinna.smartdoc.shared.dto.external.fastorder;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class BaseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private boolean checked;
    private String dtCreated;
    private String dtLastUpdate;
    private String errorNum;
    private String errorText;
    private String nomeUserCreated;
    private String nomeUserLastUpdate;
    private Integer userCreated;
    private Integer userLastUpdate;
}
