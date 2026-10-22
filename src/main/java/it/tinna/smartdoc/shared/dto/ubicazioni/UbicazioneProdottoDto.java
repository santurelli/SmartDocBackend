package it.tinna.smartdoc.shared.dto.ubicazioni;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class UbicazioneProdottoDto extends BaseDto {

    @Expose
    private long idProdotto;

    @Expose
    private long idUbicazione;

    @Expose
    private String codUbicazione;

    @Expose
    private String descUbicazione;

    @Expose
    private String corsia;

    @Expose
    private String ripiano;

    @Expose
    private String livello;

    @Expose
    private String dot;

    @Expose
    private Double quantita;

    @Expose
    private String note;

    @Expose
    private String codProdotto;

    @Expose
    private String descProdotto;

    @Expose
    private String marca;

    @Expose
    private String scultura;

    @Expose
    private Double sezione;

    @Expose
    private Double serie;

    @Expose
    private Double diametro;

    @Expose
    private String indiceVelocita;
}
