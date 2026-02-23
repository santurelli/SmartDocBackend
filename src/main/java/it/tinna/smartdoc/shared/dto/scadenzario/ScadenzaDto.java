package it.tinna.smartdoc.shared.dto.scadenzario;

import java.math.BigDecimal;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class ScadenzaDto extends BaseDto {
	
	private String dataEsecuzione;
	private String dataScadenza;
	private String descrizione;
	private Integer giorni;
	private BigDecimal importo;
	private String note;
	private String soggetto;
	private String tipo;
	private String tipoPagamento;
	private Integer flScaduta;
	
	public String getDataEsecuzione() {
		return dataEsecuzione;
	}

	public String getDataScadenza() {
		return dataScadenza;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Integer getGiorni() {
		return giorni;
	}

	public BigDecimal getImporto() {
		return importo;
	}

	public String getNote() {
		return note;
	}

	public String getSoggetto() {
		return soggetto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setDataEsecuzione(String dataEsecuzione) {
		this.dataEsecuzione = dataEsecuzione;
	}

	public void setDataScadenza(String dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setGiorni(Integer giorni) {
		this.giorni = giorni;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setSoggetto(String soggetto) {
		this.soggetto = soggetto;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public Integer getFlScaduta() {
		return flScaduta;
	}

	public void setFlScaduta(Integer flScaduta) {
		this.flScaduta = flScaduta;
	}

}

