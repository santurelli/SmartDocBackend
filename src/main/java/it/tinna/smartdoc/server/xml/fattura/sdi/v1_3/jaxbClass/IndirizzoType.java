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
 * <p>Classe Java per IndirizzoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="IndirizzoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Indirizzo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String60LatinType"/&gt;
 *         &lt;element name="NumeroCivico" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}NumeroCivicoType" minOccurs="0"/&gt;
 *         &lt;element name="CAP" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}CAPType"/&gt;
 *         &lt;element name="Comune" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String60LatinType"/&gt;
 *         &lt;element name="Provincia" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}ProvinciaType" minOccurs="0"/&gt;
 *         &lt;element name="Nazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}NazioneType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndirizzoType", propOrder = {
    "indirizzo",
    "numeroCivico",
    "cap",
    "comune",
    "provincia",
    "nazione"
})
public class IndirizzoType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Indirizzo", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String indirizzo;
    @XmlElement(name = "NumeroCivico")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String numeroCivico;
    @XmlElement(name = "CAP", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String cap;
    @XmlElement(name = "Comune", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String comune;
    @XmlElement(name = "Provincia")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String provincia;
    @XmlElement(name = "Nazione", required = true, defaultValue = "IT")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String nazione;

    /**
     * Recupera il valore della proprietà indirizzo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndirizzo() {
        return indirizzo;
    }

    /**
     * Imposta il valore della proprietà indirizzo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndirizzo(String value) {
        this.indirizzo = value;
    }

    /**
     * Recupera il valore della proprietà numeroCivico.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroCivico() {
        return numeroCivico;
    }

    /**
     * Imposta il valore della proprietà numeroCivico.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroCivico(String value) {
        this.numeroCivico = value;
    }

    /**
     * Recupera il valore della proprietà cap.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAP() {
        return cap;
    }

    /**
     * Imposta il valore della proprietà cap.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAP(String value) {
        this.cap = value;
    }

    /**
     * Recupera il valore della proprietà comune.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComune() {
        return comune;
    }

    /**
     * Imposta il valore della proprietà comune.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComune(String value) {
        this.comune = value;
    }

    /**
     * Recupera il valore della proprietà provincia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Imposta il valore della proprietà provincia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvincia(String value) {
        this.provincia = value;
    }

    /**
     * Recupera il valore della proprietà nazione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNazione() {
        return nazione;
    }

    /**
     * Imposta il valore della proprietà nazione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNazione(String value) {
        this.nazione = value;
    }

}

