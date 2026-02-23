package it.tinna.smartdoc.shared.dto.sceltecolori;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings(
{ "serial",
  "rawtypes" })
public class SceltaColoreDto<T extends SceltaColoreDto> extends BaseDto implements Comparable<T>
{

    private String  descrizione;

    private Integer idProdotto;

    @Override
    public int compareTo(T o)
    {
        return this.getDescrizione().compareToIgnoreCase(o.getDescrizione());
    }

    public String getDescrizione()
    {
        return descrizione;
    }

    public Integer getIdProdotto()
    {
        return idProdotto;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setIdProdotto(Integer idProdotto)
    {
        this.idProdotto = idProdotto;
    }
}

