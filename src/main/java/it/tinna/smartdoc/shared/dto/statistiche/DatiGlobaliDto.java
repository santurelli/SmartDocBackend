package it.tinna.smartdoc.shared.dto.statistiche;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class DatiGlobaliDto extends BaseDto {
	
	/**
	 * Numero totale dei clienti
	 */
	@Expose
	private Long totClienti;
	/**
	 * Numero totale dei fornitori
	 */
	@Expose
	private Long totFornitori;
	@Expose
	private Long numFattureMese;
	@Expose
	@SerializedName("pagamenti")
	@JsonProperty("pagamenti")
	private List<StatisticaDto> pagamentiRicevutiPerMese;
	/**
	 * Importo totale delle fatture fornitore ricevute nel mese corrente
	 */
	@Expose
	private BigDecimal totFattureFornitore;
	/**
	 * Importo totale delle note credito emesse nel mese corrente
	 */
	@Expose
	private BigDecimal totNoteCredito;
	@Expose
	private BigDecimal totDaPagare;
	@Expose
	private BigDecimal totDaRicevere;
	@Expose
	@SerializedName("venduto")
	@JsonProperty("venduto")
	private List<StatisticaDto> vendutoPerMese;
	
	public Long getNumFattureMese() {
		return numFattureMese;
	}
	
	public List<StatisticaDto> getPagamentiRicevutiPerMese() {
		return pagamentiRicevutiPerMese;
	}

	public Long getTotClienti() {
		return totClienti;
	}

	public BigDecimal getTotDaPagare() {
		return totDaPagare;
	}

	public BigDecimal getTotDaRicevere() {
		return totDaRicevere;
	}

	public BigDecimal getTotFattureFornitore() {
		return totFattureFornitore;
	}

	public Long getTotFornitori() {
		return totFornitori;
	}

	public BigDecimal getTotNoteCredito() {
		return totNoteCredito;
	}

	public List<StatisticaDto> getVendutoPerMese() {
		return vendutoPerMese;
	}

	public void setNumFattureMese(Long numFattureMese) {
		this.numFattureMese = numFattureMese;
	}

	public void setPagamentiRicevutiPerMese(
			List<StatisticaDto> pagamentiRicevutiPerMese) {
		this.pagamentiRicevutiPerMese = pagamentiRicevutiPerMese;
	}

	public void setTotClienti(Long totClienti) {
		this.totClienti = totClienti;
	}

	public void setTotDaPagare(BigDecimal totDaPagare) {
		this.totDaPagare = totDaPagare;
	}

	public void setTotDaRicevere(BigDecimal totDaRicevere) {
		this.totDaRicevere = totDaRicevere;
	}

	public void setTotFattureFornitore(BigDecimal totFattureFornitore) {
		this.totFattureFornitore = totFattureFornitore;
	}

	public void setTotFornitori(Long totFornitori) {
		this.totFornitori = totFornitori;
	}

	public void setTotNoteCredito(BigDecimal totNoteCredito) {
		this.totNoteCredito = totNoteCredito;
	}

	public void setVendutoPerMese(List<StatisticaDto> vendutoPerMese) {
		this.vendutoPerMese = vendutoPerMese;
	}

}

