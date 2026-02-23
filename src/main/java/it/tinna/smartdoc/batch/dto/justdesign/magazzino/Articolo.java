package it.tinna.smartdoc.batch.dto.justdesign.magazzino;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.justdesign.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = "id")
public class Articolo extends BaseDto
{

    private String codice;

    private String codiceBarre;

    @Expose
    private String codPerFornitore;

    private String confezione;

    @Expose
    private String costo;

    private String descrEng;

    @Expose
    private String descrIta;

    @Expose
    private String flDeleted;

    private String fornitore;

    @Expose
    private String id;

    @Expose
    private Double iva;

    private String nomeFornitore;

    private String note;

    @Expose
    private String prezzo;

    @Expose
    private String quantita;

    private int    updFromWsAccepted;
}

