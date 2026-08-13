package it.tinna.smartdoc.shared.dto.documenti;

import com.google.gson.annotations.Expose;
import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class ProdottoDocumentoDto extends BaseDto {

    private String codiceBarre;

    @Expose
    private String codiceProdotto;

    private String descColore;
    private String descConto;

    @Expose
    private String descProdotto;

    @Expose
    private String descrCalibro;

    @Expose
    private String descrFormato;

    @Expose
    private String descrScelta;

    @Expose
    private String descrTono;

    @Expose
    private String descUnitaMisura;

    private String fmCodice;
    private String fmColore;

    @Expose
    private String fmDescrizione;

    private String fmScelta;
    private String fmTaglia;
    private String fmTono;
    private String fmUnitaMisura;

    @Expose
    private boolean fuoriMagazzino;

    private Double guadagno;

    @Expose
    private Integer idAliquotaIva;

    private Integer idColore;
    private Integer idConto;

    /**
     * Override manuale del conto contabile per questa riga (Fase 3+): se valorizzato, il motore di
     * generazione automatica delle scritture lo usa al posto della cascata articolo/sottocategoria/categoria.
     * Non e' idConto (campo preesistente per una funzionalita' diversa, non contabile).
     */
    @Expose
    private Integer idContoOverride;

    @Expose
    private Long idDivisione;

    private long idDocumento;

    @Expose
    private Integer idProdotto;

    private Integer idScelta;
    private Integer idTaglia;
    private Integer idTono;

    @Expose
    private Integer idUnitaMisura;

    @Expose
    private String nota;

    @Expose
    private String notaProdotto;

    @Expose
    private Double percentualeIva;

    @Expose
    private Double prezzo;

    @Expose
    private Double prezzoVenditaIvato;

    @Expose
    private double prezzoImponibile;

    private boolean prodotto;

    @Expose
    private ProdottoDto prodottoDto;

    private Double provvigione;

    @Expose
    private Double quantita;

    @Expose
    private Integer scarica;

    @Expose
    private String sconto;

    @Expose
    private String tipologia;

    @Expose
    private Double totale;

    private Double totaleSenzaIva;

    @Expose
    private String descFornitore; // added explicitly based on legacy even if private in BaseDto? No, BaseDto doesn't have it.

    @Expose
    private String data;

    // Report fields
    private String quantitaFormattata;
    private String percentualeIvaFormattata;
    private String prezzoFormattato;
    private String totaleFormattato;

    // Field expected by report for description
    private String descrizione;

    @Expose
    private Integer flRitenuta;
}

