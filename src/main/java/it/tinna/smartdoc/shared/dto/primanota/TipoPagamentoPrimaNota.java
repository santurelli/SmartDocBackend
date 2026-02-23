package it.tinna.smartdoc.shared.dto.primanota;

public enum TipoPagamentoPrimaNota
{
 E("Entrata"),
 U("Uscita");

    private String descrizione;

    TipoPagamentoPrimaNota(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

}

