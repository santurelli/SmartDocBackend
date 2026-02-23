package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("serial")
public class BollaCaricoDto extends DocumentoAcquistoDto implements HasContabilita {
	
	private Integer idParametrizzazione;
	private BigDecimal imponibileContabilita;
	private BigDecimal impostaContabilita;
	private BigDecimal totaleContabilita;
	
	private List<Integer> idOrdine;
	
	public List<Integer> getIdOrdine() {
		return idOrdine;
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

	public void setIdOrdine(List<Integer> idDocDaAssociare) {
		this.idOrdine = idDocDaAssociare;
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

