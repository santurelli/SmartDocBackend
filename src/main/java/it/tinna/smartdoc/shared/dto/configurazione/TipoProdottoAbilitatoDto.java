package it.tinna.smartdoc.shared.dto.configurazione;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class TipoProdottoAbilitatoDto extends BaseDto {
	
	private Integer abilitato;
	private String chiave;
	private String descrizione;
	private Integer predefinito;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TipoProdottoAbilitatoDto) {
			return this.getDescrizione().equals(((TipoProdottoAbilitatoDto)obj).getDescrizione());
		}
		return false;
	}

	public Integer getAbilitato() {
		return abilitato;
	}

	public String getChiave() {
		return chiave;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public Integer getPredefinito() {
		return predefinito;
	}

	public void setAbilitato(Integer abilitato) {
		this.abilitato = abilitato;
	}

	public void setChiave(String chiave) {
		this.chiave = chiave;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setPredefinito(Integer predefinito) {
		this.predefinito = predefinito;
	}

}

