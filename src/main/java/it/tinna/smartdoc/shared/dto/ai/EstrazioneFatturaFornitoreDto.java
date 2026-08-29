package it.tinna.smartdoc.shared.dto.ai;

import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Risultato dell'estrazione AI di una fattura fornitore da immagine/PDF. Dati "grezzi" da
 * proporre in revisione all'utente nel form di Fattura Fornitore: nessun salvataggio automatico.
 */
@Getter
@Setter
@NoArgsConstructor
public class EstrazioneFatturaFornitoreDto {

    @Expose
    private String denominazioneFornitore;
    @Expose
    private String partitaIvaFornitore;
    @Expose
    private String codiceFiscaleFornitore;
    @Expose
    private String numeroDocumento;
    @Expose
    private String dataDocumento; // dd/MM/yyyy, come nel resto dell'app
    @Expose
    private Double totaleImponibile;
    @Expose
    private Double totaleIva;
    @Expose
    private Double totaleDocumento;
    @Expose
    private List<EstrazioneRigaDto> righe;

    // Valorizzato server-side se un fornitore con questa Partita IVA esiste gia' in anagrafica.
    @Expose
    private Integer idFornitoreTrovato;
    @Expose
    private String denominazioneFornitoreTrovato;

    @Expose
    private String erroreEstrazione;
}
