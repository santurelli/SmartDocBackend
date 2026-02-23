package it.tinna.smartdoc.shared.dto.statistiche;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class StatisticaPagamentoDto extends BaseDto
{

    @Expose
    private String key;

    @Expose
    private double entrate;

    @Expose
    private double saldo;

    @Expose
    private double uscite;

    public String getKey()
    {
        return key;
    }

    public double getEntrate()
    {
        return entrate;
    }

    public double getSaldo()
    {
        return saldo;
    }

    public double getUscite()
    {
        return uscite;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setEntrate(double entrate)
    {
        this.entrate = entrate;
    }

    public void setSaldo(double saldo)
    {
        this.saldo = saldo;
    }

    public void setUscite(double uscite)
    {
        this.uscite = uscite;
    }

}

