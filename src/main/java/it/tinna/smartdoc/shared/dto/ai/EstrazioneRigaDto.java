package it.tinna.smartdoc.shared.dto.ai;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstrazioneRigaDto {

    @Expose
    private String descrizione;
    @Expose
    private Double quantita;
    @Expose
    private Double prezzoUnitario;
    @Expose
    private Double aliquotaIva;

    // Suggerimento di abbinamento articolo esistente (per similarita' di descrizione).
    // Mai applicato automaticamente: l'utente deve confermarlo esplicitamente in UI.
    @Expose
    private Integer idProdottoSuggerito;
    @Expose
    private String codiceProdottoSuggerito;
    @Expose
    private String descrizioneProdottoSuggerito;
}
