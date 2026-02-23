package it.tinna.smartdoc.shared.dto.statistiche;

public enum TipoRaggruppamento
{
 MESE("Mese"),
 CLIENTE("Cliente"),
 AGENTE("Agente"),
 PAGAMENTO("Pagamento"),
 PRODOTTO("Prodotto"),
 CATEGORIA_PRODOTTO("Categoria prodotto"),
 SOTTOCATEGORIA_PRODOTTO("Sottocat. prodotto"),
 FORNITORE("Fornitore"),
 DIVISIONE("Divisione");

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

