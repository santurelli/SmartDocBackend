package it.tinna.smartdoc.shared.dto.documenti;

import java.util.List;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class ResultFatturaElettronicaDto extends BaseDto {
	
	private List<String> descError;
	private String descDocumento;
	private String filePath;

	public String getDescDocumento() {
		return descDocumento;
	}

	public List<String> getDescError() {
		return descError;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setDescDocumento(String descDocumento) {
		this.descDocumento = descDocumento;
	}

	public void setDescError(List<String> descError) {
		this.descError = descError;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}

