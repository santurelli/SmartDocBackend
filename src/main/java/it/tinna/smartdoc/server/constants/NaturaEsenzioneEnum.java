package it.tinna.smartdoc.server.constants;

public enum NaturaEsenzioneEnum
{
 ESCLUSE_ART_15("Escluse ex art. 15"),
 NON_SOGGETTE("Non soggette"),
 NON_IMPONIBILI("Non imponibili"),
 ESENTI("Esenti"),
 REGIME("Regime del margine"),
 INVERSIONE("Inversione contabile"),
 IVA_UE("Iva assolta in altro stato UE");

    private String descrizione;

    private NaturaEsenzioneEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

