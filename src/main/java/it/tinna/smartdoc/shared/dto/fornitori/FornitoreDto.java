package it.tinna.smartdoc.shared.dto.fornitori;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.clienti.BaseClienteDto;

@SuppressWarnings("serial")
public class FornitoreDto extends BaseClienteDto {
	
	@Expose
	private Integer idCategoriaSpesa;
	@Expose
	private String ultimoDocAcquisto;
	@Expose
	private Integer idContoContabile;
	@Expose
	private String descContoContabile;

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

	public Integer getIdContoContabile() {
		return idContoContabile;
	}

	public void setIdContoContabile(Integer idContoContabile) {
		this.idContoContabile = idContoContabile;
	}

	public String getDescContoContabile() {
		return descContoContabile;
	}

	public void setDescContoContabile(String descContoContabile) {
		this.descContoContabile = descContoContabile;
	}

}

