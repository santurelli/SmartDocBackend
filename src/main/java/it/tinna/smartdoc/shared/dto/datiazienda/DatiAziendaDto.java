package it.tinna.smartdoc.shared.dto.datiazienda;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

@SuppressWarnings("serial")
public class DatiAziendaDto extends BaseDto
{

    private byte[]  byteLogo;

    @Expose
    private String  cap;

    @Expose
    private String  citta;

    @Expose
    private String  codiceFiscale;

    private Boolean deleteLogo;

    @Expose
    private String  denominazione;

    @Expose
    private String  email;

    @Expose
    private String  fax;

    @Expose
    private String  indirizzo;

    private String  indirizzoServerMail;

    private String  indirizzoServerPec;

    @Expose
    private String  logo;

    @Expose
    private String  logoType;

    @Expose
    private String  nazione;

    @Expose
    private String  partitaIva;

    private String  passwordServerMail;

    private String  passwordServerPec;

    private byte[]  passwordServerMailCriptata;

    private byte[]  passwordServerPecCriptata;

    @Expose
    private String  pec;

    private String  portaServerMail;

    private String  portaServerPec;

    @Expose
    private String  provincia;

    private boolean serverProprietario;

    @Expose
    private String  sitoWeb;

    private boolean sslMail;

    private boolean sslPec;

    @Expose
    private String  telefono;

    private boolean tslMail;

    private boolean tslPec;

    private String  usernameServerMail;

    private String  usernameServerPec;

    @Expose
    private Integer idRegimeFiscale;

    private String  descrRegimeFiscale;

    private String  valoreRegimeFiscale;

    @Expose
    private String  settoreMerceologico;

    public byte[] getByteLogo()
    {
        return byteLogo;
    }

    public String getCap()
    {
        return cap;
    }

    public String getCitta()
    {
        return citta;
    }

    public String getCodiceFiscale()
    {
        return codiceFiscale;
    }

    public Boolean getDeleteLogo()
    {
        return deleteLogo;
    }

    public String getDenominazione()
    {
        return denominazione;
    }

    public String getDescrRegimeFiscale()
    {
        return descrRegimeFiscale;
    }

    public String getEmail()
    {
        return email;
    }

    public String getFax()
    {
        return fax;
    }

    public Integer getIdRegimeFiscale()
    {
        return idRegimeFiscale;
    }

    public String getIndirizzo()
    {
        return indirizzo;
    }

    public String getIndirizzoServerMail()
    {
        return indirizzoServerMail;
    }

    public String getIndirizzoServerPec()
    {
        return indirizzoServerPec;
    }

    public String getLogo()
    {
        return logo;
    }

    public String getLogoType()
    {
        return logoType;
    }

    public String getNazione()
    {
        return nazione;
    }

    public String getPartitaIva()
    {
        return partitaIva;
    }

    public String getPasswordServerMail()
    {
        return passwordServerMail;
    }

    public byte[] getPasswordServerMailCriptata()
    {
        return passwordServerMailCriptata;
    }

    public String getPasswordServerPec()
    {
        return passwordServerPec;
    }

    public byte[] getPasswordServerPecCriptata()
    {
        return passwordServerPecCriptata;
    }

    public String getPec()
    {
        return pec;
    }

    public String getPortaServerMail()
    {
        return portaServerMail;
    }

    public String getPortaServerPec()
    {
        return portaServerPec;
    }

    public String getProvincia()
    {
        return provincia;
    }

    public String getSitoWeb()
    {
        return sitoWeb;
    }

    public String getTelefono()
    {
        return telefono;
    }

    public String getUsernameServerMail()
    {
        return usernameServerMail;
    }

    public String getUsernameServerPec()
    {
        return usernameServerPec;
    }

    public String getValoreRegimeFiscale()
    {
        return valoreRegimeFiscale;
    }

    public String getSettoreMerceologico()
    {
        return settoreMerceologico;
    }

    public void setSettoreMerceologico(String settoreMerceologico)
    {
        this.settoreMerceologico = settoreMerceologico;
    }

