package it.tinna.smartdoc.shared.dto.sottocategorie;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.categorie.CategoriaDto;

@SuppressWarnings("serial")
public class SottoCategoriaDto extends CategoriaDto {
	
	@Expose
	private String parentDescription;
	@Expose
	private Integer parentId;

	public String getParentDescription() {
		return parentDescription;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentDescription(String parentDescription) {
		this.parentDescription = parentDescription;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

}

