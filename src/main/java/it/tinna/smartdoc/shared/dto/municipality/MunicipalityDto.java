package it.tinna.smartdoc.shared.dto.municipality;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class MunicipalityDto extends BaseDto
{

    private Integer clienteAttivo;

    private String  dbName;

    private String  dtAcquistoFattureElettroniche;

    private int     fatturaElettronica;

    @Expose
    private String  label;

    private Integer maxFatture;

    private Integer tipoAccount;

}

