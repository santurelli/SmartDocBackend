package it.tinna.smartdoc.shared.dto.documenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@SuppressWarnings("serial")
public class DocumentiListResponse<T extends DocumentoDto> extends DatatablesResponseDto<T>
{
    @Expose
    private double totDaSaldare;

    @Expose
    private double totFatturato;

    @Expose
    private double totSaldato;

    public double getTotDaSaldare()
    {
        return totDaSaldare;
    }

    public double getTotFatturato()
    {
        return totFatturato;
    }

    public double getTotSaldato()
    {
        return totSaldato;
    }

    public void setTotDaSaldare(double totDaSaldare)
    {
        this.totDaSaldare = totDaSaldare;
    }

    public void setTotFatturato(double totFatturato)
    {
        this.totFatturato = totFatturato;
    }

    public void setTotSaldato(double totSaldato)
    {
        this.totSaldato = totSaldato;
    }

}

