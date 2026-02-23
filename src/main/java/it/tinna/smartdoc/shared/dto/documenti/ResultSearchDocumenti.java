package it.tinna.smartdoc.shared.dto.documenti;

import java.util.List;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class ResultSearchDocumenti extends BaseDto {
	
	private List<MovimentiDocumentoDto> documenti;
	private boolean trimmed;

	public List<MovimentiDocumentoDto> getDocumenti() {
		return documenti;
	}

	public boolean isTrimmed() {
		return trimmed;
	}

	public void setDocumenti(List<MovimentiDocumentoDto> documenti) {
		this.documenti = documenti;
	}

	public void setTrimmed(boolean trimmed) {
		this.trimmed = trimmed;
	}

}

