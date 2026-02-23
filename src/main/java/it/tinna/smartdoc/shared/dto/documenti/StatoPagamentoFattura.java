package it.tinna.smartdoc.shared.dto.documenti;

public enum StatoPagamentoFattura
{

 PAGATA("P")/* somma pagamenti = importo fattura */,
 PARZIALMENTE_PAGATA("PP")/* Almeno un pagamento registrato */,
 NON_PAGATA("N")/* Quando non è stato registrato alcun pagamento */;

    private String value;

    private StatoPagamentoFattura(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

}

