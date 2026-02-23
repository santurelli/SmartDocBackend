package it.tinna.smartdoc.server.constants;

public enum TipoPagamentoEnum
{
 COMPLETO("Pagamento completo"),
 RATE("Pagamento a rate"),
 ANTICIPO("Anticipo");

    private String descrizione;

    private TipoPagamentoEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

