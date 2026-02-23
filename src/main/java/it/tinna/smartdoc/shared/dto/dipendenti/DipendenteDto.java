package it.tinna.smartdoc.shared.dto.dipendenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.clienti.BaseClienteDto;

@SuppressWarnings("serial")
public class DipendenteDto extends BaseClienteDto
{
    @Expose
    private String cognome;

    @Expose
    private String dtNascita;

    @Expose
    private String nome;

    public String getCognome()
    {
        return cognome;
    }

    public String getDtNascita()
    {
        return dtNascita;
    }

    public String getNome()
    {
        return nome;
    }

    public void setCognome(String cognome)
    {
        this.cognome = cognome;
    }

    public void setDtNascita(String dtNascita)
    {
        this.dtNascita = dtNascita;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

}

