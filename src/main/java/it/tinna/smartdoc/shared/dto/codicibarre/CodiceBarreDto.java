package it.tinna.smartdoc.shared.dto.codicibarre;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class CodiceBarreDto extends BaseDto
{

    public boolean equals(Object obj)
    {
        if ( obj instanceof CodiceBarreDto )
        {
            return ((CodiceBarreDto) obj).getId() == this.getId();
        }
        return false;
    }

    private String  codice;

    private long    idProdotto;

    private Integer idTaglia;

    private String  descrTaglia;

    private long    idColore;

    private String  descrColore;

    private Integer idTono;

    private String  descrTono;

    private long    idScelta;

    private String  descrScelta;

    public String getCodice()
    {
        return codice;
    }

    public void setCodice(String codice)
    {
        this.codice = codice;
    }

    public long getIdProdotto()
    {
        return idProdotto;
    }

    public void setIdProdotto(long idProdotto)
    {
        this.idProdotto = idProdotto;
    }

    public Integer getIdTaglia()
    {
        return idTaglia;
    }

    public void setIdTaglia(Integer idTaglia)
    {
        this.idTaglia = idTaglia;
    }

    public String getDescrTaglia()
    {
        return descrTaglia;
    }

    public void setDescrTaglia(String descrTaglia)
    {
        this.descrTaglia = descrTaglia;
    }

    public long getIdColore()
    {
        return idColore;
    }

    public void setIdColore(long idColore)
    {
        this.idColore = idColore;
    }

    public String getDescrColore()
    {
        return descrColore;
    }

    public void setDescrColore(String descrColore)
    {
        this.descrColore = descrColore;
    }

    public Integer getIdTono()
    {
        return idTono;
    }

    public void setIdTono(Integer idTono)
    {
        this.idTono = idTono;
    }

    public String getDescrTono()
    {
        return descrTono;
    }

    public void setDescrTono(String descrTono)
    {
        this.descrTono = descrTono;
    }

    public long getIdScelta()
    {
        return idScelta;
    }

    public void setIdScelta(long idScelta)
    {
        this.idScelta = idScelta;
    }

    public String getDescrScelta()
    {
        return descrScelta;
    }

    public void setDescrScelta(String descrScelta)
    {
        this.descrScelta = descrScelta;
    }

}
