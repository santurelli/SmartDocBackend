package it.tinna.smartdoc.batch.dto;

import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "nomePacchetto")
public class DatiFatturaInviataSdiDto
{

    private long              idFattura;

    private long              idFatturaElettronica;

    private String            nomePacchetto;

    private TipoDocumentoEnum tipoDocumento;

    private String            progressivoFile;

}

