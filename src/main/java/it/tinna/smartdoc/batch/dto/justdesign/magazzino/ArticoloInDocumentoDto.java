package it.tinna.smartdoc.batch.dto.justdesign.magazzino;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArticoloInDocumentoDto extends Articolo
{

    // private Double iva;
    @Expose
    private String     dataDocumento;

    @Expose
    private long       idDocumento;

    @Expose
    private int        idTipoDocumento;

    @Expose
    private String     importoSconto;

    @Expose
    private String     numDocumento;

    @Expose
    private String     quantitaInDocumento;

    @Expose
    private String     sconto;

    @Expose
    private double     scontoCorpo;

    @Expose
    private BigDecimal totale;

}

