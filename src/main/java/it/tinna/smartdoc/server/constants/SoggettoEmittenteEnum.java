package it.tinna.smartdoc.server.constants;

public enum SoggettoEmittenteEnum
{
 CESSIONARIO_COMMITTENTE("Cessionario / Committente"),
 TERZO("Terzo");

    private String descrizione;

    private SoggettoEmittenteEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

