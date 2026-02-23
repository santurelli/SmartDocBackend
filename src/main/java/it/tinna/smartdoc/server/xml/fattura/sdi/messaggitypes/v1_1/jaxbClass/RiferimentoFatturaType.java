//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.01.25 alle 04:50:38 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per RiferimentoFattura_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RiferimentoFattura_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NumeroFattura" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}NumeroFattura_Type"/>
 *         &lt;element name="AnnoFattura" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}AnnoFattura_Type"/>
 *         &lt;element name="PosizioneFattura" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RiferimentoFattura_Type", namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", propOrder = {
    "numeroFattura",
    "annoFattura",
    "posizioneFattura"
})
public class RiferimentoFatturaType {

    @XmlElement(name = "NumeroFattura", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String numeroFattura;
    @XmlElement(name = "AnnoFattura", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger annoFattura;
    @XmlElement(name = "PosizioneFattura")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger posizioneFattura;

    /**
     * Recupera il valore della proprietà numeroFattura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroFattura() {
        return numeroFattura;
    }

    /**
     * Imposta il valore della proprietà numeroFattura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroFattura(String value) {
        this.numeroFattura = value;
    }

    /**
     * Recupera il valore della proprietà annoFattura.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAnnoFattura() {
        return annoFattura;
    }

    /**
     * Imposta il valore della proprietà annoFattura.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAnnoFattura(BigInteger value) {
        this.annoFattura = value;
    }

    /**
     * Recupera il valore della proprietà posizioneFattura.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPosizioneFattura() {
        return posizioneFattura;
    }

    /**
     * Imposta il valore della proprietà posizioneFattura.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPosizioneFattura(BigInteger value) {
        this.posizioneFattura = value;
    }

}

