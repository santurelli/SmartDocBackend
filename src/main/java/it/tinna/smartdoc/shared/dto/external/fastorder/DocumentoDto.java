package it.tinna.smartdoc.shared.dto.external.fastorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocumentoDto extends BaseDto {
    private static final long serialVersionUID = 1L;

    private String data;
    private String descCliente;
    private Integer idCliente;
    private Integer idScontrino;
    private Integer numero;
    private String numeroPrint;
    private String suffisso;
}
