package it.tinna.smartdoc.shared.dto.categorie;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class CategoriaDto extends BaseDto {

	@Expose
	private String descrizione;

	@Expose
	private Integer idContoRicavo;

	@Expose
	private Integer idContoCosto;

	@Expose
	private String descContoRicavo;

	@Expose
	private String descContoCosto;

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Integer getIdContoRicavo() {
		return idContoRicavo;
	}

	public void setIdContoRicavo(Integer idContoRicavo) {
		this.idContoRicavo = idContoRicavo;
	}

	public Integer getIdContoCosto() {
		return idContoCosto;
	}

	public void setIdContoCosto(Integer idContoCosto) {
		this.idContoCosto = idContoCosto;
	}

	public String getDescContoRicavo() {
		return descContoRicavo;
	}

	public void setDescContoRicavo(String descContoRicavo) {
		this.descContoRicavo = descContoRicavo;
	}

	public String getDescContoCosto() {
		return descContoCosto;
	}

	public void setDescContoCosto(String descContoCosto) {
		this.descContoCosto = descContoCosto;
	}

}
