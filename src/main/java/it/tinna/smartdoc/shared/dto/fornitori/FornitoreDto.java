package it.tinna.smartdoc.shared.dto.fornitori;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.clienti.BaseClienteDto;

@SuppressWarnings("serial")
public class FornitoreDto extends BaseClienteDto {
	
	@Expose
	private Integer idCategoriaSpesa;
	@Expose
	private String ultimoDocAcquisto;
	
	public Integer getIdCategoriaSpesa() {
		return idCategoriaSpesa;
	}

	public String getUltimoDocAcquisto() {
		return ultimoDocAcquisto;
	}

	public void setIdCategoriaSpesa(Integer idCategoriaSpesa) {
		this.idCategoriaSpesa = idCategoriaSpesa;
	}

	public void setUltimoDocAcquisto(String ultimoDocVendita) {
		this.ultimoDocAcquisto = ultimoDocVendita;
	}

}

