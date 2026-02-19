package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
public class DocumentoDto extends BaseDto {

    @Expose
    private String abi;
    @Expose
    private String abiNsBanca;
    @Expose
    private Double acconto;

    @Expose
    @SerializedName("descAgente")
    private String agente;

    @Expose
    private String nomeAgente;

    @Expose
    @SerializedName("agente")
    private AgenteDto agenteDto;

    @Expose
    private String annotazioneEstesa;

    @Expose
    private String bic;
    @Expose
    private String bicNsBanca;
    @Expose
    private String cab;
    @Expose
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

    @Expose
    private String cin;
    @Expose
    private String cinNsBanca;

    @Expose
    private String cittaDestinazione;

    @Expose
    private String cittaIntestazione;

    @Expose
    @SerializedName("cliente")
    private ClienteDto clienteDto;

    @Expose
    private String codAgente;

    @Expose
    private String codiceFiscale;

    @Expose
    private String codiceUfficioDestinazione;

    @Expose
    private Integer colli;

    @Expose
    private String conto;
    @Expose
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
    private String dtLiquidazioneProvvigione;

    @Expose
    private String denominazioneCliente;

    @Expose
    private String nomeCliente;

    @Expose
    private String descrizioneBanca;

    @Expose
    private String descrizioneListino;

    @Expose
    private String descrizioneNsBanca;

    @Expose
    private String nomeProgetto;

    @Expose
    private Integer esigibilitaDifferita;
    @Expose
    private Integer flFatturaElettronica;

    @Expose
    private String iban;
    @Expose
    private String ibanNsBanca;

    @Expose
    private Integer idAgente;
    @Expose
    private Integer idAspettoBeni;
    @Expose
    private Integer idCausaleEsigibilitaDifferita;
    @Expose
    private Integer idCausaleTrasporto;

    @Expose
    private Integer idCliente;

    @Expose
    private Integer idListino;
    @Expose
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

    @Expose
    private BigDecimal importoRitenutaAcconto;

    @Expose
    private Integer flRitenutaAcconto;

    @Expose
    private Double percRitenutaAcconto;

    @Expose
    private String indirizzoDestinazione;

    @Expose
    private String indirizzoIntestazione;

    @Expose
    private List<ScadenzaPagamentoDocumentoDto> listaScadenzePagamentiDocumento;

    @Expose
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
    private Integer pallet;

    @Expose
    private String targa;

    @Expose
    private String dataOraTrasporto;

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
    private List<SpesaIncassoDocumentoDto> listaSpeseIncassoFattura;

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

    @Expose
    private String fatturareA;

    @Expose
    private Integer idDocAssociato;

    @Expose
    private String tipoDocAssociato;

    // Reporting fields
    @Expose
    private String luogoDestinazione;
    @Expose
    private String descTipoPorto;
    @Expose
    private String descCausaleTrasporto;
    @Expose
    private String descVettore;
    @Expose
    private String descAspettoBeni;
    @Expose
    private String descPesoLordo;
    @Expose
    private String descTipoPagamento;
}
