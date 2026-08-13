package it.tinna.smartdoc.shared.dto.contabilita;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class EsercizioDto extends BaseDto {

    @Expose
    private int    anno;

    @Expose
    private String stato;

    @Expose
    private String dtChiusura;

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getDtChiusura() {
        return dtChiusura;
    }

    public void setDtChiusura(String dtChiusura) {
        this.dtChiusura = dtChiusura;
    }
}
