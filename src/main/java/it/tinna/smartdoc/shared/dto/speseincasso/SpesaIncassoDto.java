package it.tinna.smartdoc.shared.dto.speseincasso;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class SpesaIncassoDto extends BaseDto
{

    private String  codiceIva;

    private String  descrizione;

    private Integer idAliquotaIva;

    private Double  importo;

    private Integer trasporto;

    public String getCodiceIva()
    {
        return codiceIva;
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public Integer getIdAliquotaIva()
    {
        return idAliquotaIva;
    }

    public Double getImporto()
    {
        return importo;
    }

    public Integer getTrasporto()
    {
        return trasporto;
    }

    public void setCodiceIva(String codiceIva)
    {
        this.codiceIva = codiceIva;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setIdAliquotaIva(Integer idAliquotaIva)
    {
        this.idAliquotaIva = idAliquotaIva;
    }

    public void setImporto(Double importo)
    {
        this.importo = importo;
    }

    public void setTrasporto(Integer trasporto)
    {
        this.trasporto = trasporto;
    }

}

