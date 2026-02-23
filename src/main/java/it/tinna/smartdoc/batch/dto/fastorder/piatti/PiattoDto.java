package it.tinna.smartdoc.batch.dto.fastorder.piatti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.fastorder.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class PiattoDto extends BaseDto
{

    private String  codiceBarre;

    private String  descrCategoria;

    private String  descrDe;

    private String  descrEn;

    private String  descrEs;

    private String  descrFr;

    private String  descrizione;

    private String  descrPalmare;

    private String  descrPulsante;

    private Integer flEscludiServizio;

    private Integer flPreferito;

    private Integer flStampaConto;

    @Expose
    private Integer flVariante;

    @Expose
    private Integer flVarianteLibera;

    private Integer idCategoria;

    private Double  iva;

    private String  note;

    @Expose
    private Double  prezzo;

}

