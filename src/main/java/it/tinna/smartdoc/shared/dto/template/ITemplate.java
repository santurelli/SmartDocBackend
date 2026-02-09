package it.tinna.smartdoc.shared.dto.template;

import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

import java.util.List;

public abstract class ITemplate {
	
	private List<ProdottoDocumentoDto> prodotti;
	private List<RiepilogoIvaDto> riepilogoIva;
	private List<ScadenzaPagamentoDocumentoDto> scadenze;
	
	public List<ProdottoDocumentoDto> getProdotti() {
		return prodotti;
	}

	public List<RiepilogoIvaDto> getRiepilogoIva() {
		return riepilogoIva;
	}

	public List<ScadenzaPagamentoDocumentoDto> getScadenze() {
		return scadenze;
	}

	public void setProdotti(List<ProdottoDocumentoDto> prodotti) {
		this.prodotti = prodotti;
	}

	public void setRiepilogoIva(List<RiepilogoIvaDto> riepilogoIva) {
		this.riepilogoIva = riepilogoIva;
	}

	public void setScadenze(List<ScadenzaPagamentoDocumentoDto> scadenze) {
		this.scadenze = scadenze;
	}

}
