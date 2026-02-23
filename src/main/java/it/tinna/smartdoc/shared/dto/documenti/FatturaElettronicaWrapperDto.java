package it.tinna.smartdoc.shared.dto.documenti;

import java.io.File;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString(callSuper = false, onlyExplicitlyIncluded = true, includeFieldNames = true)
public class FatturaElettronicaWrapperDto extends BaseDto
{
    @ToString.Include
    private FatturaElettronicaDto fattura;

    private byte[]                flussoFatturaElettronica;

    private File                  fileFatturaDaInviareSDI;

    // rappresenta l'id della fattura elettroncia nella tabella d_e_fatture_elettroniche del service db
    private long                  idFatturaElettronica;

    private String                nomeFileFattura;

    private String                progressivoFile;

}

