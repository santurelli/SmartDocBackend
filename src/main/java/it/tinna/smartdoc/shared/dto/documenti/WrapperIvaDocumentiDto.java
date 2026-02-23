package it.tinna.smartdoc.shared.dto.documenti;

import java.util.List;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;

@SuppressWarnings("serial")
public class WrapperIvaDocumentiDto extends BaseDto {
	
	private List<IvaDocumentoDto> documenti;
	private List<RiepilogoIvaDto> riepilogo;
	private String totDetraibile;
	private String totImponibile;
	private String totaleImposta;
	private String totIndetraibile;

	public List<IvaDocumentoDto> getDocumenti() {
		return documenti;
	}

	public List<RiepilogoIvaDto> getRiepilogo() {
		return riepilogo;
	}

	public String getTotaleImposta() {
		return totaleImposta;
	}

	public String getTotDetraibile() {
		return totDetraibile;
	}

	public String getTotImponibile() {
		return totImponibile;
	}

	public String getTotIndetraibile() {
		return totIndetraibile;
	}

	public void setDocumenti(List<IvaDocumentoDto> documenti) {
		this.documenti = documenti;
	}

	public void setRiepilogo(List<RiepilogoIvaDto> riepilogo) {
		this.riepilogo = riepilogo;
	}

	public void setTotaleImposta(String totaleImposta) {
		this.totaleImposta = totaleImposta;
	}

	public void setTotDetraibile(String totDetraibile) {
		this.totDetraibile = totDetraibile;
	}

	public void setTotImponibile(String totImponibile) {
		this.totImponibile = totImponibile;
	}

	public void setTotIndetraibile(String totIndetraibile) {
		this.totIndetraibile = totIndetraibile;
	}

}

