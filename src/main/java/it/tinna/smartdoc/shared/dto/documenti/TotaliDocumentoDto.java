package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class TotaliDocumentoDto extends BaseDto {
	
	private Double totaleDocumento;
	private Double totaleImponibile;
	private Double totaleIva;
	private Double totaleColli;
	private Double totalePallet;
	private Double totalePesoLordo;
	private Double totalePesoNetto;

	public Double getTotaleColli() {
		return totaleColli;
	}

	public Double getTotaleDocumento() {
		return totaleDocumento;
	}

	public Double getTotaleImponibile() {
		return totaleImponibile;
	}

	public Double getTotaleIva() {
		return totaleIva;
	}

	public Double getTotalePallet() {
		return totalePallet;
	}

	public Double getTotalePesoLordo() {
		return totalePesoLordo;
	}

	public Double getTotalePesoNetto() {
		return totalePesoNetto;
	}

	public void setTotaleColli(Double totaleColli) {
		this.totaleColli = totaleColli;
	}

	public void setTotaleDocumento(Double totaleDocumento) {
		this.totaleDocumento = totaleDocumento;
	}

	public void setTotaleImponibile(Double totaleImponibile) {
		this.totaleImponibile = totaleImponibile;
	}

	public void setTotaleIva(Double totaleIva) {
		this.totaleIva = totaleIva;
	}

	public void setTotalePallet(Double totalePallet) {
		this.totalePallet = totalePallet;
	}

	public void setTotalePesoLordo(Double totalePesoLordo) {
		this.totalePesoLordo = totalePesoLordo;
	}

	public void setTotalePesoNetto(Double totalePesoNetto) {
		this.totalePesoNetto = totalePesoNetto;
	}

	
	
}

