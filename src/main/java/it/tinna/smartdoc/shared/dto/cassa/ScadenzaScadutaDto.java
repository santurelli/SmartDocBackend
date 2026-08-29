package it.tinna.smartdoc.shared.dto.cassa;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScadenzaScadutaDto {

    @Expose
    private String tipo; // INCASSO | PAGAMENTO
    @Expose
    private String tipoDocumento; // Fattura, Nota Credito, Fattura Fornitore, Nota Credito Fornitore
    @Expose
    private String numeroDocumento;
    @Expose
    private String soggetto;
    @Expose
    private String dataScadenza;
    @Expose
    private BigDecimal importo;
    @Expose
    private Integer giorniRitardo;
}
