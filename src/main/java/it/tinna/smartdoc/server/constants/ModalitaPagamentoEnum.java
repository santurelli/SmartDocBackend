package it.tinna.smartdoc.server.constants;

public enum ModalitaPagamentoEnum
{
    CONTANTI("Contanti", "CO", "MP01"),
    ASSEGNO_CIRCOLARE("Assegno circolare", "AC", "MP03"),
    ASSEGNO("Assegno", "AS", "MP02"),
    BOLLETTINO_BANCA("Bollettino bancario", "BB", "MP07"),
    BONIFICO("Bonifico", "BO", "MP05"),
    CARTA_CREDITO("Carta di credito", "CC", "MP08"),
    RID("RID", "RD", "MP09"),
    RID_UTENZE("Rid Utenze", "RU", "MP10"),
    RID_VELOCE("Rid Veloce", "RV", "MP11"),
    RIBA("Riba", "RI", "MP12"),
    MAV("MAV", "MA", "MP13"),
    CONTANTI_TEROSERIA("Contanti presso tesoreria", "CT", "MP04"),
    VAGLIA("Vaglia bancario", "VA", "MP06"),
    ERARIO("Quietanza erario", "ER", "MP14"),
    DOMICILIAZIONE_BANCA("Domiciliazione bancaria", "DB", "MP16"),
    DOMICILIAZIONE_POSTA("Domiciliazione postale", "DP", "MP17"),
    GIROCONTO("Giroconto su conti di contabilità speciale", "GC", "MP15"),
    BOLLETTINO_POSTA("Bollettino di c/c postale", "BP", "MP18"),
    SEPA_DD("SEPA Direct Debit", "SD", "MP19"),
    SEPA_DDC("SEPA Direct Debit CORE", "SC", "MP20"),
    SEPA_B2B("SEPA Direct Debit B2B", "SB", "MP21"),
    TRATTENUTA_RISCOSSE("Trattenuta su somme riscosse", "TR", "MP22");

    private String descrizione;
    private String codice;
    private String codiceSdi;

    private ModalitaPagamentoEnum(String descrizione, String codice, String codiceSdi)
    {
        this.descrizione = descrizione;
        this.codice = codice;
        this.codiceSdi = codiceSdi;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public String getCodice()
    {
        return codice;
    }

    public String getCodiceSdi()
    {
        return codiceSdi;
    }

    public static ModalitaPagamentoEnum fromCodice(String val)
    {
        if ( val == null || val.trim().isEmpty() )
        {
            return null;
        }
        for ( ModalitaPagamentoEnum m : ModalitaPagamentoEnum.values() )
        {
            if ( m.name().equalsIgnoreCase(val.trim()) || (m.getCodice() != null && m.getCodice().equalsIgnoreCase(val.trim())) || (m.getCodiceSdi() != null && m.getCodiceSdi().equalsIgnoreCase(val.trim())) )
            {
                return m;
            }
        }
        return null;
    }

}

