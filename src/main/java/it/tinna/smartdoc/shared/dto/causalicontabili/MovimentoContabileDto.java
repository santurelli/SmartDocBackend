package it.tinna.smartdoc.shared.dto.causalicontabili;

import it.tinna.smartdoc.shared.dto.BaseDto;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class MovimentoContabileDto extends BaseDto {
	
	private String codiceConto;
	private String dataMovimento;
	private String descrizione;
	private String descrizioneConto;
	private Integer idCausaleContabile;
	private Integer idConto;
	private Integer idDocumento;
	private Integer idParametrizzazione;
	private BigDecimal importoAvere;
	private BigDecimal importoDare;
	private String importoAvereFormattato;
	private String importoDareFormattato;
	private Integer nProgr;
	private String riferimentoDocumento; //nel caso di pagamento memorizza il tipo di documento a cui è associato il pagamento
	private String tipoDocumento;
	private Integer riferimentoScadenza;
	private String dataRegistrazione;
	private Integer idGruppo;
	private Integer flManuale;

	public String getCodiceConto() {
		return codiceConto;
	}

	public String getDataMovimento() {
		return dataMovimento;
	}
	
	public String getDescrizione() {
		return descrizione;
	}

	public String getDescrizioneConto() {
		return descrizioneConto;
	}

	public Integer getIdCausaleContabile() {
		return idCausaleContabile;
	}

	public Integer getIdConto() {
		return idConto;
	}

	public Integer getIdDocumento() {
		return idDocumento;
	}

	public Integer getIdParametrizzazione() {
		return idParametrizzazione;
	}

	public BigDecimal getImportoAvere() {
		return importoAvere;
	}

	public String getImportoAvereFormattato() {
		return importoAvereFormattato;
	}

	public BigDecimal getImportoDare() {
		return importoDare;
	}

	public String getImportoDareFormattato() {
		return importoDareFormattato;
	}

	public Integer getnProgr() {
		return nProgr;
	}

	public String getRiferimentoDocumento() {
		return riferimentoDocumento;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setCodiceConto(String codiceConto) {
		this.codiceConto = codiceConto;
	}

	public void setDataMovimento(String dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setDescrizioneConto(String descrizioneConto) {
		this.descrizioneConto = descrizioneConto;
	}

	public void setIdCausaleContabile(Integer idCausaleContabile) {
		this.idCausaleContabile = idCausaleContabile;
	}

	public void setIdConto(Integer idConto) {
		this.idConto = idConto;
	}

	public void setIdDocumento(Integer idDocumento) {
		this.idDocumento = idDocumento;
	}

	public void setIdParametrizzazione(Integer idParametrizzazione) {
		this.idParametrizzazione = idParametrizzazione;
	}

	public void setImportoAvere(BigDecimal importoAvere) {
		this.importoAvere = importoAvere;
	}

	public void setImportoAvereFormattato(String importoAvereFormattato) {
		this.importoAvereFormattato = importoAvereFormattato;
	}

	public void setImportoDare(BigDecimal importoDare) {
		this.importoDare = importoDare;
	}

	public void setImportoDareFormattato(String importoDareFormattato) {
		this.importoDareFormattato = importoDareFormattato;
	}

	public void setnProgr(Integer nProgr) {
		this.nProgr = nProgr;
	}

	public void setRiferimentoDocumento(String riferimentoDocumento) {
		this.riferimentoDocumento = riferimentoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Integer getRiferimentoScadenza() {
		return riferimentoScadenza;
	}

	public void setRiferimentoScadenza(Integer riferimentoScadenza) {
		this.riferimentoScadenza = riferimentoScadenza;
	}

	public String getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(String dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public Integer getIdGruppo() {
		return idGruppo;
	}

	public void setIdGruppo(Integer idGruppo) {
		this.idGruppo = idGruppo;
	}

	public Integer getFlManuale() {
		return flManuale;
	}

	public void setFlManuale(Integer flManuale) {
		this.flManuale = flManuale;
	}
	
}

