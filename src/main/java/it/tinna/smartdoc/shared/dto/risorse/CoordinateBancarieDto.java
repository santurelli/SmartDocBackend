package it.tinna.smartdoc.shared.dto.risorse;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class CoordinateBancarieDto extends BaseDto {

	private String abi;
	private String bic;
	private String cab;
	private String cin;
	private String conto;
	private String descBanca;
	private String iban;

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

	public String getConto() {
		return conto;
	}

	public String getDescBanca() {
		return descBanca;
	}

	public String getIban() {
		return iban;
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

	public void setConto(String conto) {
		this.conto = conto;
	}

	public void setDescBanca(String descBanca) {
		this.descBanca = descBanca;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

}

