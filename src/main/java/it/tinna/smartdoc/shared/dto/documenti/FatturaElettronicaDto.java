package it.tinna.smartdoc.shared.dto.documenti;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
@ToString(callSuper = false, onlyExplicitlyIncluded = true, includeFieldNames = true)
public class FatturaElettronicaDto extends DocumentoDto
{
    @com.google.gson.annotations.Expose
    private StatoFatturaElettronica statoFatturaElettronica;

    @com.google.gson.annotations.Expose
    private Integer splitPayment;

    @com.google.gson.annotations.Expose
    private String tipoComunicazione;

    @com.google.gson.annotations.Expose
    private Integer progInvioFatturaElettronica;

    @com.google.gson.annotations.Expose
    private String progFileFatturaElettronica;

    @com.google.gson.annotations.Expose
    private String dtLiquidazioneProvvigione;

    @com.google.gson.annotations.Expose
    private Integer flRitenutaAcconto;

    @com.google.gson.annotations.Expose
    private Double percRitenutaAcconto;

    @com.google.gson.annotations.Expose
    private java.math.BigDecimal importoRitenutaAcconto;

    @com.google.gson.annotations.Expose
    private String tipoRitenuta;
}

