package it.tinna.smartdoc.server.constants;

public enum TipoDocumentoEnum
{

 FATTURA("Fattura"),
 FATTURA_PRO_FORMA("Fattura pro-forma"),
 NOTA_CREDITO("Nota di credito"),
 NOTA_DEBITO("Nota di debito"),
 NOTA_CREDITO_AS("Nota di credito dell'ammontare stabilito"),
 NOTA_DEBITO_AS("Nota di debito dell'ammontare stabilito"),
 FATTURA_BODY_RENTAL("Fattura per body-rental"),
 FATTURA_SERVIZI("Fattura per servizi"),
 ACCONTO_ANTICIPO_FATTURA("Acconto/anticipo su fattura"),
 ACCONTO_ANTICIPO_PARCELLA("Acconto/anticipo su parcella"),
 PARCELLA("Parcella");

    private String descrizione;

    private TipoDocumentoEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

}

