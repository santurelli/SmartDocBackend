package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class RiepilogoFattureDto extends BaseDto
{
    private double totDaSaldare;

    private double totFatturato;

    private long   totRighe;

    private double totSaldato;

    public double getTotDaSaldare()
    {
        return totDaSaldare;
    }

    public double getTotFatturato()
    {
        return totFatturato;
    }

    public long getTotRighe()
    {
        return totRighe;
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

    public void setTotRighe(long totRighe)
    {
        this.totRighe = totRighe;
    }

    public void setTotSaldato(double totSaldato)
    {
        this.totSaldato = totSaldato;
    }

}

