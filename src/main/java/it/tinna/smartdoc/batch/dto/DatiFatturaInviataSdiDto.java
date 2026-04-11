package it.tinna.smartdoc.batch.dto;

import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "nomePacchetto")
public class DatiFatturaInviataSdiDto implements Serializable
{
    private static final long serialVersionUID = 1L;

    private long              idFattura;

    private long              idFatturaElettronica;

    private String            nomePacchetto;

    private TipoDocumentoEnum tipoDocumento;

    private String            progressivoFile;

}

