//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.05 alle 12:23:20 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per DatiBolloType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiBolloType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BolloVirtuale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}BolloVirtualeType"/&gt;
 *         &lt;element name="ImportoBollo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount2DecimalType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiBolloType", propOrder = {
    "bolloVirtuale",
    "importoBollo"
})
public class DatiBolloType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "BolloVirtuale", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter23 .class)
    protected Boolean bolloVirtuale;
    @XmlElement(name = "ImportoBollo", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double importoBollo;

    /**
     * Recupera il valore della proprietà bolloVirtuale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isBolloVirtuale() {
        return bolloVirtuale;
    }

    /**
     * Imposta il valore della proprietà bolloVirtuale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBolloVirtuale(Boolean value) {
        this.bolloVirtuale = value;
    }

    /**
     * Recupera il valore della proprietà importoBollo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImportoBollo() {
        return importoBollo;
    }

    /**
     * Imposta il valore della proprietà importoBollo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportoBollo(Double value) {
        this.importoBollo = value;
    }

}

