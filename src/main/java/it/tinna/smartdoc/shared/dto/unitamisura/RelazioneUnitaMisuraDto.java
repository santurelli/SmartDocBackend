package it.tinna.smartdoc.shared.dto.unitamisura;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RelazioneUnitaMisuraDto extends BaseDto {
	
	private String descUnitaMisura1;
	private String descUnitaMisura2;
	private Integer idUunitaMisura1;
	private Integer idUnitaMisura2;
	private Double relazione;

	public String getDescUnitaMisura1() {
		return descUnitaMisura1;
	}

	public String getDescUnitaMisura2() {
		return descUnitaMisura2;
	}

	public Integer getIdUunitaMisura1() {
		return idUunitaMisura1;
	}

	public Integer getIdUnitaMisura2() {
		return idUnitaMisura2;
	}

	public Double getRelazione() {
		return relazione;
	}

	public void setDescUnitaMisura1(String descUnitaMisura1) {
		this.descUnitaMisura1 = descUnitaMisura1;
	}

	public void setDescUnitaMisura2(String descUnitaMisura2) {
		this.descUnitaMisura2 = descUnitaMisura2;
	}

	public void setIdUunitaMisura1(Integer idUunitaMisura1) {
		this.idUunitaMisura1 = idUunitaMisura1;
	}

	public void setIdUnitaMisura2(Integer idUnitaMisura2) {
		this.idUnitaMisura2 = idUnitaMisura2;
	}

	public void setRelazione(Double relazione) {
		this.relazione = relazione;
	}

}

