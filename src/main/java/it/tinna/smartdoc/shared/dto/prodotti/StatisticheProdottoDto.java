package it.tinna.smartdoc.shared.dto.prodotti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings({ "serial"})
public class StatisticheProdottoDto extends BaseDto {
	
	private Double prezzoMedioAcquisto;
	private Double ultimoPrezzoAcquisto;
	private Double prezzoMedioVendita;
	private String dataPrimoCarico;
	private String dataUltimoCarico;
	private String dataUltimoScarico;
	private Double quantitaVenditaMensileMedia;

	public Double getPrezzoMedioAcquisto() {
		return prezzoMedioAcquisto;
	}

	public void setPrezzoMedioAcquisto(Double prezzoMedioAcquisto) {
		this.prezzoMedioAcquisto = prezzoMedioAcquisto;
	}

	public String getDataPrimoCarico() {
		return dataPrimoCarico;
	}

	public void setDataPrimoCarico(String dataPrimoCarico) {
		this.dataPrimoCarico = dataPrimoCarico;
	}

	public String getDataUltimoCarico() {
		return dataUltimoCarico;
	}

	public void setDataUltimoCarico(String dataUltimoCarico) {
		this.dataUltimoCarico = dataUltimoCarico;
	}

	public String getDataUltimoScarico() {
		return dataUltimoScarico;
	}

	public void setDataUltimoScarico(String dataUltimoScarico) {
		this.dataUltimoScarico = dataUltimoScarico;
	}

	public Double getUltimoPrezzoAcquisto() {
		return ultimoPrezzoAcquisto;
	}

	public void setUltimoPrezzoAcquisto(Double ultimoPrezzoAcquisto) {
		this.ultimoPrezzoAcquisto = ultimoPrezzoAcquisto;
	}

	public Double getPrezzoMedioVendita() {
		return prezzoMedioVendita;
	}

	public void setPrezzoMedioVendita(Double prezzoMedioVendita) {
		this.prezzoMedioVendita = prezzoMedioVendita;
	}

	public Double getQuantitaVenditaMensileMedia() {
		return quantitaVenditaMensileMedia;
	}

	public void setQuantitaVenditaMensileMedia(Double quantitaVenditaMensileMedia) {
		this.quantitaVenditaMensileMedia = quantitaVenditaMensileMedia;
	}
	
	
	
}

