package it.tinna.smartdoc.shared.dto.causalicontabili;

import it.tinna.smartdoc.shared.dto.conti.ContoDto;

@SuppressWarnings("serial")
public class ContoCausaleDto extends ContoDto
{

    private Integer dare;

    // private String descrizioneConto;
    private String  descrizioneTipoImporto;

    private Integer idCausaleContabile;

    private Integer idConto;

    private String  tipoImporto;

    public Integer getDare()
    {
        return dare;
    }

    // public String getDescrizioneConto() {
    // return descrizioneConto;
    // }

    public String getDescrizioneTipoImporto()
    {
        return descrizioneTipoImporto;
    }

    public Integer getIdCausaleContabile()
    {
        return idCausaleContabile;
    }

    public Integer getIdConto()
    {
        return idConto;
    }

    public String getTipoImporto()
    {
        return tipoImporto;
    }

    public void setDare(Integer dare)
    {
        this.dare = dare;
    }

    // public void setDescrizioneConto(String descrizioneConto) {
    // this.descrizioneConto = descrizioneConto;
    // }

    public void setDescrizioneTipoImporto(String descrizioneTipoImporto)
    {
        this.descrizioneTipoImporto = descrizioneTipoImporto;
    }

    public void setIdCausaleContabile(Integer idCausaleContabile)
    {
        this.idCausaleContabile = idCausaleContabile;
    }

    public void setIdConto(Integer idConto)
    {
        this.idConto = idConto;
    }

    public void setTipoImporto(String tipoImporto)
    {
        this.tipoImporto = tipoImporto;
    }

}

