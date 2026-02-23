package it.tinna.smartdoc.batch.dto.fastorder.documenti;

import it.tinna.smartdoc.batch.dto.fastorder.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class DocumentoDto extends BaseDto
{

    private String  data;

    private String  descCliente;

    private Integer idCliente;

    public Integer  idScontrino;

    public Integer  numero;

    public String   numeroPrint;

}

