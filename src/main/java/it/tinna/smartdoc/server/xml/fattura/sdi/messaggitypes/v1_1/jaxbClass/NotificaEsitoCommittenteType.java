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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per NotificaEsitoCommittente_Type complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="NotificaEsitoCommittente_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IdentificativoSdI" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}IdentificativoSdI_Type"/>
 *         &lt;element name="RiferimentoFattura" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}RiferimentoFattura_Type" minOccurs="0"/>
 *         &lt;element name="Esito" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}EsitoCommittente_Type"/>
 *         &lt;element name="Descrizione" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}Descrizione_Type" minOccurs="0"/>
 *         &lt;element name="MessageIdCommittente" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}MessageId_Type" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="versione" use="required" type="{http://www.fatturapa.gov.it/sdi/messaggi/v1.0}Versione_Type" fixed="1.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NotificaEsitoCommittente_Type", namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0", propOrder = {
    "identificativoSdI",
    "riferimentoFattura",
    "esito",
    "descrizione",
    "messageIdCommittente",
    "signature"
})
public class NotificaEsitoCommittenteType {

    @XmlElement(name = "IdentificativoSdI", required = true)
    protected BigInteger identificativoSdI;
    @XmlElement(name = "RiferimentoFattura")
    protected RiferimentoFatturaType riferimentoFattura;
    @XmlElement(name = "Esito", required = true)
    @XmlSchemaType(name = "string")
    protected EsitoCommittenteType esito;
    @XmlElement(name = "Descrizione")
    protected String descrizione;
    @XmlElement(name = "MessageIdCommittente")
    protected String messageIdCommittente;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "versione", required = true)
    protected String versione;

    /**
     * Recupera il valore della proprietà identificativoSdI.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIdentificativoSdI() {
        return identificativoSdI;
    }

    /**
     * Imposta il valore della proprietà identificativoSdI.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIdentificativoSdI(BigInteger value) {
        this.identificativoSdI = value;
    }

    /**
     * Recupera il valore della proprietà riferimentoFattura.
     * 
     * @return
     *     possible object is
     *     {@link RiferimentoFatturaType }
     *     
     */
    public RiferimentoFatturaType getRiferimentoFattura() {
        return riferimentoFattura;
    }

    /**
     * Imposta il valore della proprietà riferimentoFattura.
     * 
     * @param value
     *     allowed object is
     *     {@link RiferimentoFatturaType }
     *     
     */
    public void setRiferimentoFattura(RiferimentoFatturaType value) {
        this.riferimentoFattura = value;
    }

    /**
     * Recupera il valore della proprietà esito.
     * 
     * @return
     *     possible object is
     *     {@link EsitoCommittenteType }
     *     
     */
    public EsitoCommittenteType getEsito() {
        return esito;
    }

    /**
     * Imposta il valore della proprietà esito.
     * 
     * @param value
     *     allowed object is
     *     {@link EsitoCommittenteType }
     *     
     */
    public void setEsito(EsitoCommittenteType value) {
        this.esito = value;
    }

    /**
     * Recupera il valore della proprietà descrizione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta il valore della proprietà descrizione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescrizione(String value) {
        this.descrizione = value;
    }

    /**
     * Recupera il valore della proprietà messageIdCommittente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageIdCommittente() {
        return messageIdCommittente;
    }

    /**
     * Imposta il valore della proprietà messageIdCommittente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageIdCommittente(String value) {
        this.messageIdCommittente = value;
    }

    /**
     * Recupera il valore della proprietà signature.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Imposta il valore della proprietà signature.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
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
            return "1.0";
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

