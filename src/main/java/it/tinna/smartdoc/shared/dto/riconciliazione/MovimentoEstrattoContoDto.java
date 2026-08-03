package it.tinna.smartdoc.shared.dto.riconciliazione;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rappresentazione "grezza" di una riga di estratto conto, indipendente dal formato
 * sorgente (MT940 o CSV). Prodotta dai parser, consumata dal motore di matching.
 */
public class MovimentoEstrattoContoDto
{

    private LocalDate    dataValuta;
    private LocalDate    dataContabile;
    private BigDecimal   importo; // positivo = entrata, negativo = uscita
    private String       causaleBanca;
    private String       controparte;
    private String       ibanControparte;

    public LocalDate getDataValuta() { return dataValuta; }
    public void setDataValuta(LocalDate dataValuta) { this.dataValuta = dataValuta; }

    public LocalDate getDataContabile() { return dataContabile; }
    public void setDataContabile(LocalDate dataContabile) { this.dataContabile = dataContabile; }

    public BigDecimal getImporto() { return importo; }
    public void setImporto(BigDecimal importo) { this.importo = importo; }

    public String getCausaleBanca() { return causaleBanca; }
    public void setCausaleBanca(String causaleBanca) { this.causaleBanca = causaleBanca; }

    public String getControparte() { return controparte; }
    public void setControparte(String controparte) { this.controparte = controparte; }

    public String getIbanControparte() { return ibanControparte; }
    public void setIbanControparte(String ibanControparte) { this.ibanControparte = ibanControparte; }

}
