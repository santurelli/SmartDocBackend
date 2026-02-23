package it.tinna.smartdoc.shared.dto.indirizzi;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

// import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class IndirizzoDto extends BaseDto {

    public enum Richiedente {
        CLIENTI("clienti"),
        FORNITORI("fornitori");

        private String richiedente;

        Richiedente(String richiedente) {
            this.richiedente = richiedente;
        }

        public String getValore() {
            return this.richiedente;
        }
    }

    public enum TipologiaIndirizzo {
        ALTRO("Altro", "T"),
        DESTINAZIONE_MERCE("Destinazione merce", "M"),
        SEDE_AMMINISTRATIVA("Sede amministrativa", "A"),
        SEDE_LEGALE("Sede legale", "L"),
        SEDE_OPERATIVA("Sede operativa", "O");

        private static Map<String, TipologiaIndirizzo> valueToDescrMap;

        public static TipologiaIndirizzo getDescrizioneByValue(String value) {
            if (valueToDescrMap == null) {
                initMapping();
            }
            return valueToDescrMap.get(value);
        }

        private static void initMapping() {
            valueToDescrMap = new HashMap<>();
            for (TipologiaIndirizzo s : values()) {
                valueToDescrMap.put(s.valore, s);
            }
        }

        // @Expose
        public String descrizione;

        public String valore;

        TipologiaIndirizzo(String descrizione,
                String valore) {
            this.descrizione = descrizione;
            this.valore = valore;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public String getValore() {
            return this.valore;
        }

    }

    // @Expose
    private String cap;

    // @Expose
    private String citta;

    // @Expose
    private String codiceUfficio;

    // @Expose
    private String descrizione;

    // @Expose
    private String descrTipologia;

    private long idRichiedente;

    // @Expose
    private String idTmp;

    // private Integer idCliente;
    // @Expose
    private String indirizzo;

    // @Expose
    private String nazione;

    // @Expose
    private String provincia;

    // @Expose
    private String tipologia;

    public void setDescrizione(String descrizione) {
        /*
         * Se this.descrizione è non vuoto vuol dire che è stato settato in
         * corrisponsenza del metodo setTipologia con la descrizione di uno dei tipi
         * "standard" (sede operativa, destinazione merce, etc...)
         */
        if (StringUtils.isEmpty(this.descrizione)) {
            this.descrizione = descrizione;
        }
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
        if (tipologia.equals(TipologiaIndirizzo.DESTINAZIONE_MERCE.getValore())) {
            setDescrizione(TipologiaIndirizzo.DESTINAZIONE_MERCE.getDescrizione());
        } else if (tipologia.equals(TipologiaIndirizzo.SEDE_AMMINISTRATIVA.getValore())) {
            setDescrizione(TipologiaIndirizzo.SEDE_AMMINISTRATIVA.getDescrizione());
        } else if (tipologia.equals(TipologiaIndirizzo.SEDE_LEGALE.getValore())) {
            setDescrizione(TipologiaIndirizzo.SEDE_LEGALE.getDescrizione());
        } else if (tipologia.equals(TipologiaIndirizzo.SEDE_OPERATIVA.getValore())) {
            setDescrizione(TipologiaIndirizzo.SEDE_OPERATIVA.getDescrizione());
        }
    }

}

