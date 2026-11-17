package it.tinna.smartdoc.shared.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoSaveResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer numDocumento;
    private boolean numeroRiassegnato;
    private Integer numeroOriginale;

}
