package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;
// import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto; // Create if needed or comment out for now
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
public class DocumentoDto extends BaseDto {

    private String abi;
    private String abiNsBanca;
    private Double acconto;

    @Expose
    @SerializedName("descAgente")
    private String agente;

    @Expose
    @SerializedName("agente")
    private AgenteDto agenteDto;

    @Expose
    private String annotazioneEstesa;

    private String bic;
    private String bicNsBanca;
    private String cab;
    private String cabNsBanca;

    @Expose
    private String capDestinazione;

    @Expose
    private String capIntestazione;

    @Expose
    private String causale;

    private boolean checked;

    @Expose
    private String cig;

    private String cin;
    private String cinNsBanca;

    @Expose
    private String cittaDestinazione;

    @Expose
    private String cittaIntestazione;

    @Expose
    @SerializedName("cliente")
    private ClienteDto clienteDto;

    private String codAgente;

    @Expose
    private String codiceFiscale;

    @Expose
    private String codiceUfficioDestinazione;

    @Expose
    private Integer colli;

    private String conto;
    private String contoNsBanca;

    @Expose
    private String cup;

    @Expose
    private String dataDocumento;

    @Expose
    private String dataOrdineAcquisto;

    @Expose
    private String datiCommessa;

    @Expose
    private String denominazioneCliente;

    private String descrizioneBanca;

    @Expose
    private String descrizioneListino;

    private String descrizioneNsBanca;

    private Integer esigibilitaDifferita;
    private Integer flFatturaElettronica;

    private String iban;
    private String ibanNsBanca;

    private Integer idAgente;
    private Integer idAspettoBeni;
    private Integer idCausaleEsigibilitaDifferita;
    private Integer idCausaleTrasporto;

    @Expose
    private Integer idCliente;

    private Integer idListino;
    private Integer idMagazzino;

    @Expose
    private Integer idNsBanca;

    @Expose
    private Integer idProgetto;

    @Expose
    private Integer idTipoPagamento;

    @Expose
    private Integer idTipoPorto;

    @Expose
    private Integer idVettore;

    private BigDecimal importoRitenutaAcconto;

    @Expose
    private String indirizzoDestinazione;

    @Expose
    private String indirizzoIntestazione;

    // private List<ScadenzaPagamentoDocumentoDto> listaScadenzePagamentiDocumento;

    private String modalitaPagamento;

    @Expose
    private String nazioneDestinazione;

    @Expose
    private String nazioneIntestazione;

    @Expose
    private Integer numDocumento;

    @Expose
    private String numeroOrdineAcquisto;

    @Expose
    private String particella;

    @Expose
    private String partitaIva;

    @Expose
    private String pec;

    private Double percProvvigioneAgente;
    private Double percRitenutaPrevidenziale;

    @Expose
    private Double pesoLordo;

    @Expose
    private Double pesoNetto;

    @Expose
    private List<ProdottoDocumentoDto> prodotti;

    @Expose
    @SerializedName("progetto")
    private ProgettoDto progettoDto;

    @Expose
    private String provinciaDestinazione;

    @Expose
    private String provinciaIntestazione;

    private String sconto;

    @Expose
    private Double totale;

    @Expose
    private Double totaleDaPagare;

    @Expose
    private Double totaleIva;

    private Double totalePagato;
}
