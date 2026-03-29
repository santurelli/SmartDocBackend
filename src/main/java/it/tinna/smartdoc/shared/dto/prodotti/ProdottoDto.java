package it.tinna.smartdoc.shared.dto.prodotti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.codicibarre.CodiceBarreDto;
import it.tinna.smartdoc.shared.dto.sceltecolori.SceltaColoreDto;
import it.tinna.smartdoc.shared.dto.tonitaglie.TonoTagliaDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings(
{ "serial",
  "rawtypes" })
@Getter
@Setter
@NoArgsConstructor
public class ProdottoDto extends BaseDto
{

    public enum TipoProdotto
    {
     ARTICOLO("A", "Articolo"),
     ARTICOLO_MAGAZZINO("AM", "Articolo con magazzino"),
     ARTICOLO_SCELTE_COLORI("AMSC", "Articolo con scelte/colori"),
     SERVIZIO("S", "Servizio");

        private static Map<String, TipoProdotto> valueToDescrMap;

        public static TipoProdotto getDescrizioneByValue(String value)
        {
            if ( valueToDescrMap == null )
            {
                initMapping();
            }
            return valueToDescrMap.get(value);
        }

        private static void initMapping()
        {
            valueToDescrMap = new HashMap<String, TipoProdotto>();
            for ( TipoProdotto s : values() )
            {
                valueToDescrMap.put(s.valore, s);
            }
        }

        private String descrizione;

        private String valore;

        TipoProdotto(String valore,
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

    private Double                  altezzaLorda;

    private Double                  altezzaNetta;

    private Integer                 assemblato;

    @Expose
    private String                  codice;

    @Expose
    private String                  codicePerFornitore;

    private List<CodiceBarreDto>    codiciBarre;

    private Integer                 colli;

    @Expose
    private String                  dataPrimoCarico;

    @Expose
    private String                  dataUltimoCarico;

    @Expose
    private String                  dataUltimoScarico;

    private String                  descAliquotaIva;
    
    @Expose
    private Double                  prezzoFornitore;

    @Expose
    private String                  descCategoria;

    @Expose
    private String                  descrizione;

    @SuppressWarnings("unused")
    private String                  descrizioneDocumento;           // usato per stampare la descrizione articolo nei documenti (tiene conto del fatto che può essere uno store "ceramica" oppure no)

    @Expose
    private String                  descFornitore;

    @Expose
    private String                  descSottoCategoria;

    @Expose
    @SerializedName("descUnitaMisura")
    private String                  descUnitaMisura1;

    private String                  descUnitaMisura2;

    private String                  descUnitaMisuraVendita;

    private Double                  equivUnitaMisura;

    private Double                  equivUnitaMisuraVendita;

    private Integer                 esclusoDaProvvigione;

    private Integer                 gestMagazzino;

    @Expose
    private Integer                 idAliquotaIva;

    @Expose
    private long                    idCalibro;

    @Expose
    private Integer                 idCategoria;

    @Expose
    private long                    idConto;

    @Expose
    private long                    idDivisione;

    @Expose
    private long                    idFormato;

    @Expose
    private Integer                 idFornitore;

    private Integer                 idGruppoTaglia;                 // usato in fase di salvataggio

    private Integer                 idGruppoTono;                   // usato in fase di salvataggio

    @Expose
    private long                    idScelta;
    // private Integer idGruppoTonoTaglia; //usato quando si recuperano i prodotti dal db

    @Expose
    private Integer                 idSottoCategoria;

    private Integer                 idSottocontoArticolo;

    @Expose
    private long                    idTono;

    @Expose
    private Integer                 idUnitaMisura1;

    private Integer                 idUnitaMisura2;

    private Integer                 idUnitaMisuraDimensioni;

    private Integer                 idUnitaMisuraPeso;

    private Integer                 idUnitaMisuraVendita;

    private Double                  impostaAliquotaIva;

    private Double                  larghezzaLorda;

    private Double                  larghezzaNetta;

    @Expose
    private double                  mqBox;

    @Expose
    private String                  note;

    private int                     obsoleto;

    private Integer                 pallet;

    private Double                  pesoLordo;

    private Double                  pesoNetto;

    @Expose
    private int                     pezziBox;

    @Expose
    private List<PrezzoProdottoDto> prezzi;

    private Double                  prezzo;

    private Double                  prezzoIvato;

    private Double                  prezzoListinoRiferimentoVendita;

    private Double                  prezzoListinoVendita;

    private Double                  prezzoMedioAcquisto;

    private Double                  prezzoMedioVendita;

    private Double                  profonditaLorda;

    private Double                  profonditaNetta;

    @Expose
    private Double                  quantitaEsistente;

    @Expose
    private Double                  quantitaImpegnata;

    private Double                  quantitaInDistinta;

    private List<SceltaColoreDto>   scelteColori;

    @Expose
    private Double                  scortaMinima;

    @Expose
    private String                  tipologia;

    private List<TonoTagliaDto>     tonitaglie;

    private Double                  ultimoPrezzoAcquisto;

    // info utilizzate per la distinta base
    private Integer                 idTaglia;

    private Integer                 idColore;

    @Expose
    private String                  descrFormato;

    @Expose
    private String                  descrCalibro;

    @Expose
    private String                  descrScelta;

    private String                  descrTaglia;

    @Expose
    private String                  descrTono;

    // info per prodotto di gommista
    private Integer                 sezione;

    private Integer                 serie;

    private Integer                 diametro;

    private String                  scultura;

    private String                  indiceVelocita;

    private String                  dot;

    public String getDescFornitore() {
        return descFornitore;
    }

    public void setDescFornitore(String descFornitore) {
        this.descFornitore = descFornitore;
    }



    public void setDescrizioneDocumento(String tipoStore)
    {
        StringBuilder strB = new StringBuilder(getDescrizione());
        if ( "CERAMICA".equals(tipoStore) )
        {
            if ( StringUtils.isNotBlank(getDescrFormato()) || StringUtils.isNotBlank(getDescrScelta()) || StringUtils.isNotBlank(getDescrTono()) || StringUtils.isNotBlank(getDescrCalibro()) )
            {
                strB.append("\n");
                if ( StringUtils.isNotBlank(getDescrFormato()) )
                {
                    strB.append(" Formato: ").append(getDescrFormato());
                }
                if ( StringUtils.isNotBlank(getDescrScelta()) )
                {
                    strB.append(" Scelta: ").append(getDescrScelta());
                }
                if ( StringUtils.isNotBlank(getDescrTono()) )
                {
                    strB.append(" Tono: ").append(getDescrTono());
                }
                if ( StringUtils.isNotBlank(getDescrCalibro()) )
                {
                    strB.append(" Calibro: ").append(getDescrCalibro());
                }
            }
        }
        this.descrizioneDocumento = strB.toString();
    }

}

