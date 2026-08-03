package it.tinna.smartdoc.shared.dto.riconciliazione;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RiconciliazioneCsvMappingDto extends BaseDto
{

    private Integer idRisorsa;
    private String  delimitatore;
    private Integer flHaIntestazione;
    private String  formatoData;
    private Integer colData;
    private Integer colImporto;
    private Integer colCausale;
    private Integer colControparte;
    private Integer colIbanControparte;
    private Integer flImportoUnicoConSegno;
    private Integer colImportoEntrata;
    private Integer colImportoUscita;

    public Integer getIdRisorsa() { return idRisorsa; }
    public void setIdRisorsa(Integer idRisorsa) { this.idRisorsa = idRisorsa; }

    public String getDelimitatore() { return delimitatore; }
    public void setDelimitatore(String delimitatore) { this.delimitatore = delimitatore; }

    public Integer getFlHaIntestazione() { return flHaIntestazione; }
    public void setFlHaIntestazione(Integer flHaIntestazione) { this.flHaIntestazione = flHaIntestazione; }

    public String getFormatoData() { return formatoData; }
    public void setFormatoData(String formatoData) { this.formatoData = formatoData; }

    public Integer getColData() { return colData; }
    public void setColData(Integer colData) { this.colData = colData; }

    public Integer getColImporto() { return colImporto; }
    public void setColImporto(Integer colImporto) { this.colImporto = colImporto; }

    public Integer getColCausale() { return colCausale; }
    public void setColCausale(Integer colCausale) { this.colCausale = colCausale; }

    public Integer getColControparte() { return colControparte; }
    public void setColControparte(Integer colControparte) { this.colControparte = colControparte; }

    public Integer getColIbanControparte() { return colIbanControparte; }
    public void setColIbanControparte(Integer colIbanControparte) { this.colIbanControparte = colIbanControparte; }

    public Integer getFlImportoUnicoConSegno() { return flImportoUnicoConSegno; }
    public void setFlImportoUnicoConSegno(Integer flImportoUnicoConSegno) { this.flImportoUnicoConSegno = flImportoUnicoConSegno; }

    public Integer getColImportoEntrata() { return colImportoEntrata; }
    public void setColImportoEntrata(Integer colImportoEntrata) { this.colImportoEntrata = colImportoEntrata; }

    public Integer getColImportoUscita() { return colImportoUscita; }
    public void setColImportoUscita(Integer colImportoUscita) { this.colImportoUscita = colImportoUscita; }

}
