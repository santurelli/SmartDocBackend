package it.tinna.smartdoc.shared.dto.external.fastorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PiattoDto extends BaseDto {
    private static final long serialVersionUID = 1L;

    private String codiceBarre;
    private String descrCategoria;
    private String descrDe;
    private String descrEn;
    private String descrEs;
    private String descrFr;
    private String descrizione;
    private String descrPalmare;
    private String descrPulsante;
    private Integer flEscludiServizio;
    private Integer flPreferito;
    private Integer flStampaConto;
    private Integer flVariante;
    private Integer flVarianteLibera;
    private Integer idCategoria;
    private Double iva;
    private String note;
    private Double prezzo;
}
