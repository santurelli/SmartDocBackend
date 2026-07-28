package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("serial")
public class FatturaFornitoreDto extends DocumentoAcquistoDto implements HasContabilita {
	
	private Integer idParametrizzazione;
	private String tipoDocumentoSdi;
	private BigDecimal imponibileContabilita;
	private BigDecimal impostaContabilita;
	private BigDecimal totaleContabilita;
	
	private List<Integer> idBolleCarico;
	private List<Integer> idOrdini;
	
	public List<Integer> getIdBolleCarico() {
		return idBolleCarico;
	}
	
	public List<Integer> getIdOrdini() {
		return idOrdini;
	}

	public String getTipoDocumentoSdi() {
		return tipoDocumentoSdi;
	}

	public void setTipoDocumentoSdi(String tipoDocumentoSdi) {
		this.tipoDocumentoSdi = tipoDocumentoSdi;
	}

	@Override
	public Integer getIdParametrizzazione() {
		return idParametrizzazione;
	}

	@Override
	public BigDecimal getImponibileContabilita() {
		return imponibileContabilita;
	}

	@Override
	public BigDecimal getImpostaContabilita() {
		return impostaContabilita;
	}

	@Override
	public BigDecimal getTotaleContabilita() {
		return totaleContabilita;
	}

	public void setIdBolleCarico(List<Integer> idBollaCarico) {
		this.idBolleCarico = idBollaCarico;
	}

	public void setIdOrdini(List<Integer> idDocDaAssociare) {
		this.idOrdini = idDocDaAssociare;
	}

	@Override
	public void setIdParametrizzazione(Integer idParametrizzazione) {
		this.idParametrizzazione = idParametrizzazione;
	}

	@Override
	public void setImponibileContabilita(BigDecimal imponibileContabilita) {
		this.imponibileContabilita = imponibileContabilita;
	}

	@Override
	public void setImpostaContabilita(BigDecimal impostaContabilita) {
		this.impostaContabilita = impostaContabilita;
	}

	@Override
	public void setTotaleContabilita(BigDecimal totaleContabilita) {
		this.totaleContabilita = totaleContabilita;
	}

}