    public boolean isServerProprietario()
    {
        return serverProprietario;
    }

    public boolean isSslMail()
    {
        return sslMail;
    }

    public boolean isSslPec()
    {
        return sslPec;
    }

    public boolean isTslMail()
    {
        return tslMail;
    }

    public boolean isTslPec()
    {
        return tslPec;
    }

    public void setByteLogo(byte[] byteLogo)
    {
        this.byteLogo = byteLogo;
    }

    public void setCap(String cap)
    {
        this.cap = cap;
    }

    public void setCitta(String citta)
    {
        this.citta = citta;
    }

    public void setCodiceFiscale(String codiceFiscale)
    {
        this.codiceFiscale = codiceFiscale;
    }

    public void setDeleteLogo(Boolean deleteLogo)
    {
        this.deleteLogo = deleteLogo;
    }

    public void setDenominazione(String denominazione)
    {
        this.denominazione = denominazione;
    }

    public void setDescrRegimeFiscale(String descrRegimeFiscale)
    {
        this.descrRegimeFiscale = descrRegimeFiscale;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public void setIdRegimeFiscale(Integer idRegimeFiscale)
    {
        this.idRegimeFiscale = idRegimeFiscale;
    }

    public void setIndirizzo(String indirizzo)
    {
        this.indirizzo = indirizzo;
    }

    public void setIndirizzoServerMail(String indirizzoServerMail)
    {
        this.indirizzoServerMail = indirizzoServerMail;
    }

    public void setIndirizzoServerPec(String indirizzoServerPec)
    {
        this.indirizzoServerPec = indirizzoServerPec;
    }

    public void setLogo(String logo)
    {
        this.logo = logo;
    }

    public void setLogoType(String logoType)
    {
        this.logoType = logoType;
    }

    public void setNazione(String nazione)
    {
        this.nazione = nazione;
    }

    public void setPartitaIva(String partitaIva)
    {
        this.partitaIva = partitaIva;
    }

    public void setPasswordServerMail(String passwordServerMail)
    {
        this.passwordServerMail = passwordServerMail;
    }

    public void setPasswordServerMailCriptata(byte[] passwordServerMailCriptata)
    {
        this.passwordServerMailCriptata = passwordServerMailCriptata;
    }

    public void setPasswordServerPec(String passwordServerPec)
    {
        this.passwordServerPec = passwordServerPec;
    }

    public void setPasswordServerPecCriptata(byte[] passwordServerPecCriptata)
    {
        this.passwordServerPecCriptata = passwordServerPecCriptata;
    }

    public void setPec(String pec)
    {
        this.pec = pec;
    }

    public void setPortaServerMail(String portaServerMail)
    {
        this.portaServerMail = portaServerMail;
    }

    public void setPortaServerPec(String portaServerPec)
    {
        this.portaServerPec = portaServerPec;
    }

    public void setProvincia(String provincia)
    {
        this.provincia = provincia;
    }

    public void setServerProprietario(boolean serverProprietario)
    {
        this.serverProprietario = serverProprietario;
    }

    public void setSitoWeb(String sitoWeb)
    {
        this.sitoWeb = sitoWeb;
    }

    public void setSslMail(boolean sslMail)
    {
        this.sslMail = sslMail;
    }

    public void setSslPec(boolean sslPec)
    {
        this.sslPec = sslPec;
    }

    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    public void setTslMail(boolean tslMail)
    {
        this.tslMail = tslMail;
    }

    public void setTslPec(boolean tslPec)
    {
        this.tslPec = tslPec;
    }

    public void setUsernameServerMail(String usernameServerMail)
    {
        this.usernameServerMail = usernameServerMail;
    }

    public void setUsernameServerPec(String usernameServerPec)
    {
        this.usernameServerPec = usernameServerPec;
    }

    public void setValoreRegimeFiscale(String valoreRegimeFiscale)
    {
        this.valoreRegimeFiscale = valoreRegimeFiscale;
    }

}

