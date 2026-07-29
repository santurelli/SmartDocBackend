package it.tinna.smartdoc.shared.dto.scadenzario;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class ScadenzarioRegolaDto extends BaseDto {

    private String tipo; // INCASSO | PAGAMENTO
    private Integer giorniOffset; // negativo = giorni prima della scadenza, positivo = giorni di ritardo
    private String oggetto;
    private String corpo;
    private Integer attivo;
    private Integer ordine;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getGiorniOffset() { return giorniOffset; }
    public void setGiorniOffset(Integer giorniOffset) { this.giorniOffset = giorniOffset; }

    public String getOggetto() { return oggetto; }
    public void setOggetto(String oggetto) { this.oggetto = oggetto; }

    public String getCorpo() { return corpo; }
    public void setCorpo(String corpo) { this.corpo = corpo; }

    public Integer getAttivo() { return attivo; }
    public void setAttivo(Integer attivo) { this.attivo = attivo; }

    public Integer getOrdine() { return ordine; }
    public void setOrdine(Integer ordine) { this.ordine = ordine; }
}
