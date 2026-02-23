package it.tinna.smartdoc.shared.dto.documenti;

public enum StatoFatturaElettronica
{

 BO("Bozza"),
 DI("Da inviare"),
 IN("Inviata"),
 AC("Accettata"),
 NS("Scartata"),
 RC("Consegnata"),
 MC("Mancata consegna"),
 RF("Rifiutata");

    private String descrizione;

    StatoFatturaElettronica(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

}

