package it.tinna.smartdoc.server.constants;

public enum TipoRitenutaEnum
{
 PERSONE_FISICHE("Ritenuta di acconto persone fisiche"),
 PERSONE_GIURIDICHE("Ritenuta di acconto persone giuridiche");

    private String descrizione;

    private TipoRitenutaEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

