package it.tinna.smartdoc.shared.dto.external.fastorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteDto extends BaseDto {
    private static final long serialVersionUID = 1L;

    private String cap;
    private String cellulare;
    private String citta;
    private String codice;
    private String codiceDestinatario;
    private String codiceFiscale;
    private String denominazione;
    private String email;
    private String fax;
    private String indirizzo;
    private String partitaIva;
    private String pec;
    private String provincia;
    private String nazione;
    private String telefono;
}
