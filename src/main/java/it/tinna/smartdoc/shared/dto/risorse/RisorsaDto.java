package it.tinna.smartdoc.shared.dto.risorse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RisorsaDto extends BaseDto {

	public enum Tipologia {
		ALTRO("Altro", ISharedConstants.RISORSA_ALTRO),
		BANCA("Banca", ISharedConstants.RISORSA_BANCA),
		CARTA_CREDITO("Carta di credito", ISharedConstants.RISORSA_CARTACREDITO),
		CASSA("Cassa", ISharedConstants.RISORSA_CASSA),
		TITOLI("Titoli", ISharedConstants.RISORSA_TITOLI);

		private String descrizione;
		private String valore;

		Tipologia(String descrizione, String valore) {
			this.descrizione = descrizione;
			this.valore = valore;
		}

		public String getDescrizione() {
			return descrizione;
		}

		public String getValore() {
			return this.valore;
		}
	}

	private String abi;
	private String cab;
	private String bic;
	private String cin;
	@Expose
	private String codSia;
	private String conto;
	private String descBanca;
	@Expose
	@SerializedName("descrizione")
	private String descrizione;
	@Expose
	private String descTipologia;
	@Expose
	private String iban;
	@Expose
	private String note;
	@Expose
	private Integer predefinita;
	@Expose
	private Double saldoIniziale;
	@Expose
	private String tipologia;

	public String getAbi() {
		return abi;
	}

	public String getBic() {
		return bic;
	}

	public String getCab() {
		return cab;
	}

	public String getCin() {
		return cin;
	}

	public String getCodSia() {
		return codSia;
	}

	public String getConto() {
		return conto;
	}

	public String getDescBanca() {
		return descBanca;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getDescTipologia() {
		return descTipologia;
	}

	public String getIban() {
		return iban;
	}

	public String getNote() {
		return note;
	}

	public Integer getPredefinita() {
		return predefinita;
	}

	public Double getSaldoIniziale() {
		return saldoIniziale;
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setAbi(String abi) {
		this.abi = abi;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public void setCab(String cab) {
		this.cab = cab;
	}

	public void setCin(String cin) {
		this.cin = cin;
	}

	public void setCodSia(String codSia) {
		this.codSia = codSia;
	}

	public void setConto(String conto) {
		this.conto = conto;
	}

	public void setDescBanca(String descBanca) {
		this.descBanca = descBanca;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setDescTipologia(String descCategoria) {
		this.descTipologia = descCategoria;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setPredefinita(Integer predefinita) {
		this.predefinita = predefinita;
	}

	public void setSaldoIniziale(Double saldoIniziale) {
		this.saldoIniziale = saldoIniziale;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
		for (int i = 0; i < Tipologia.values().length; i++) {
			if (Tipologia.values()[i].getValore().equals(tipologia)) {
				setDescTipologia(Tipologia.values()[i].getDescrizione());
				break;
			}
		}
	}

}

