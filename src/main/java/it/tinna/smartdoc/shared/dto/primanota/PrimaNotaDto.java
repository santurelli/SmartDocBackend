package it.tinna.smartdoc.shared.dto.primanota;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;

@SuppressWarnings("serial")
public class PrimaNotaDto extends BaseDto
{

    public class SoggettoPrimaNotaDto
    {
        @Expose
        private String codiceFiscale;

        @Expose
        private String id;

        @Expose
        private String denominazione;

        @Expose
        private String partitaIva;

        public SoggettoPrimaNotaDto()
        {
        }

        public String getCodiceFiscale()
        {
            return codiceFiscale;
        }

        public String getDenominazione()
        {
            return denominazione;
        }

        public String getId()
        {
            return id;
        }

        public String getPartitaIva()
        {
            return partitaIva;
        }

        public void setCodiceFiscale(String codiceFiscale)
        {
            this.codiceFiscale = codiceFiscale;
        }

        public void setDenominazione(String denominazione)
        {
            this.denominazione = denominazione;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public void setPartitaIva(String partitaIva)
        {
            this.partitaIva = partitaIva;
        }

    }

    @Expose
    private String               descrDocumento;

    @Expose
    private String               dtDocumento;

    @Expose
    private String               descrTipoPagamento;

    @Expose
    private BigDecimal           entrate;             // campo che vale totale oppure null; è usato
                                                      // per il report excel

    /**
     * Contiene l'id del documento a cui si riferisce il pagamento
     */
    @Expose
    private long                 idDocumento;

    @Expose
    private Long                 idDivisione;

    @Expose
    private Long                 idProgetto;

    @Expose
    private Integer              idRisorsa;

    @Expose
    private String               idSoggetto;

    @Expose
    private Integer              idTipoPagamento;

    @Expose
    private String               modalita;

    @Expose
    private SoggettoPrimaNotaDto objSoggetto;

    @Expose
    private ProgettoDto          progetto;

    @Expose
    private String               riferimentoPagamento;

    @Expose
    private String               risorsa;

    @Expose
    private Integer              saldato;

    private String               saldatoString;

    @Expose
    private String               soggetto;

    @Expose
    private String               tipoDocumento;

    @Expose
    private BigDecimal           totale;

    @Expose
    private String               type;

    @Expose
    private BigDecimal           uscite;              // campo che vale totale oppure null; è usato per
                                                      // il report excel

    public String getDescrDocumento()
    {
        return descrDocumento;
    }

    public String getDescrTipoPagamento()
    {
        return descrTipoPagamento;
    }

    public String getDtDocumento()
    {
        return dtDocumento;
    }

    public BigDecimal getEntrate()
    {
        return entrate;
    }

    public Long getIdDivisione()
    {
        return idDivisione;
    }

    public long getIdDocumento()
    {
        return idDocumento;
    }

    public Long getIdProgetto()
    {
        return idProgetto;
    }

    public Integer getIdRisorsa()
    {
        return idRisorsa;
    }

    public String getIdSoggetto()
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

    public SoggettoPrimaNotaDto getObjSoggetto()
    {
        return objSoggetto;
    }

    public ProgettoDto getProgetto()
    {
        return progetto;
    }

    public String getRiferimentoPagamento()
    {
        return riferimentoPagamento;
    }

    public String getRisorsa()
    {
        return risorsa;
    }

    public Integer getSaldato()
    {
        return saldato;
    }

    public String getSaldatoString()
    {
        return saldatoString;
    }

    public String getSoggetto()
    {
        return soggetto;
    }

    public String getTipoDocumento()
    {
        return tipoDocumento;
    }

    public BigDecimal getTotale()
    {
        return totale;
    }

    public String getType()
    {
        return type;
    }

    public BigDecimal getUscite()
    {
        return uscite;
    }

    public void setDescrDocumento(String descrDocumento)
    {
        this.descrDocumento = descrDocumento;
    }

    public void setDescrTipoPagamento(String descrTipoPagamento)
    {
        this.descrTipoPagamento = descrTipoPagamento;
    }

    public void setDtDocumento(String dtDocumento)
    {
        this.dtDocumento = dtDocumento;
    }

    public void setEntrate(BigDecimal entrate)
    {
        this.entrate = entrate;
    }

    public void setIdDivisione(Long idDivisione)
    {
        this.idDivisione = idDivisione;
    }

    public void setIdDocumento(long idDocumento)
    {
        this.idDocumento = idDocumento;
    }

    public void setIdProgetto(Long idProgetto)
    {
        this.idProgetto = idProgetto;
    }

    public void setIdRisorsa(Integer idRisorsa)
    {
        this.idRisorsa = idRisorsa;
    }

    public void setIdSoggetto(String idSoggetto)
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

    public void setObjSoggetto(SoggettoPrimaNotaDto objSoggetto)
    {
        this.objSoggetto = objSoggetto;
    }

    public void setProgetto(ProgettoDto progetto)
    {
        this.progetto = progetto;
    }

    public void setRiferimentoPagamento(String riferimentoPagamento)
    {
        this.riferimentoPagamento = riferimentoPagamento;
    }

    public void setRisorsa(String risorsa)
    {
        this.risorsa = risorsa;
    }

    public void setSaldato(Integer saldato)
    {
        this.saldato = saldato;
    }

    public void setSaldatoString(String saldatoString)
    {
        this.saldatoString = saldatoString;
    }

    public void setSoggetto(String soggetto)
    {
        this.soggetto = soggetto;
    }

    public void setTipoDocumento(String tipoDocumento)
    {
        this.tipoDocumento = tipoDocumento;
    }

    public void setTotale(BigDecimal totale)
    {
        this.totale = totale;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setUscite(BigDecimal uscite)
    {
        this.uscite = uscite;
    }

}

