package it.tinna.smartdoc.shared.dto.prodotti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class PrezzoProdottoDto extends BaseDto
{

    private String  descrizioneListino;

    @Expose
    private Integer idListino;

    private long    idProdotto;

    @Expose
    private Double  prezzo;

    public String getDescrizioneListino()
    {
        return descrizioneListino;
    }

    public Integer getIdListino()
    {
        return idListino;
    }

    public long getIdProdotto()
    {
        return idProdotto;
    }

    public Double getPrezzo()
    {
        return prezzo;
    }

    public void setDescrizioneListino(String descrizioneListino)
    {
        this.descrizioneListino = descrizioneListino;
    }

    public void setIdListino(Integer idListino)
    {
        this.idListino = idListino;
    }

    public void setIdProdotto(long idPiatto)
    {
        this.idProdotto = idPiatto;
    }

    public void setPrezzo(Double prezzo)
    {
        this.prezzo = prezzo;
    }

}
