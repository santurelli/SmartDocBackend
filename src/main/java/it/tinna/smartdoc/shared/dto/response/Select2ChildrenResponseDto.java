package it.tinna.smartdoc.shared.dto.response;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class Select2ChildrenResponseDto extends BaseDto {

	@Expose
	private String codiceFiscale;
	@Expose
	private String partitaIva;
	@Expose
	private String type;

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public String getType() {
		return type;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public void setType(String type) {
		this.type = type;
	}

}

