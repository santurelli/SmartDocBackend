package it.tinna.smartdoc.shared.dto.external.fastorder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import it.tinna.smartdoc.shared.dto.categorie.CategoriaDto;

@Getter
@Setter
@NoArgsConstructor
public class PiattoComandaDto extends PiattoDto {
    private static final long serialVersionUID = 1L;

    private CategoriaDto categoriaDto;
    private String descrizionePiatto;
    private Integer flPagato;
    private Integer idComanda;
    private Integer idDestinazioneStampa;
    private Integer idPiatto;
    private Integer idRifVariante;
    private Integer quantita;
    private Integer quantitaPagato;
    private boolean variante;
    private Double costoPiatto;
    private Double totIncassato;
    private boolean varianteLibera;
    private String descrizioneVarianteLibera;
}
