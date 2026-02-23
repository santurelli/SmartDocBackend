package it.tinna.smartdoc.server.constants;

public enum TipoCessazionePrestazioneEnum
{
 SCONTO("Sconto"),
 PREMIO("Premio"),
 ABBUONO("Abbuono"),
 SPECSA_ACCESSORIA("Spesa accessoria");

    private String descrizione;

    private TipoCessazionePrestazioneEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

