package it.tinna.smartdoc.shared.dto.tonitaglie;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings(
{ "serial",
  "rawtypes" })
public class TonoTagliaDto<T extends TonoTagliaDto> extends BaseDto implements Comparable<T>
{

    private String  descrizione;

    private Integer idGruppo;

    @Override
    public int compareTo(T o)
    {
        return this.getDescrizione().compareToIgnoreCase(o.getDescrizione());
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public Integer getIdGruppo()
    {
        return idGruppo;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setIdGruppo(Integer idGruppo)
    {
        this.idGruppo = idGruppo;
    }

}

