package it.tinna.smartdoc.shared.dto.ritenute;

import java.math.BigDecimal;

public class RitenutaFornitoreDto {

    private Integer idFornitore;
    private String denominazioneFornitore;
    private String codiceFiscaleFornitore;
    private String partitaIvaFornitore;
    private String causalePagamento;
    private String tipoRitenuta;
    private BigDecimal totaleCompensi;
    private BigDecimal totaleRitenute;
    private Integer numeroFatture;

    public Integer getIdFornitore() { return idFornitore; }
    public void setIdFornitore(Integer idFornitore) { this.idFornitore = idFornitore; }

    public String getDenominazioneFornitore() { return denominazioneFornitore; }
    public void setDenominazioneFornitore(String denominazioneFornitore) { this.denominazioneFornitore = denominazioneFornitore; }

    public String getCodiceFiscaleFornitore() { return codiceFiscaleFornitore; }
    public void setCodiceFiscaleFornitore(String codiceFiscaleFornitore) { this.codiceFiscaleFornitore = codiceFiscaleFornitore; }

    public String getPartitaIvaFornitore() { return partitaIvaFornitore; }
    public void setPartitaIvaFornitore(String partitaIvaFornitore) { this.partitaIvaFornitore = partitaIvaFornitore; }

    public String getCausalePagamento() { return causalePagamento; }
    public void setCausalePagamento(String causalePagamento) { this.causalePagamento = causalePagamento; }

    public String getTipoRitenuta() { return tipoRitenuta; }
    public void setTipoRitenuta(String tipoRitenuta) { this.tipoRitenuta = tipoRitenuta; }

    public BigDecimal getTotaleCompensi() { return totaleCompensi; }
    public void setTotaleCompensi(BigDecimal totaleCompensi) { this.totaleCompensi = totaleCompensi; }

    public BigDecimal getTotaleRitenute() { return totaleRitenute; }
    public void setTotaleRitenute(BigDecimal totaleRitenute) { this.totaleRitenute = totaleRitenute; }

    public Integer getNumeroFatture() { return numeroFatture; }
    public void setNumeroFatture(Integer numeroFatture) { this.numeroFatture = numeroFatture; }
}
