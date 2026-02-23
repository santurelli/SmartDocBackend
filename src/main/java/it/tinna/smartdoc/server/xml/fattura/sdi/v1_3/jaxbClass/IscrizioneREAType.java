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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per IscrizioneREAType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="IscrizioneREAType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Ufficio" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}ProvinciaType"/&gt;
 *         &lt;element name="NumeroREA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String20Type"/&gt;
 *         &lt;element name="CapitaleSociale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="SocioUnico" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}SocioUnicoType" minOccurs="0"/&gt;
 *         &lt;element name="StatoLiquidazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}StatoLiquidazioneType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IscrizioneREAType", propOrder = {
    "ufficio",
    "numeroREA",
    "capitaleSociale",
    "socioUnico",
    "statoLiquidazione"
})
public class IscrizioneREAType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Ufficio", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String ufficio;
    @XmlElement(name = "NumeroREA", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String numeroREA;
    @XmlElement(name = "CapitaleSociale", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double capitaleSociale;
    @XmlElement(name = "SocioUnico", type = String.class)
    @XmlJavaTypeAdapter(Adapter19 .class)
    protected Boolean socioUnico;
    @XmlElement(name = "StatoLiquidazione", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter20 .class)
    protected Boolean statoLiquidazione;

    /**
     * Recupera il valore della proprietà ufficio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUfficio() {
        return ufficio;
    }

    /**
     * Imposta il valore della proprietà ufficio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUfficio(String value) {
        this.ufficio = value;
    }

    /**
     * Recupera il valore della proprietà numeroREA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroREA() {
        return numeroREA;
    }

    /**
     * Imposta il valore della proprietà numeroREA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroREA(String value) {
        this.numeroREA = value;
    }

    /**
     * Recupera il valore della proprietà capitaleSociale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getCapitaleSociale() {
        return capitaleSociale;
    }

    /**
     * Imposta il valore della proprietà capitaleSociale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCapitaleSociale(Double value) {
        this.capitaleSociale = value;
    }

    /**
     * Recupera il valore della proprietà socioUnico.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isSocioUnico() {
        return socioUnico;
    }

    /**
     * Imposta il valore della proprietà socioUnico.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSocioUnico(Boolean value) {
        this.socioUnico = value;
    }

    /**
     * Recupera il valore della proprietà statoLiquidazione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isStatoLiquidazione() {
        return statoLiquidazione;
    }

    /**
     * Imposta il valore della proprietà statoLiquidazione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatoLiquidazione(Boolean value) {
        this.statoLiquidazione = value;
    }

}

