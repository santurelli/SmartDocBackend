package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class TipoCassaDto extends BaseDto{
	
	private String descrizione;

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}

