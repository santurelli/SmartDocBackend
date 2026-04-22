package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
import lombok.ToString;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
public class DocumentoDto extends BaseDto
{

    public enum TipoDoc
    {
     BOLLE_CARICO("B", "Arrivo merce"),
     CONF_ORDINE("C", "Conf. ordine"),
     DDT("D", "Doc. trasporto"),
     DOC_ACQUISTO("A", ""),
     DOC_VENDITA("V", ""),
     FATTURE("F", "Fattura"),
     FATTURE_FORNITORE("FF", "Fattura fornitore"),
     NOTA_CREDITO_CLIENTE("NCC", "Nota di credito"),
     NOTA_CREDITO_FORNITORE("NCF", "Nota di credito fornitore"),
     ORDINI_FORNITORE("O", "Ordine fornitore"),
     PREVENTIVI("P", "Preventivo"),
     SCONTRINI("S", "Scontrini");

        private static Map<String, TipoDoc> valueToDescrMap;

        public static TipoDoc getDescrizioneByValue(String value)
        {
            if ( valueToDescrMap == null )
            {
                initMapping();
            }
            return valueToDescrMap.get(value);
        }

        private static void initMapping()
        {
            valueToDescrMap = new HashMap<>();
            for ( TipoDoc s : values() )
            {
                valueToDescrMap.put(s.valore, s);
            }
        }

        private String descrizione;

        private String valore;

        TipoDoc(String valore,
                String descrizione)
        {
            this.valore = valore;
            this.descrizione = descrizione;
        }

        public String getDescrizione()
        {
            return descrizione;
        }

        public String getValore()
        {
            return this.valore;
        }
    }

    private String                              abi;

    private String                              abiNsBanca;

    private Double                              acconto;

    @Expose
    @SerializedName("descAgente")
    private String                              agente;

    @Expose
    @SerializedName("agente")
    private AgenteDto                           agenteDto;

    @Expose
    private String                              annotazioneEstesa;

    private String                              bic;

    private String                              bicNsBanca;

    private String                              cab;

    private String                              cabNsBanca;

    @Expose
    private String                              capDestinazione;

    @Expose
    private String                              capIntestazione;

    @Expose
    private String                              causale;

    @Expose
    private String                              fatturareA;

    @Expose
    private String                              luogoDestinazione;

    private boolean                             checked;

    @Expose
    private String                              cig;

    private String                              cin;

    private String                              cinNsBanca;

    @Expose
    private String                              cittaDestinazione;

    @Expose
    private String                              cittaIntestazione;

    @Expose
    @SerializedName("cliente")
    private ClienteDto                          clienteDto;

    private String                              codAgente;

    @Expose
    private String                              codiceFiscale;

    @Expose
    private String                              codiceUfficioDestinazione;

    @Expose
    private Integer                             colli;

    private String                              conto;

    private String                              contoNsBanca;

    @Expose
    private String                              cup;

    @Expose
    private String                              dataDocumento;

    private String                              dataOraTrasporto;

    @Expose
    private String                              dataOrdineAcquisto;

    @Expose
    private String                              datiCommessa;

    @Expose
    private String                              denominazioneCliente;

    private String                              descAspettoBeni;

    private String                              descCausaleEsigibilitaDifferita;

    private String                              descCausaleTrasporto;

    private String                              descPesoLordo;

    private String                              descPesoNetto;

    private String                              descrContropartitaRitenutaPrevidenziale;

    private String                              descrizioneBanca;

    @Expose
    private String                              descrizioneListino;

    private String                              descrizioneNsBanca;

    private String                              descrMagazzino;

    private String                              descTipoPagamento;

    private String                              descTipoPorto;

    private String                              descVettore;

    private String                              dtLiquidazioneProvvigione;

    @ToString.Include
    private String                              erroreValidazioneXml;

    @Expose
    @ToString.Include
    private String                              erroreConsegna;

    @Expose
    private Integer                             esigibilitaDifferita;

    @Expose
    private Integer                             flFatturaElettronica;

    private String                              iban;

    private String                              ibanNsBanca;

    private Integer                             idAgente;

    @Expose
    private Integer                             idAspettoBeni;

    @Expose
    private Integer                             idCausaleEsigibilitaDifferita;

    @Expose
    private Integer                             idCausaleTrasporto;

    @Expose
    private Integer                             idCliente;

    private Integer                             idContropartitaRitenutaPrevidenziale;

    private Integer                             idListino;

    private Integer                             idMagazzino;

    @Expose
    private Integer                             idNsBanca;

    @Expose
    private Integer                             idProgetto;

    private Integer                             idRitenutaPrevidenziale;

    @Expose
    private Integer                             idTipoPagamento;

    @Expose
    private Integer                             idTipoPorto;

    @Expose
    private Integer                             idVettore;

    @Expose
    private Integer                             flRitenutaAcconto;

    @Expose
    private Double                              percRitenutaAcconto;

    @Expose
    private BigDecimal                          importoRitenutaAcconto;

    @Expose
    private String                              tipoRitenuta;

    @Expose
    private Integer                             flRivalsaInps;

    @Expose
    private Double                              percRivalsaInps;

    @Expose
    private BigDecimal                          importoRivalsaInps;

    @Expose
    private String                              tipoCassaInps;

    @Expose
    private String                              indirizzoDestinazione;

    @Expose
    private String                              indirizzoIntestazione;

    @Expose
    @SerializedName("scadenze")
    private List<ScadenzaPagamentoDocumentoDto> listaScadenzePagamentiDocumento;

    @Expose
    private List<SpesaIncassoDocumentoDto>      listaSpeseIncassoFattura;

    private String                              modalitaPagamento;

