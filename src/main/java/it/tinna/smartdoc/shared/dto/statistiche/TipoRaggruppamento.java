package it.tinna.smartdoc.shared.dto.statistiche;

public enum TipoRaggruppamento
{
 MESE("Mese"),
 GIORNO("Giorno"),
 TRIMESTRE("Trimestre"),
 ANNOTEMPORALE("Anno"),
 CLIENTE("Cliente"),
 AGENTE("Agente"),
 PAGAMENTO("Pagamento"),
 PRODOTTO("Prodotto"),
 CATEGORIA_PRODOTTO("Categoria prodotto"),
 SOTTOCATEGORIA_PRODOTTO("Sottocat. prodotto"),
 FORNITORE("Fornitore"),
 DIVISIONE("Divisione"),
 CITTA("Città"),
 PROVINCIA("Provincia"),
 NAZIONE("Nazione"),
 TIPO_DOCUMENTO("Tipo documento");

    private String descrizione;

    private TipoRaggruppamento(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public String getDescrizione()
    {
        return descrizione;
    }
}

