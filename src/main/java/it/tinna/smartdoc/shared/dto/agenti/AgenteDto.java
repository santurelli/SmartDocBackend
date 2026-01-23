package it.tinna.smartdoc.shared.dto.agenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class AgenteDto extends BaseDto
{

    @Expose
    private String  denominazione;

    @Expose
    private String  indirizzo;

    @Expose
    private String  cap;

    @Expose
    private String  citta;

    @Expose
    private String  provincia;

    @Expose
    private String  nazione;

    @Expose
    private String  telefono;

    @Expose
    private String  fax;

    @Expose
    private String  cellulare;

    @Expose
    private String  email;

    @Expose
    private Double  percProvvigione;

    private Integer idZonaCompetenza;

    @Expose
    private String  tipoMaturazioneProvvigione;

    public enum MaturazioneProvvigione
    {
     VENDUTO("V"),
     PAGATO("P");

        private String maturazioneProvvigione;

        MaturazioneProvvigione(String maturazioneProvvigione)
        {
            this.maturazioneProvvigione = maturazioneProvvigione;
        }

        public String getValore()
        {
            return this.maturazioneProvvigione;
        }
    }

    public String getDenominazione()
    {
        return denominazione;
    }

    public void setDenominazione(String denominazione)
    {
        this.denominazione = denominazione;
    }

    public String getIndirizzo()
    {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo)
    {
        this.indirizzo = indirizzo;
    }

    public String getCap()
    {
        return cap;
    }

    public void setCap(String cap)
    {
        this.cap = cap;
    }

    public String getCitta()
    {
        return citta;
    }

    public void setCitta(String citta)
    {
        this.citta = citta;
    }

    public String getProvincia()
    {
        return provincia;
    }

    public void setProvincia(String provincia)
    {
        this.provincia = provincia;
    }

    public String getNazione()
    {
        return nazione;
    }

    public void setNazione(String nazione)
    {
        this.nazione = nazione;
    }

    public String getTelefono()
    {
        return telefono;
    }

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    public String getFax()
    {
        return fax;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public String getCellulare()
    {
        return cellulare;
    }

    public void setCellulare(String cellulare)
    {
        this.cellulare = cellulare;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Double getPercProvvigione()
    {
        return percProvvigione;
    }

    public void setPercProvvigione(Double percProvvigione)
    {
        this.percProvvigione = percProvvigione;
    }

    public Integer getIdZonaCompetenza()
    {
        return idZonaCompetenza;
    }

    public void setIdZonaCompetenza(Integer idZonaCompetenza)
    {
        this.idZonaCompetenza = idZonaCompetenza;
    }

    public String getTipoMaturazioneProvvigione()
    {
        return tipoMaturazioneProvvigione;
    }

    public void setTipoMaturazioneProvvigione(String tipoMaturazioneProvvigione)
    {
        this.tipoMaturazioneProvvigione = tipoMaturazioneProvvigione;
    }

}
