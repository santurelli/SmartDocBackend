package it.tinna.smartdoc.batch.dto.fastorder.comande;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.batch.dto.fastorder.piatti.PiattoDto;
import it.tinna.smartdoc.shared.dto.categorie.CategoriaDto;

@SuppressWarnings("serial")
public class PiattoComandaDto extends PiattoDto
{

    // private boolean checked;
    private CategoriaDto categoriaDto;

    @Expose
    private String       descrizionePiatto;

    private Integer      flPagato;

    // private Integer idCategoria;
    private Integer      idComanda;

    private Integer      idDestinazioneStampa;

    private Integer      idPiatto;

    private Integer      idRifVariante;

    // private Double prezzo;
    @Expose
    private Integer      quantita;

    private Integer      quantitaPagato;

    private boolean      variante;

    private Double       costoPiatto;

    private Double       totIncassato;

    private boolean      varianteLibera;

    private String       descrizioneVarianteLibera;

    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof PiattoComandaDto )
        {
            if ( ((PiattoComandaDto) obj).getId() == null || this.getId() == null )
            {
                return false;
            }
            else
            {
                return ((PiattoComandaDto) obj).getId().intValue() == this.getId().intValue();
            }
        }
        return false;
    }

    public CategoriaDto getCategoriaDto()
    {
        return categoriaDto;
    }

    public Double getCostoPiatto()
    {
        return costoPiatto;
    }

    public String getDescrizionePiatto()
    {
        return descrizionePiatto;
    }

    public String getDescrizioneVarianteLibera()
    {
        return descrizioneVarianteLibera;
    }

    public Integer getFlPagato()
    {
        return flPagato;
    }

    // public Integer getIdCategoria() {
    // return idCategoria;
    // }

    public Integer getIdComanda()
    {
        return idComanda;
    }

    public Integer getIdDestinazioneStampa()
    {
        return idDestinazioneStampa;
    }

    public Integer getIdPiatto()
    {
        return idPiatto;
    }

    // public Double getPrezzo() {
    // return prezzo;
    // }

    public Integer getIdRifVariante()
    {
        return idRifVariante;
    }

    public Integer getQuantita()
    {
        return quantita;
    }

    // public boolean isChecked() {
    // return checked;
    // }

    public Integer getQuantitaPagato()
    {
        return quantitaPagato;
    }

    // public void setChecked(boolean checked) {
    // this.checked = checked;
    // }

    public Double getTotIncassato()
    {
        return totIncassato;
    }

    public boolean isVariante()
    {
        return variante;
    }

    public boolean isVarianteLibera()
    {
        return varianteLibera;
    }

    public void setCategoriaDto(CategoriaDto categoriaDto)
    {
        this.categoriaDto = categoriaDto;
    }

    // public void setIdCategoria(Integer idCategoria) {
    // this.idCategoria = idCategoria;
    // }

    public void setCostoPiatto(Double costoPiatto)
    {
        this.costoPiatto = costoPiatto;
    }

    public void setDescrizionePiatto(String descrizionePiatto)
    {
        this.descrizionePiatto = descrizionePiatto;
    }

    public void setDescrizioneVarianteLibera(String descrizioneVarianteLibera)
    {
        this.descrizioneVarianteLibera = descrizioneVarianteLibera;
    }

    // public void setPrezzo(Double prezzo) {
    // this.prezzo = prezzo;
    // }

    public void setFlPagato(Integer flPagato)
    {
        this.flPagato = flPagato;
    }

    public void setIdComanda(Integer idComanda)
    {
        this.idComanda = idComanda;
    }

    public void setIdDestinazioneStampa(Integer idDestinazioneStampa)
    {
        this.idDestinazioneStampa = idDestinazioneStampa;
    }

    public void setIdPiatto(Integer idPiatto)
    {
        this.idPiatto = idPiatto;
    }

    public void setIdRifVariante(Integer idRifVariante)
    {
        this.idRifVariante = idRifVariante;
    }

    public void setQuantita(Integer quantita)
    {
        this.quantita = quantita;
    }

    public void setQuantitaPagato(Integer quantitaPagato)
    {
        this.quantitaPagato = quantitaPagato;
    }

    public void setTotIncassato(Double totIncassato)
    {
        this.totIncassato = totIncassato;
    }

    public void setVariante(boolean variante)
    {
        this.variante = variante;
        this.setFlVariante(variante ? 1 : 0);
    }

    public void setVarianteLibera(boolean varianteLibera)
    {
        this.varianteLibera = varianteLibera;
    }

}

