package it.tinna.smartdoc.shared.dto.documenti;

public enum TipoRigaDocumento {
	
	ARTICOLO("A"),
	NOTA("N"),
	FUORI_MAGAZZINO("F");
	
	private String value;
	
	private TipoRigaDocumento(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

}

