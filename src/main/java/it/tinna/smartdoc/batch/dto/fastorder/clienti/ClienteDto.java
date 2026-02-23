package it.tinna.smartdoc.batch.dto.fastorder.clienti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.fastorder.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteDto extends BaseDto
{

    private static final long serialVersionUID = 5253825468807393961L;

    @Expose
    private String            cap;

    private String            cellulare;

    @Expose
    private String            citta;

    private String            codice;

    @Expose
    private String            codiceDestinatario;

    @Expose
    private String            codiceFiscale;

    @Expose
    private String            denominazione;

    @Expose
    private String            email;

    private String            fax;

    @Expose
    private String            indirizzo;

    @Expose
    private String            partitaIva;

    @Expose
    private String            pec;

    @Expose
    private String            provincia;

    private String            telefono;

}

