package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class DocumentoWrapperDto extends BaseDto
{
    private byte[] flusso;

    private String nome;

    public byte[] getFlusso()
    {
        return flusso;
    }

    public String getNome()
    {
        return nome;
    }

    public void setFlusso(byte[] flusso)
    {
        this.flusso = flusso;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

}

