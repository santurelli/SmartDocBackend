package it.tinna.smartdoc.shared.dto.pianoconti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class PianoContoDto extends BaseDto {

    @Expose
    private String codice;

    @Expose
    private String descrizione;

    @Expose
    private Long idPadre;

    @Expose
    private String tipo;

    @Expose
    private String ruoloDefault;

    @Expose
    private int predefinito;

    @Expose
    private int bloccato;

    @Expose
    private String note;

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Long getIdPadre() {
        return idPadre;
    }

    public void setIdPadre(Long idPadre) {
        this.idPadre = idPadre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRuoloDefault() {
        return ruoloDefault;
    }

    public void setRuoloDefault(String ruoloDefault) {
        this.ruoloDefault = ruoloDefault;
    }

    public int getPredefinito() {
        return predefinito;
    }

    public void setPredefinito(int predefinito) {
        this.predefinito = predefinito;
    }

    public int getBloccato() {
        return bloccato;
    }

    public void setBloccato(int bloccato) {
        this.bloccato = bloccato;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
