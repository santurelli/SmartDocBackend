//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.02 alle 04:43:41 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_3.jaxbClass;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Blocco relativo ai dati del Cessionario / Committente
 * 
 * <p>Classe Java per CessionarioCommittenteType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="CessionarioCommittenteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatiAnagrafici" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}DatiAnagraficiCessionarioType"/&gt;
 *         &lt;element name="Sede" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}IndirizzoType"/&gt;
 *         &lt;element name="StabileOrganizzazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}IndirizzoType" minOccurs="0"/&gt;
 *         &lt;element name="RappresentanteFiscale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}RappresentanteFiscaleCessionarioType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CessionarioCommittenteType", propOrder = {
    "datiAnagrafici",
    "sede",
    "stabileOrganizzazione",
    "rappresentanteFiscale"
})
public class CessionarioCommittenteType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "DatiAnagrafici", required = true)
    protected DatiAnagraficiCessionarioType datiAnagrafici;
    @XmlElement(name = "Sede", required = true)
    protected IndirizzoType sede;
    @XmlElement(name = "StabileOrganizzazione")
    protected IndirizzoType stabileOrganizzazione;
    @XmlElement(name = "RappresentanteFiscale")
    protected RappresentanteFiscaleCessionarioType rappresentanteFiscale;

    /**
     * Recupera il valore della proprietà datiAnagrafici.
     * 
     * @return
     *     possible object is
     *     {@link DatiAnagraficiCessionarioType }
     *     
     */
    public DatiAnagraficiCessionarioType getDatiAnagrafici() {
        return datiAnagrafici;
    }

    /**
     * Imposta il valore della proprietà datiAnagrafici.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiAnagraficiCessionarioType }
     *     
     */
    public void setDatiAnagrafici(DatiAnagraficiCessionarioType value) {
        this.datiAnagrafici = value;
    }

    /**
     * Recupera il valore della proprietà sede.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoType }
     *     
     */
    public IndirizzoType getSede() {
        return sede;
    }

    /**
     * Imposta il valore della proprietà sede.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoType }
     *     
     */
    public void setSede(IndirizzoType value) {
        this.sede = value;
    }

    /**
     * Recupera il valore della proprietà stabileOrganizzazione.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoType }
     *     
     */
    public IndirizzoType getStabileOrganizzazione() {
        return stabileOrganizzazione;
    }

    /**
     * Imposta il valore della proprietà stabileOrganizzazione.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoType }
     *     
     */
    public void setStabileOrganizzazione(IndirizzoType value) {
        this.stabileOrganizzazione = value;
    }

    /**
     * Recupera il valore della proprietà rappresentanteFiscale.
     * 
     * @return
     *     possible object is
     *     {@link RappresentanteFiscaleCessionarioType }
     *     
     */
    public RappresentanteFiscaleCessionarioType getRappresentanteFiscale() {
        return rappresentanteFiscale;
    }

    /**
     * Imposta il valore della proprietà rappresentanteFiscale.
     * 
     * @param value
     *     allowed object is
     *     {@link RappresentanteFiscaleCessionarioType }
     *     
     */
    public void setRappresentanteFiscale(RappresentanteFiscaleCessionarioType value) {
        this.rappresentanteFiscale = value;
    }

}

