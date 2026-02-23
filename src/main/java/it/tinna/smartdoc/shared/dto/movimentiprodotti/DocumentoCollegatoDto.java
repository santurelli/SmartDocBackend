package it.tinna.smartdoc.shared.dto.movimentiprodotti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class DocumentoCollegatoDto extends BaseDto
{

    private String data;

    private String numero;

    private String tipo;

    public String getData()
    {
        return data;
    }

    public String getNumero()
    {
        return numero;
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setData(String descrizione)
    {
        this.data = descrizione;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

}

