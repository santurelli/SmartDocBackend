package it.tinna.smartdoc.server.constants;

public enum StatoPagamento {

	SALDATO("Saldato"), NON_SALDATO("Da saldare");

	private String descrizione;

	StatoPagamento(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}

}

