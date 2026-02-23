package it.tinna.smartdoc.batch.dto.justdesign.documenti.fatturescontrino;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.justdesign.documenti.DocumentoDto;
import it.tinna.smartdoc.batch.dto.justdesign.magazzino.ArticoloInDocumentoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FatturaScontrinoDto extends DocumentoDto
{

    @Expose
    private ArrayList<ArticoloInDocumentoDto> articoli;

    @Expose
    private String                            cap;

    @Expose
    private String                            citta;

    @Expose
    private String                            cliente;           // nome del cliente a cui è associata la fattura

    @Expose
    private String                            codiceDestinatario;

    @Expose
    private String                            codiceFiscale;

    @Expose
    private String                            dataScontrino;

    private String                            idScontrino;       // id della vendita al banco

    @Expose
    private String                            indirizzo;

    @Expose
    private String                            numScontrino;

    @Expose
    private String                            partitaIva;

    @Expose
    private String                            pec;

    @Expose
    private String                            provincia;

    @Expose
    private BigDecimal                        totIva;

}

