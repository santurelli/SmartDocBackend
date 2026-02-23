package it.tinna.smartdoc.batch.dto.fastorder.documenti;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.fastorder.clienti.ClienteDto;
import it.tinna.smartdoc.batch.dto.fastorder.comande.PagamentoComandaDto;
import it.tinna.smartdoc.batch.dto.fastorder.comande.PiattoComandaDto;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class FatturaDto extends DocumentoDto
{

    private String                 cap;

    private String                 citta;

    @Expose
    private ClienteDto             cliente;

    private String                 codiceFiscale;

    private String                 dtScontrino;

    private BigDecimal             imponibile;

    private String                 indirizzo;

    private BigDecimal             iva;

    private int                    numScontrino;

    private String                 partitaIva;

    @Expose
    private PagamentoComandaDto    pagamentoComandaDto;

    @Expose
    private List<PiattoComandaDto> piatti;

    private String                 provincia;

    @Expose
    private String                 testoModelloStampa;

    private BigDecimal             totale;

}

