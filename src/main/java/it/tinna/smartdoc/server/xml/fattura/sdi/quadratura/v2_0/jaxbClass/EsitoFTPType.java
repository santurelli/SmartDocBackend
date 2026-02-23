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
import it.tinna.smartdoc.server.constants.EsitoTrasferimentoFtpEnum;


/**
 * <p>Classe Java per EsitoFTP_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="EsitoFTP_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdentificativoNodo" type="{http://www.fatturapa.it/sdi/ftp/v2.0}IdentificativoNodo_Type"/>
 *         &lt;element name="DataOraRicezione" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="DataOraEsito" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="NomeSupporto" type="{http://www.fatturapa.it/sdi/ftp/v2.0}NomeFile_Type"/>
 *         &lt;element name="Esito" type="{http://www.fatturapa.it/sdi/ftp/v2.0}EsitoTrasferimentoFTP_Type"/>
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
@XmlType(name = "EsitoFTP_Type", propOrder = {
    "identificativoNodo",
    "dataOraRicezione",
    "dataOraEsito",
    "nomeSupporto",
    "esito"
})
public class EsitoFTPType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "IdentificativoNodo", required = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
    protected String identificativoNodo;
    @XmlElement(name = "DataOraRicezione", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dataOraRicezione;
    @XmlElement(name = "DataOraEsito", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dataOraEsito;
    @XmlElement(name = "NomeSupporto", required = true)
    @XmlJavaTypeAdapter(Adapter3 .class)
    protected String nomeSupporto;
    @XmlElement(name = "Esito", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter6 .class)
    protected EsitoTrasferimentoFtpEnum esito;
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
     * Recupera il valore della proprietà dataOraRicezione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataOraRicezione() {
        return dataOraRicezione;
    }

    /**
     * Imposta il valore della proprietà dataOraRicezione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataOraRicezione(Date value) {
        this.dataOraRicezione = value;
    }

    /**
     * Recupera il valore della proprietà dataOraEsito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataOraEsito() {
        return dataOraEsito;
    }

    /**
     * Imposta il valore della proprietà dataOraEsito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataOraEsito(Date value) {
        this.dataOraEsito = value;
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
     * Recupera il valore della proprietà esito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public EsitoTrasferimentoFtpEnum getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della proprietà esito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsito(EsitoTrasferimentoFtpEnum value) {
        this.esito = value;
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

