//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.02 alle 04:43:41 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_3.jaxbClass;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per AltriDatiGestionaliType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AltriDatiGestionaliType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoDato" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String10Type"/&gt;
 *         &lt;element name="RiferimentoTesto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String60LatinType" minOccurs="0"/&gt;
 *         &lt;element name="RiferimentoNumero" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount8DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="RiferimentoData" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AltriDatiGestionaliType", propOrder = {
    "tipoDato",
    "riferimentoTesto",
    "riferimentoNumero",
    "riferimentoData"
})
public class AltriDatiGestionaliType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TipoDato", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String tipoDato;
    @XmlElement(name = "RiferimentoTesto")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String riferimentoTesto;
    @XmlElement(name = "RiferimentoNumero", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double riferimentoNumero;
    @XmlElement(name = "RiferimentoData", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date riferimentoData;

    /**
     * Recupera il valore della proprietà tipoDato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDato() {
        return tipoDato;
    }

    /**
     * Imposta il valore della proprietà tipoDato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDato(String value) {
        this.tipoDato = value;
    }

    /**
     * Recupera il valore della proprietà riferimentoTesto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiferimentoTesto() {
        return riferimentoTesto;
    }

    /**
     * Imposta il valore della proprietà riferimentoTesto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoTesto(String value) {
        this.riferimentoTesto = value;
    }

    /**
     * Recupera il valore della proprietà riferimentoNumero.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getRiferimentoNumero() {
        return riferimentoNumero;
    }

    /**
     * Imposta il valore della proprietà riferimentoNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoNumero(Double value) {
        this.riferimentoNumero = value;
    }

    /**
     * Recupera il valore della proprietà riferimentoData.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getRiferimentoData() {
        return riferimentoData;
    }

    /**
     * Imposta il valore della proprietà riferimentoData.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoData(Date value) {
        this.riferimentoData = value;
    }

}

