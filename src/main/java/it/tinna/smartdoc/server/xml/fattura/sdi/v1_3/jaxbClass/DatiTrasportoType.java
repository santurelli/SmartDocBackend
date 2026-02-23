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
import it.tinna.smartdoc.server.constants.TipoResaEnum;


/**
 * <p>Classe Java per DatiTrasportoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiTrasportoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatiAnagraficiVettore" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}DatiAnagraficiVettoreType" minOccurs="0"/&gt;
 *         &lt;element name="MezzoTrasporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String80LatinType" minOccurs="0"/&gt;
 *         &lt;element name="CausaleTrasporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String100LatinType" minOccurs="0"/&gt;
 *         &lt;element name="NumeroColli" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}NumeroColliType" minOccurs="0"/&gt;
 *         &lt;element name="Descrizione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String100LatinType" minOccurs="0"/&gt;
 *         &lt;element name="UnitaMisuraPeso" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String10Type" minOccurs="0"/&gt;
 *         &lt;element name="PesoLordo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}PesoType" minOccurs="0"/&gt;
 *         &lt;element name="PesoNetto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}PesoType" minOccurs="0"/&gt;
 *         &lt;element name="DataOraRitiro" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="DataInizioTrasporto" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="TipoResa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}TipoResaType" minOccurs="0"/&gt;
 *         &lt;element name="IndirizzoResa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}IndirizzoType" minOccurs="0"/&gt;
 *         &lt;element name="DataOraConsegna" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiTrasportoType", propOrder = {
    "datiAnagraficiVettore",
    "mezzoTrasporto",
    "causaleTrasporto",
    "numeroColli",
    "descrizione",
    "unitaMisuraPeso",
    "pesoLordo",
    "pesoNetto",
    "dataOraRitiro",
    "dataInizioTrasporto",
    "tipoResa",
    "indirizzoResa",
    "dataOraConsegna"
})
public class DatiTrasportoType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "DatiAnagraficiVettore")
    protected DatiAnagraficiVettoreType datiAnagraficiVettore;
    @XmlElement(name = "MezzoTrasporto")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String mezzoTrasporto;
    @XmlElement(name = "CausaleTrasporto")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String causaleTrasporto;
    @XmlElement(name = "NumeroColli")
    @XmlSchemaType(name = "integer")
    protected Integer numeroColli;
    @XmlElement(name = "Descrizione")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String descrizione;
    @XmlElement(name = "UnitaMisuraPeso")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String unitaMisuraPeso;
    @XmlElement(name = "PesoLordo", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double pesoLordo;
    @XmlElement(name = "PesoNetto", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double pesoNetto;
    @XmlElement(name = "DataOraRitiro", type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dataOraRitiro;
    @XmlElement(name = "DataInizioTrasporto", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataInizioTrasporto;
    @XmlElement(name = "TipoResa", type = String.class)
    @XmlJavaTypeAdapter(Adapter22 .class)
    protected TipoResaEnum tipoResa;
    @XmlElement(name = "IndirizzoResa")
    protected IndirizzoType indirizzoResa;
    @XmlElement(name = "DataOraConsegna", type = String.class)
    @XmlJavaTypeAdapter(Adapter3 .class)
    @XmlSchemaType(name = "dateTime")
    protected Date dataOraConsegna;

    /**
     * Recupera il valore della proprietà datiAnagraficiVettore.
     * 
     * @return
     *     possible object is
     *     {@link DatiAnagraficiVettoreType }
     *     
     */
    public DatiAnagraficiVettoreType getDatiAnagraficiVettore() {
        return datiAnagraficiVettore;
    }

    /**
     * Imposta il valore della proprietà datiAnagraficiVettore.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiAnagraficiVettoreType }
     *     
     */
    public void setDatiAnagraficiVettore(DatiAnagraficiVettoreType value) {
        this.datiAnagraficiVettore = value;
    }

    /**
     * Recupera il valore della proprietà mezzoTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMezzoTrasporto() {
        return mezzoTrasporto;
    }

    /**
     * Imposta il valore della proprietà mezzoTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMezzoTrasporto(String value) {
        this.mezzoTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà causaleTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCausaleTrasporto() {
        return causaleTrasporto;
    }

    /**
     * Imposta il valore della proprietà causaleTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCausaleTrasporto(String value) {
        this.causaleTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà numeroColli.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumeroColli() {
        return numeroColli;
    }

    /**
     * Imposta il valore della proprietà numeroColli.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumeroColli(Integer value) {
        this.numeroColli = value;
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
     * Recupera il valore della proprietà unitaMisuraPeso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitaMisuraPeso() {
        return unitaMisuraPeso;
    }

    /**
     * Imposta il valore della proprietà unitaMisuraPeso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitaMisuraPeso(String value) {
        this.unitaMisuraPeso = value;
    }

    /**
     * Recupera il valore della proprietà pesoLordo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getPesoLordo() {
        return pesoLordo;
    }

    /**
     * Imposta il valore della proprietà pesoLordo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPesoLordo(Double value) {
        this.pesoLordo = value;
    }

    /**
     * Recupera il valore della proprietà pesoNetto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getPesoNetto() {
        return pesoNetto;
    }

    /**
     * Imposta il valore della proprietà pesoNetto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPesoNetto(Double value) {
        this.pesoNetto = value;
    }

    /**
     * Recupera il valore della proprietà dataOraRitiro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataOraRitiro() {
        return dataOraRitiro;
    }

    /**
     * Imposta il valore della proprietà dataOraRitiro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataOraRitiro(Date value) {
        this.dataOraRitiro = value;
    }

    /**
     * Recupera il valore della proprietà dataInizioTrasporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataInizioTrasporto() {
        return dataInizioTrasporto;
    }

    /**
     * Imposta il valore della proprietà dataInizioTrasporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataInizioTrasporto(Date value) {
        this.dataInizioTrasporto = value;
    }

    /**
     * Recupera il valore della proprietà tipoResa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoResaEnum getTipoResa() {
        return tipoResa;
    }

    /**
     * Imposta il valore della proprietà tipoResa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoResa(TipoResaEnum value) {
        this.tipoResa = value;
    }

    /**
     * Recupera il valore della proprietà indirizzoResa.
     * 
     * @return
     *     possible object is
     *     {@link IndirizzoType }
     *     
     */
    public IndirizzoType getIndirizzoResa() {
        return indirizzoResa;
    }

    /**
     * Imposta il valore della proprietà indirizzoResa.
     * 
     * @param value
     *     allowed object is
     *     {@link IndirizzoType }
     *     
     */
    public void setIndirizzoResa(IndirizzoType value) {
        this.indirizzoResa = value;
    }

    /**
     * Recupera il valore della proprietà dataOraConsegna.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataOraConsegna() {
        return dataOraConsegna;
    }

    /**
     * Imposta il valore della proprietà dataOraConsegna.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataOraConsegna(Date value) {
        this.dataOraConsegna = value;
    }

}

