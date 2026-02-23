package it.tinna.smartdoc.server.constants;

public enum TipoScontoDocumentoEnum
{
 SCONTO("Sconto"),
 SCONTO_MERCE("Sconto merce"),
 MAGGIORAZIONE("Maggiorazione");

    private String descrizione;

    private TipoScontoDocumentoEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