    @Expose
    private String                              nazioneDestinazione;

    @Expose
    private String                              nazioneIntestazione;

    private String                              nomeFileFattura;

    @Expose
    private Integer                             numDocumento;

    @Expose
    private String                              numeroOrdineAcquisto;

    @Expose
    private Integer                             pallet;

    @Expose
    private String                              particella;

    @Expose
    private String                              partitaIva;

    @Expose
    private String                              pec;

    private Double                              percProvvigioneAgente;

    private Double                              percRitenutaPrevidenziale;

    @Expose
    private Double                              pesoLordo;

    @Expose
    private Double                              pesoNetto;

    @Expose
    private List<ProdottoDocumentoDto>          prodotti;

    @Expose
    @SerializedName("progetto")
    private ProgettoDto                         progettoDto;

    private String                              progFileFatturaElettronica;

    private Integer                             progInvioFatturaElettronica;

    @Expose
    private String                              provinciaDestinazione;

    @Expose
    private String                              provinciaIntestazione;

    @Expose
    private String                              sconto;

    @Expose
    private Integer                                 splitPayment;

    @Expose
    private StatoFatturaElettronica             statoFatturaElettronica;

    @Expose
    private String                              targa;

    private String                              tipoComunicazione;

    private String                              tipoDocumento;

    @Expose
    private Double                              totale;

    @Expose
    private Double                              totaleDaPagare;

    @Expose
    private Double                              totaleIva;

    private Double                              totalePagato;

    @ToString.Include
    private String                              xmlFattura;

    @ToString.Include
    private String                              xmlNonValido;

    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof DocumentoDto )
        {
            return ((DocumentoDto) obj).getId() == this.getId();
        }
        return false;
    }

    public String getFatturareA()
    {
        StringBuilder fatturareA = new StringBuilder("");
        if ( this.clienteDto != null )
        {
            fatturareA.append("<b>").append(clienteDto.getDenominazione()).append("</b><br>");
            if ( StringUtils.isNotBlank(this.partitaIva) )
            {
                fatturareA.append("P.IVA: ").append(this.partitaIva).append("<br>");
            }
            if ( StringUtils.isNotBlank(this.codiceFiscale) )
            {
                fatturareA.append("C.F.: ").append(this.codiceFiscale).append("<br>");
            }
        }
        if ( StringUtils.isNotBlank(this.indirizzoIntestazione) )
        {
            fatturareA.append(this.indirizzoIntestazione).append("<br>");
        }
        if ( StringUtils.isNotBlank(this.capIntestazione) )
        {
            fatturareA.append(this.capIntestazione);
            if ( StringUtils.isNotBlank(this.cittaIntestazione) )
            {
                fatturareA.append(" - ");
            }
            else if ( StringUtils.isNotBlank(this.provinciaIntestazione) )
            {
                fatturareA.append(" ");
            }
            else
            {
                fatturareA.append("<br>");
            }
        }
        if ( StringUtils.isNotBlank(this.cittaIntestazione) )
        {
            fatturareA.append(this.cittaIntestazione);
            if ( StringUtils.isNotBlank(this.provinciaIntestazione) )
            {
                fatturareA.append(" ");
            }
            else
            {
                fatturareA.append("<br>");
            }
        }
        if ( StringUtils.isNotBlank(this.provinciaIntestazione) )
        {
            fatturareA.append("(").append(this.provinciaIntestazione).append(")").append("<br>");
        }
        if ( StringUtils.isNotBlank(this.nazioneIntestazione) )
        {
            fatturareA.append(this.nazioneIntestazione);
        }

        return fatturareA.toString();
    }

    public String getLuogoDestinazione()
    {
        // StringBuilder luogoDestinazione = new StringBuilder("This text field element contains styled text displaying the text-only version of the <style size=\"12\" isBold=\"true\" forecolor=\"black\">^<style forecolor=\"#808080\">Jasper</style><style forecolor=\"#990000\">Reports</style></style> logo and some <font size=\"10\"><sup>superscript</sup></font> text and <font size=\"10\"><sub>subscript</sub></font> text.");
        StringBuilder luogoDestinazione = new StringBuilder("");
        if ( this.clienteDto != null )
        {
            luogoDestinazione.append("<b>").append(clienteDto.getDenominazione()).append("</b><br>");
        }
        if ( StringUtils.isNotBlank(this.indirizzoDestinazione) )
        {
            luogoDestinazione.append(this.indirizzoDestinazione).append("<br>");
        }
        if ( StringUtils.isNotBlank(this.capDestinazione) )
        {
            luogoDestinazione.append(this.capDestinazione);
            if ( StringUtils.isNotBlank(this.cittaDestinazione) )
            {
                luogoDestinazione.append(" - ");
            }
            else if ( StringUtils.isNotBlank(this.provinciaDestinazione) )
            {
                luogoDestinazione.append(" ");
            }
            else
            {
                luogoDestinazione.append("<br>");
            }
        }
        if ( StringUtils.isNotBlank(this.cittaDestinazione) )
        {
            luogoDestinazione.append(this.cittaDestinazione);
            if ( StringUtils.isNotBlank(this.provinciaDestinazione) )
            {
                luogoDestinazione.append(" ");
            }
            else
            {
                luogoDestinazione.append("<br>");
            }
        }
        if ( StringUtils.isNotBlank(this.provinciaDestinazione) )
        {
            luogoDestinazione.append("(").append(this.provinciaDestinazione).append(")").append("<br>");
        }
        if ( StringUtils.isNotBlank(this.nazioneDestinazione) )
        {
            luogoDestinazione.append(this.nazioneDestinazione);
        }

        return luogoDestinazione.append("").toString();
    }

}

