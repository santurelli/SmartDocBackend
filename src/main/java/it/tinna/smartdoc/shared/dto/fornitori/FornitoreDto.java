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
	@Expose
	private Integer flRitenutaAcconto;
	@Expose
	private String tipoRitenuta;
	@Expose
	private java.math.BigDecimal percRitenutaAcconto;

	public Integer getFlRitenutaAcconto() {
		return flRitenutaAcconto;
	}

	public void setFlRitenutaAcconto(Integer flRitenutaAcconto) {
		this.flRitenutaAcconto = flRitenutaAcconto;
	}

	public String getTipoRitenuta() {
		return tipoRitenuta;
	}

	public void setTipoRitenuta(String tipoRitenuta) {
		this.tipoRitenuta = tipoRitenuta;
	}

	public java.math.BigDecimal getPercRitenutaAcconto() {
		return percRitenutaAcconto;
	}

	public void setPercRitenutaAcconto(java.math.BigDecimal percRitenutaAcconto) {
		this.percRitenutaAcconto = percRitenutaAcconto;
	}

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

