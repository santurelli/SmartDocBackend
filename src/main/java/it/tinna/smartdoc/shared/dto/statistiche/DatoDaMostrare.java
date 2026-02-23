package it.tinna.smartdoc.shared.dto.statistiche;

public enum DatoDaMostrare
{
 NUMERO_DOCUMENTI("Num. documenti"),
 IMPONIBILE("Imponibile"),
 IVA("Iva"),
 TOTALE_DOCUMENTO("Totale documento"),
 QUANTITA_PRODOTTI("Quantità"),
 IMPORTO_PRODOTTI("Importo"),
 IMPORTO_PRODOTTI_IVATO("Importo ivato"),
 PREZZO_MEDIO_PRODOTTI("Prezzo medio"),
 PREZZO_MASSIMO_PRODOTTI("Prezzo massimo"),
 PREZZO_MINIMO_PRODOTTI("Prezzo minimo");

    private String descrizione;

    private DatoDaMostrare(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

}

