package it.tinna.smartdoc.shared.dto.registratoricassa;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RegistratoreCassaDto extends BaseDto {
	
	private Integer baudRate;
	private Integer bitDati;
	private String descrizione;
	private Integer parita;
	private Integer stopBit;

	public Integer getBaudRate() {
		return baudRate;
	}

	public Integer getBitDati() {
		return bitDati;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Integer getParita() {
		return parita;
	}

	public Integer getStopBit() {
		return stopBit;
	}

	public void setBaudRate(Integer baudRate) {
		this.baudRate = baudRate;
	}

	public void setBitDati(Integer bitDati) {
		this.bitDati = bitDati;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setParita(Integer partita) {
		this.parita = partita;
	}

	public void setStopBit(Integer stopBit) {
		this.stopBit = stopBit;
	}

}

