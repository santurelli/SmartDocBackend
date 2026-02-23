package it.tinna.smartdoc.server.constants;

public enum FormatoTrasmissioneEnum
{
 FATTURA_PA("Fattura verso PA"),
 FATTURA_PRIVATI("Fattura verso privati");

    private String descrizione;

    private FormatoTrasmissioneEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

