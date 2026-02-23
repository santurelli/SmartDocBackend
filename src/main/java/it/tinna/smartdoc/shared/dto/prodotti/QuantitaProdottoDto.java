package it.tinna.smartdoc.shared.dto.prodotti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class QuantitaProdottoDto extends BaseDto {
	
	private Integer idProdotto;
	
	private Integer idTaglia;
	private Integer idColore;
	private Integer idTono;
	private Integer idScelta;
	
	private String descrTaglia;
	private String descrColore;
	private String descrTono;
	private String descrScelta;
	
	private Double esistente;
	private Double impegnata;
	private Double disponibile;
	private Double arrivo;
	
	public Integer getIdProdotto() {
		return idProdotto;
	}
	public void setIdProdotto(Integer idProdotto) {
		this.idProdotto = idProdotto;
	}
	public Integer getIdTaglia() {
		return idTaglia;
	}
	public void setIdTaglia(Integer idTaglia) {
		this.idTaglia = idTaglia;
	}
	public Integer getIdColore() {
		return idColore;
	}
	public void setIdColore(Integer idColore) {
		this.idColore = idColore;
	}
	public Integer getIdTono() {
		return idTono;
	}
	public void setIdTono(Integer idTono) {
		this.idTono = idTono;
	}
	public Integer getIdScelta() {
		return idScelta;
	}
	public void setIdScelta(Integer idScelta) {
		this.idScelta = idScelta;
	}
	public String getDescrTaglia() {
		return descrTaglia;
	}
	public void setDescrTaglia(String descrTaglia) {
		this.descrTaglia = descrTaglia;
	}
	public String getDescrColore() {
		return descrColore;
	}
	public void setDescrColore(String descrColore) {
		this.descrColore = descrColore;
	}
	public String getDescrTono() {
		return descrTono;
	}
	public void setDescrTono(String descrTono) {
		this.descrTono = descrTono;
	}
	public String getDescrScelta() {
		return descrScelta;
	}
	public void setDescrScelta(String descrScelta) {
		this.descrScelta = descrScelta;
	}
	public Double getEsistente() {
		return esistente;
	}
	public void setEsistente(Double esistente) {
		this.esistente = esistente;
	}
	public Double getImpegnata() {
		return impegnata;
	}
	public void setImpegnata(Double impegnata) {
		this.impegnata = impegnata;
	}
	public Double getDisponibile() {
		return disponibile;
	}
	public void setDisponibile(Double disponibile) {
		this.disponibile = disponibile;
	}
	public Double getArrivo() {
		return arrivo;
	}
	public void setArrivo(Double arrivo) {
		this.arrivo = arrivo;
	}
	

	

}

