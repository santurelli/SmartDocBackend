package it.tinna.smartdoc.shared.dto.primanota;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class PagamentoPrimaNotaDto extends BaseDto
{

    @Expose
    private String  descrizione;

    @Expose
    private String  dtPagamento;

    @Expose
    private Double  entrata;

    @Expose
    private Long    idDivisione;

    @Expose
    private Integer idProgetto;

    @Expose
    private Integer idRisorsa;

    @Expose
    private Integer idSoggetto;

    @Expose
    private Integer idTipoPagamento;

    @Expose
    private String  modalita;

    @Expose
    private String  riferimento;

    @Expose
    private String  tipoSoggetto;

    @Expose
    private Double  uscita;

    public String getDescrizione()
    {
        return descrizione;
    }

    public String getDtPagamento()
    {
        return dtPagamento;
    }

    public Double getEntrata()
    {
        return entrata;
    }

    public Long getIdDivisione()
    {
        return idDivisione;
    }

    public Integer getIdProgetto()
    {
        return idProgetto;
    }

    public Integer getIdRisorsa()
    {
        return idRisorsa;
    }

    public Integer getIdSoggetto()
    {
        return idSoggetto;
    }

    public Integer getIdTipoPagamento()
    {
        return idTipoPagamento;
    }

    public String getModalita()
    {
        return modalita;
    }

    public String getRiferimento()
    {
        return riferimento;
    }

    public String getTipoSoggetto()
    {
        return tipoSoggetto;
    }

    public Double getUscita()
    {
        return uscita;
    }

    public void setDescrizione(String descrizione)
    {
        this.descrizione = descrizione;
    }

    public void setDtPagamento(String dtPagamento)
    {
        this.dtPagamento = dtPagamento;
    }

    public void setEntrata(Double entrata)
    {
        this.entrata = entrata;
    }

    public void setIdDivisione(Long idDivisione)
    {
        this.idDivisione = idDivisione;
    }

    public void setIdProgetto(Integer idProgetto)
    {
        this.idProgetto = idProgetto;
    }

    public void setIdRisorsa(Integer idRisorsa)
    {
        this.idRisorsa = idRisorsa;
    }

    public void setIdSoggetto(Integer idSoggetto)
    {
        this.idSoggetto = idSoggetto;
    }

    public void setIdTipoPagamento(Integer idTipoPagamento)
    {
        this.idTipoPagamento = idTipoPagamento;
    }

    public void setModalita(String modalita)
    {
        this.modalita = modalita;
    }

    public void setRiferimento(String riferimento)
    {
        this.riferimento = riferimento;
    }

    public void setTipoSoggetto(String tipoSoggetto)
    {
        this.tipoSoggetto = tipoSoggetto;
    }

    public void setUscita(Double uscita)
    {
        this.uscita = uscita;
    }

}

