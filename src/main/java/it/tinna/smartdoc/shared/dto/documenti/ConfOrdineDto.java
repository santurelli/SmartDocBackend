package it.tinna.smartdoc.shared.dto.documenti;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class ConfOrdineDto extends DocumentoDto {

    @Expose
    private String descCausaleTrasporto;

    @Expose
    private String descTipoPorto;

    @Expose
    private String descVettore;

    @Expose
    private String descAspettoBeni;

    @Expose
    private String descTipoPagamento;

    @Expose
    private String descrMagazzino;

    @Expose
    private String dataOraTrasporto;

    @Expose
    private String targa;

    @Expose
    private Integer pallet;

    @Expose
    private Long idDocAssociato;

    @Expose
    private String tipoDocAssociato;
}

