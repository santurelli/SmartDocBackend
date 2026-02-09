package it.tinna.smartdoc.server.constants;

public enum ModalitaPagamentoEnum
{
 CONTANTI("Contanti"),
 ASSEGNO_CIRCOLARE("Assegno circolare"),
 ASSEGNO("Assegno"),
 BOLLETTINO_BANCA("Bollettino bancario"),
 BONIFICO("Bonifico"),
 CARTA_CREDITO("Carta di credito"),
 RID("RID"),
 RID_UTENZE("Rid Utenze"),
 RID_VELOCE("Rid Veloce"),
 RIBA("Riba"),
 MAV("MAV"),
 CONTANTI_TEROSERIA("Contanti presso tesoreria"),
 VAGLIA("Vaglia bancario"),
 ERARIO("Quietanza erario"),
 DOMICILIAZIONE_BANCA("Domiciliazione bancaria"),
 DOMICILIAZIONE_POSTA("Domiciliazione postale"),
 GIROCONTO("Giroconto su conti di contabilità speciale"),
 BOLLETTINO_POSTA("Bollettino di c/c postale"),
 SEPA_DD("SEPA Direct Debit"),
 SEPA_DDC("SEPA Direct Debit CORE"),
 SEPA_B2B("SEPA Direct Debit B2B"),
 TRATTENUTA_RISCOSSE("Trattenuta su somme riscosse");

    private String descrizione;

    private ModalitaPagamentoEnum(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}
