//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.01.03 alle 10:50:09 AM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass;

import java.io.Serializable;
import java.util.Date;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Classe Java per QuadraturaFTP_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="QuadraturaFTP_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdentificativoNodo" type="{http://www.fatturapa.it/sdi/ftp/v2.0}IdentificativoNodo_Type"/>
 *         &lt;element name="DataOraCreazione" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="NomeSupporto" type="{http://www.fatturapa.it/sdi/ftp/v2.0}NomeFile_Type"/>
 *         &lt;element name="NumeroFile" type="{http://www.fatturapa.it/sdi/ftp/v2.0}NumeroFile_Type"/>
 *       &lt;/sequence>
 *       &lt;attribute name="versione" use="required" type="{http://www.fatturapa.it/sdi/ftp/v2.0}Versione_Type" fixed="2.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuadraturaFTP_Type", propOrder = {
    "identificativoNodo",
    "dataOraCreazione",
    "nomeSupporto",
    "numeroFile"
})
public class QuadraturaFTPType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "IdentificativoNodo", required = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
    protected String identificativoNodo;
    @XmlElement(name = "DataOraCreazione", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dataOraCreazione;
    @XmlElement(name = "NomeSupporto", required = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
    protected String nomeSupporto;
    @XmlElement(name = "NumeroFile", required = true)
    protected NumeroFileType numeroFile;
    @XmlAttribute(name = "versione", required = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
    protected String versione;

    /**
     * Recupera il valore della proprietà identificativoNodo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoNodo() {
        return identificativoNodo;
    }

    /**
     * Imposta il valore della proprietà identificativoNodo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoNodo(String value) {
        this.identificativoNodo = value;
    }

    /**
     * Recupera il valore della proprietà dataOraCreazione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataOraCreazione() {
        return dataOraCreazione;
    }

    /**
     * Imposta il valore della proprietà dataOraCreazione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataOraCreazione(Date value) {
        this.dataOraCreazione = value;
    }

    /**
     * Recupera il valore della proprietà nomeSupporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeSupporto() {
        return nomeSupporto;
    }

    /**
     * Imposta il valore della proprietà nomeSupporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeSupporto(String value) {
        this.nomeSupporto = value;
    }

    /**
     * Recupera il valore della proprietà numeroFile.
     * 
     * @return
     *     possible object is
     *     {@link NumeroFileType }
     *     
     */
    public NumeroFileType getNumeroFile() {
        return numeroFile;
    }

    /**
     * Imposta il valore della proprietà numeroFile.
     * 
     * @param value
     *     allowed object is
     *     {@link NumeroFileType }
     *     
     */
    public void setNumeroFile(NumeroFileType value) {
        this.numeroFile = value;
    }

    /**
     * Recupera il valore della proprietà versione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersione() {
        if (versione == null) {
            return new Adapter3().unmarshal("2.0");
        } else {
            return versione;
        }
    }

    /**
     * Imposta il valore della proprietà versione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersione(String value) {
        this.versione = value;
    }

}

