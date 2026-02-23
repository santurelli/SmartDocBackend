package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class DettaglioIvaDocumentoDto extends BaseDto {

	private String codiceAliquotaIva;
	private String descrizioneAliquotaIva;
	private Integer idAliquotaIva;
	private BigDecimal imponibile;
	private BigDecimal imposta;
	private Double indetraibilita;

	public String getCodiceAliquotaIva() {
		return codiceAliquotaIva;
	}

	public String getDescrizioneAliquotaIva() {
		return descrizioneAliquotaIva;
	}

	public Integer getIdAliquotaIva() {
		return idAliquotaIva;
	}

	public BigDecimal getImponibile() {
		return imponibile;
	}

	public BigDecimal getImposta() {
		return imposta;
	}

	public Double getIndetraibilita() {
		return indetraibilita;
	}

	public void setCodiceAliquotaIva(String codiceAliquotaIva) {
		this.codiceAliquotaIva = codiceAliquotaIva;
	}

	public void setDescrizioneAliquotaIva(String descrizioneAliquotaIva) {
		this.descrizioneAliquotaIva = descrizioneAliquotaIva;
	}

	public void setIdAliquotaIva(Integer idAliquotaIva) {
		this.idAliquotaIva = idAliquotaIva;
	}

	public void setImponibile(BigDecimal imponibile) {
		this.imponibile = imponibile;
	}

	public void setImposta(BigDecimal imposta) {
		this.imposta = imposta;
	}

	public void setIndetraibilita(Double indetraibilita) {
		this.indetraibilita = indetraibilita;
	}

}

