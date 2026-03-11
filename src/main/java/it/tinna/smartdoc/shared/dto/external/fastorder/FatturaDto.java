package it.tinna.smartdoc.shared.dto.external.fastorder;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FatturaDto extends DocumentoDto {
    private static final long serialVersionUID = 1L;

    private String cap;
    private String citta;
    private ClienteDto cliente;
    private String codiceFiscale;
    private String dtScontrino;
    private BigDecimal imponibile;
    private String indirizzo;
    private BigDecimal iva;
    private int numScontrino;
    private String partitaIva;
    private PagamentoComandaDto pagamentoComandaDto;
    private List<PiattoComandaDto> piatti;
    private String provincia;
    private String testoModelloStampa;
    private BigDecimal totale;
}
