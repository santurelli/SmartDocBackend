package it.tinna.smartdoc.shared.dto.contatti;

import java.util.HashMap;
import java.util.Map;

// import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, of = {})
public class ContattoDto extends BaseDto {

    public enum Richiedente {
        FORNITORI("fornitori"),
        CLIENTI("clienti");

        private String richiedente;

        Richiedente(String richiedente) {
            this.richiedente = richiedente;
        }

        public String getValore() {
            return this.richiedente;
        }
    }

    public enum TipologiaContatto {
        SEDE_OPERATIVA("O"),
        SEDE_AMMINISTRATIVA("A"),
        SEDE_LEGALE("L"),
        SCARICO_MERCI("M"),
        ALTRO("T");

        private static Map<String, TipologiaContatto> valueToDescrMap;

        public static TipologiaContatto getDescrizioneByValue(String value) {
            if (valueToDescrMap == null) {
                initMapping();
            }
            return valueToDescrMap.get(value);
        }

        private static void initMapping() {
            valueToDescrMap = new HashMap<>();
            for (TipologiaContatto s : values()) {
                valueToDescrMap.put(s.tipologiaContatto, s);
            }
        }

        private String tipologiaContatto;

        TipologiaContatto(String tipologiaContatto) {
            this.tipologiaContatto = tipologiaContatto;
        }

        public String getValore() {
            return this.tipologiaContatto;
        }
    }

    // @Expose
    private String tipologia;

    // @Expose
    private String descrizione;

    // @Expose
    private String telefono;

    // @Expose
    private String cellulare;

    // @Expose
    private String fax;

    // @Expose
    private String email;

    // @Expose
    private String idTmp;

    // @Expose
    private String pec;

    // @Expose
    private Integer flUsaPerSolleciti;

    private long idRichiedente;

    public void setReferente(String referente) {
        this.descrizione = referente;
    }

    public String getReferente() {
        return this.descrizione;
    }

}

