package it.tinna.smartdoc.shared.dto.risorse;

import it.tinna.smartdoc.shared.dto.BaseDto;

public class RisorsaDto extends BaseDto {
    private String tipologia;
    private String descrizione;
    private Double saldoIniziale;
    private String codSia;
    private String descBanca;
    private String iban;
    private String cin;
    private String abi;
    private String cab;
    private String conto;
    private String bic;
    private String note;
    private Integer predefinita;


    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Double getSaldoIniziale() {
        return saldoIniziale;
    }

    public void setSaldoIniziale(Double saldoIniziale) {
        this.saldoIniziale = saldoIniziale;
    }

    public String getCodSia() {
        return codSia;
    }

    public void setCodSia(String codSia) {
        this.codSia = codSia;
    }

    public String getDescBanca() {
        return descBanca;
    }

    public void setDescBanca(String descBanca) {
        this.descBanca = descBanca;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getAbi() {
        return abi;
    }

    public void setAbi(String abi) {
        this.abi = abi;
    }

    public String getCab() {
        return cab;
    }

    public void setCab(String cab) {
        this.cab = cab;
    }

    public String getConto() {
        return conto;
    }

    public void setConto(String conto) {
        this.conto = conto;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getPredefinita() {
        return predefinita;
    }

    public void setPredefinita(Integer predefinita) {
        this.predefinita = predefinita;
    }


}
