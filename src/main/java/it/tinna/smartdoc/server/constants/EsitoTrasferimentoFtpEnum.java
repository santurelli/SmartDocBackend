package it.tinna.smartdoc.server.constants;

public enum EsitoTrasferimentoFtpEnum
{

 OK("ET01"),
 ERRORE("ET02");

    private String codice;

    private EsitoTrasferimentoFtpEnum(String codice)
    {
        this.codice = codice;
    }

    public String getCodice()
    {
        return codice;
    }

}

