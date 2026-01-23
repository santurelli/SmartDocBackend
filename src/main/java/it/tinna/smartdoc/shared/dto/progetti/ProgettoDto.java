package it.tinna.smartdoc.shared.dto.progetti;

import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaDto;

@SuppressWarnings("serial")
public class ProgettoDto extends BaseDto
{

    @Expose
    private String                      annoRiferimento;

    private List<FatturaDto>            fattureAttive;

    private List<MovimentiDocumentoDto> fatturePassive;

    private List<PrimaNotaDto>          pagamentiEffettuati;

    private List<PrimaNotaDto>          pagamentiRicevuti;

    @Expose
    private double                      totaleFatture;

    @Expose
    private double                      totaleFatturePassive;

    @Expose
    private double                      totaleIncassato;

    @Expose
    private double                      totaleSpese;

    public String getAnnoRiferimento()
    {
        return annoRiferimento;
    }

    public List<FatturaDto> getFattureAttive()
    {
        return fattureAttive;
    }

    public List<MovimentiDocumentoDto> getFatturePassive()
    {
        return fatturePassive;
    }

    public List<PrimaNotaDto> getPagamentiEffettuati()
    {
        return pagamentiEffettuati;
    }

    public List<PrimaNotaDto> getPagamentiRicevuti()
    {
        return pagamentiRicevuti;
    }

    public double getTotaleFatture()
    {
        return totaleFatture;
    }

    public double getTotaleFatturePassive()
    {
        return totaleFatturePassive;
    }

    public double getTotaleIncassato()
    {
        return totaleIncassato;
    }

    public double getTotaleSpese()
    {
        return totaleSpese;
    }

    public void setAnnoRiferimento(String annoRiferimento)
    {
        this.annoRiferimento = annoRiferimento;
    }

    public void setFattureAttive(List<FatturaDto> fattureAttive)
    {
        this.fattureAttive = fattureAttive;
    }

    public void setFatturePassive(List<MovimentiDocumentoDto> fatturePassive)
    {
        this.fatturePassive = fatturePassive;
    }

    public void setPagamentiEffettuati(List<PrimaNotaDto> pagamentieffettuati)
    {
        this.pagamentiEffettuati = pagamentieffettuati;
    }

    public void setPagamentiRicevuti(List<PrimaNotaDto> pagamentiRicevuti)
    {
        this.pagamentiRicevuti = pagamentiRicevuti;
    }

    public void setTotaleFatture(double totaleFatture)
    {
        this.totaleFatture = totaleFatture;
    }

    public void setTotaleFatturePassive(double totaleFatturePassive)
    {
        this.totaleFatturePassive = totaleFatturePassive;
    }

    public void setTotaleIncassato(double totaleIncassato)
    {
        this.totaleIncassato = totaleIncassato;
    }

    public void setTotaleSpese(double totaleSpese)
    {
        this.totaleSpese = totaleSpese;
    }

}
