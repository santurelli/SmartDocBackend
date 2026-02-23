package it.tinna.smartdoc.shared.dto.statistiche;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class StatisticaDto extends BaseDto {
	
	@Expose
	private String descrizione;
	@Expose
	private Double valore;

	public String getDescrizione() {
		return descrizione;
	}

	public Double getValore() {
		return valore;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setValore(Double valore) {
		this.valore = valore;
	}
}

