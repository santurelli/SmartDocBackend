package it.tinna.smartdoc.shared.dto.conti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class ContoDto extends BaseDto
{

    private String  codice;

    private String  descCeeAvere;

    private String  descCeeDare;

    @Expose
    private String  descrizione;

    private boolean folder;

    private Integer idCeeAvere;

    private Integer idCeeDare;

    private Integer idPadre;

    private String  note;

    private Integer parametrizzato;

    private String  path;

    @Expose
    private int     predefinito;

    private boolean sistema;

    private String  tabellaRiferimento;

    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof ContoDto )
        {
            return ((ContoDto) obj).getId() == this.getId();
        }
        return false;
    }

    public String getCodice()
    {
        return codice;
    }

    public String getDescCeeAvere()
    {
        return descCeeAvere;
    }

    public String getDescCeeDare()
    {
        return descCeeDare;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public Integer getIdCeeAvere()
    {
        return idCeeAvere;
    }

    public Integer getIdCeeDare()
    {
        return idCeeDare;
    }

    public Integer getIdPadre()
    {
        return idPadre;
    }

    public String getNote()
    {
        return note;
    }

    public Integer getParametrizzato()
    {
        return parametrizzato;
    }

    public String getPath()
    {
        return path;
    }

    public int getPredefinito()
    {
        return predefinito;
    }

    public String getTabellaRiferimento()
    {
        return tabellaRiferimento;
    }

    public boolean isFolder()
    {
        return folder;
    }

    public boolean isSistema()
    {
        return sistema;
    }

    public void setCodice(String codice)
    {
        this.codice = codice;
    }

    public void setDescCeeAvere(String descCeeAvere)
    {
        this.descCeeAvere = descCeeAvere;
    }

    public void setDescCeeDare(String descCeeDare)
    {
        this.descCeeDare = descCeeDare;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setFolder(boolean folder)
    {
        this.folder = folder;
    }

    public void setIdCeeAvere(Integer idCeeAvere)
    {
        this.idCeeAvere = idCeeAvere;
    }

    public void setIdCeeDare(Integer idCeeDare)
    {
        this.idCeeDare = idCeeDare;
    }

    public void setIdPadre(Integer idPadre)
    {
        this.idPadre = idPadre;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public void setParametrizzato(Integer parametrizzato)
    {
        this.parametrizzato = parametrizzato;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setPredefinito(int predefinito)
    {
        this.predefinito = predefinito;
    }

    public void setSistema(boolean sistema)
    {
        this.sistema = sistema;
    }

    public void setTabellaRiferimento(String tabellaRiferimento)
    {
        this.tabellaRiferimento = tabellaRiferimento;
    }

}

