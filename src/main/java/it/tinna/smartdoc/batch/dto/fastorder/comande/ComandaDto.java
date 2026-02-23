package it.tinna.smartdoc.batch.dto.fastorder.comande;

import java.util.List;

import it.tinna.smartdoc.batch.dto.fastorder.BaseDto;

@SuppressWarnings("serial")
public class ComandaDto extends BaseDto
{

    private boolean                checked;

    private String                 descTavoloOccupato;

    private Integer                id;

    // private Integer idCliente;
    private Integer                idListino;

    private Integer                idTavoloOccupato;

    private String                 nomeTavoloOccupato;

    private Integer                numPersone;

    // private Double sconto;
    // private Double servizio;
    // private String tipoDocumento;
    // private Double totale;
    private PagamentoComandaDto    pagamentoComanda;

    private List<PiattoComandaDto> piatti;

    private String                 tipoConto;

    public String getDescTavoloOccupato()
    {
        return descTavoloOccupato;
    }

    public Integer getId()
    {
        return id;
    }

    // public Integer getIdCliente() {
    // return idCliente;
    // }

    public Integer getIdListino()
    {
        return idListino;
    }

    public Integer getIdTavoloOccupato()
    {
        return idTavoloOccupato;
    }

    // public Double getSconto() {
    // return sconto;
    // }

    // public Double getServizio() {
    // return servizio;
    // }

    // public String getTipoDocumento() {
    // return tipoDocumento;
    // }

    // public Double getTotale() {
    // return totale;
    // }

    public String getNomeTavoloOccupato()
    {
        return nomeTavoloOccupato;
    }

    public Integer getNumPersone()
    {
        return numPersone;
    }

    public PagamentoComandaDto getPagamentoComanda()
    {
        return pagamentoComanda;
    }

    public List<PiattoComandaDto> getPiatti()
    {
        return piatti;
    }

    // public void setIdCliente(Integer idCliente) {
    // this.idCliente = idCliente;
    // }

    public String getTipoConto()
    {
        return tipoConto;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }

    public void setDescTavoloOccupato(String descTavoloOccupato)
    {
        this.descTavoloOccupato = descTavoloOccupato;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public void setIdListino(Integer idListino)
    {
        this.idListino = idListino;
    }

    public void setIdTavoloOccupato(Integer idTavoloOccupato)
    {
        this.idTavoloOccupato = idTavoloOccupato;
    }

    public void setNomeTavoloOccupato(String nomeTavoloOccupato)
    {
        this.nomeTavoloOccupato = nomeTavoloOccupato;
    }

    public void setNumPersone(Integer numPersone)
    {
        this.numPersone = numPersone;
    }

    public void setPagamentoComanda(PagamentoComandaDto pagamentoComanda)
    {
        this.pagamentoComanda = pagamentoComanda;
    }

    public void setPiatti(List<PiattoComandaDto> piatti)
    {
        this.piatti = piatti;
    }

    public void setTipoConto(String tipoConto)
    {
        this.tipoConto = tipoConto;
    }

    // public void setSconto(Double sconto) {
    // this.sconto = sconto;
    // }

    // public void setServizio(Double servizio) {
    // this.servizio = servizio;
    // }

    // public void setTipoDocumento(String tipoDocumento) {
    // this.tipoDocumento = tipoDocumento;
    // }

    // public void setTotale(Double totale) {
    // this.totale = totale;
    // }

}

