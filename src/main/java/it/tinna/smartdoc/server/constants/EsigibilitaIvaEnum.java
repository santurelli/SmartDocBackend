package it.tinna.smartdoc.server.constants;

public enum EsigibilitaIvaEnum
{
 DIFFERITA("Differita"),
 IMMEDIATA("Immediata"),
 SCISSIONE("Scissione dei pagamenti");

    private String descrizione;

    private EsigibilitaIvaEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

