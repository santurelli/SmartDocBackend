//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.05 alle 12:23:20 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import it.tinna.smartdoc.server.constants.NaturaEsenzioneEnum;
import it.tinna.smartdoc.server.constants.TipoCessazionePrestazioneEnum;


/**
 * <p>Classe Java per DettaglioLineeType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DettaglioLineeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumeroLinea" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}NumeroLineaType"/&gt;
 *         &lt;element name="TipoCessionePrestazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}TipoCessionePrestazioneType" minOccurs="0"/&gt;
 *         &lt;element name="CodiceArticolo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}CodiceArticoloType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Descrizione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String1000LatinType"/&gt;
 *         &lt;element name="Quantita" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}QuantitaType" minOccurs="0"/&gt;
 *         &lt;element name="UnitaMisura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String10Type" minOccurs="0"/&gt;
 *         &lt;element name="DataInizioPeriodo" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="DataFinePeriodo" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="PrezzoUnitario" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount8DecimalType"/&gt;
 *         &lt;element name="ScontoMaggiorazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}ScontoMaggiorazioneType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PrezzoTotale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount8DecimalType"/&gt;
 *         &lt;element name="AliquotaIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}RateType"/&gt;
 *         &lt;element name="Ritenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}RitenutaType" minOccurs="0"/&gt;
 *         &lt;element name="Natura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}NaturaType" minOccurs="0"/&gt;
 *         &lt;element name="RiferimentoAmministrazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String20Type" minOccurs="0"/&gt;
 *         &lt;element name="AltriDatiGestionali" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}AltriDatiGestionaliType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DettaglioLineeType", propOrder = {
    "numeroLinea",
    "tipoCessionePrestazione",
    "codiceArticolo",
    "descrizione",
    "quantita",
    "unitaMisura",
    "dataInizioPeriodo",
    "dataFinePeriodo",
    "prezzoUnitario",
    "scontoMaggiorazione",
    "prezzoTotale",
    "aliquotaIVA",
    "ritenuta",
    "natura",
    "riferimentoAmministrazione",
    "altriDatiGestionali"
})
public class DettaglioLineeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "NumeroLinea")
    @XmlSchemaType(name = "integer")
    protected int numeroLinea;
    @XmlElement(name = "TipoCessionePrestazione", type = String.class)
    @XmlJavaTypeAdapter(Adapter21 .class)
    protected TipoCessazionePrestazioneEnum tipoCessionePrestazione;
    @XmlElement(name = "CodiceArticolo")
    protected List<CodiceArticoloType> codiceArticolo;
    @XmlElement(name = "Descrizione", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String descrizione;
    @XmlElement(name = "Quantita", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double quantita;
    @XmlElement(name = "UnitaMisura")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String unitaMisura;
    @XmlElement(name = "DataInizioPeriodo", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataInizioPeriodo;
    @XmlElement(name = "DataFinePeriodo", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataFinePeriodo;
    @XmlElement(name = "PrezzoUnitario", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double prezzoUnitario;
    @XmlElement(name = "ScontoMaggiorazione")
    protected List<ScontoMaggiorazioneType> scontoMaggiorazione;
    @XmlElement(name = "PrezzoTotale", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double prezzoTotale;
    @XmlElement(name = "AliquotaIVA", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double aliquotaIVA;
    @XmlElement(name = "Ritenuta", type = String.class)
    @XmlJavaTypeAdapter(Adapter16 .class)
    protected Boolean ritenuta;
    @XmlElement(name = "Natura", type = String.class)
    @XmlJavaTypeAdapter(Adapter18 .class)
    protected NaturaEsenzioneEnum natura;
    @XmlElement(name = "RiferimentoAmministrazione")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String riferimentoAmministrazione;
    @XmlElement(name = "AltriDatiGestionali")
    protected List<AltriDatiGestionaliType> altriDatiGestionali;

    /**
     * Recupera il valore della proprietà numeroLinea.
     * 
     */
    public int getNumeroLinea() {
        return numeroLinea;
    }

    /**
     * Imposta il valore della proprietà numeroLinea.
     * 
     */
    public void setNumeroLinea(int value) {
        this.numeroLinea = value;
    }

    /**
     * Recupera il valore della proprietà tipoCessionePrestazione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoCessazionePrestazioneEnum getTipoCessionePrestazione() {
        return tipoCessionePrestazione;
    }

    /**
     * Imposta il valore della proprietà tipoCessionePrestazione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoCessionePrestazione(TipoCessazionePrestazioneEnum value) {
        this.tipoCessionePrestazione = value;
    }

    /**
     * Gets the value of the codiceArticolo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codiceArticolo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCodiceArticolo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CodiceArticoloType }
     * 
     * 
     */
    public List<CodiceArticoloType> getCodiceArticolo() {
        if (codiceArticolo == null) {
            codiceArticolo = new ArrayList<CodiceArticoloType>();
        }
        return this.codiceArticolo;
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
     * Recupera il valore della proprietà quantita.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getQuantita() {
        return quantita;
    }

    /**
     * Imposta il valore della proprietà quantita.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantita(Double value) {
        this.quantita = value;
    }

    /**
     * Recupera il valore della proprietà unitaMisura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitaMisura() {
        return unitaMisura;
    }

    /**
     * Imposta il valore della proprietà unitaMisura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitaMisura(String value) {
        this.unitaMisura = value;
    }

    /**
     * Recupera il valore della proprietà dataInizioPeriodo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataInizioPeriodo() {
        return dataInizioPeriodo;
    }

    /**
     * Imposta il valore della proprietà dataInizioPeriodo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataInizioPeriodo(Date value) {
        this.dataInizioPeriodo = value;
    }

    /**
     * Recupera il valore della proprietà dataFinePeriodo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataFinePeriodo() {
        return dataFinePeriodo;
    }

    /**
     * Imposta il valore della proprietà dataFinePeriodo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataFinePeriodo(Date value) {
        this.dataFinePeriodo = value;
    }

    /**
     * Recupera il valore della proprietà prezzoUnitario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    /**
     * Imposta il valore della proprietà prezzoUnitario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrezzoUnitario(Double value) {
        this.prezzoUnitario = value;
    }

    /**
     * Gets the value of the scontoMaggiorazione property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the scontoMaggiorazione property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getScontoMaggiorazione().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScontoMaggiorazioneType }
     * 
     * 
     */
    public List<ScontoMaggiorazioneType> getScontoMaggiorazione() {
        if (scontoMaggiorazione == null) {
            scontoMaggiorazione = new ArrayList<ScontoMaggiorazioneType>();
        }
        return this.scontoMaggiorazione;
    }

    /**
     * Recupera il valore della proprietà prezzoTotale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getPrezzoTotale() {
        return prezzoTotale;
    }

    /**
     * Imposta il valore della proprietà prezzoTotale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrezzoTotale(Double value) {
        this.prezzoTotale = value;
    }

    /**
     * Recupera il valore della proprietà aliquotaIVA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getAliquotaIVA() {
        return aliquotaIVA;
    }

    /**
     * Imposta il valore della proprietà aliquotaIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAliquotaIVA(Double value) {
        this.aliquotaIVA = value;
    }

    /**
     * Recupera il valore della proprietà ritenuta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isRitenuta() {
        return ritenuta;
    }

    /**
     * Imposta il valore della proprietà ritenuta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRitenuta(Boolean value) {
        this.ritenuta = value;
    }

    /**
     * Recupera il valore della proprietà natura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public NaturaEsenzioneEnum getNatura() {
        return natura;
    }

    /**
     * Imposta il valore della proprietà natura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNatura(NaturaEsenzioneEnum value) {
        this.natura = value;
    }

    /**
     * Recupera il valore della proprietà riferimentoAmministrazione.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiferimentoAmministrazione() {
        return riferimentoAmministrazione;
    }

    /**
     * Imposta il valore della proprietà riferimentoAmministrazione.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoAmministrazione(String value) {
        this.riferimentoAmministrazione = value;
    }

    /**
     * Gets the value of the altriDatiGestionali property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the altriDatiGestionali property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAltriDatiGestionali().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AltriDatiGestionaliType }
     * 
     * 
     */
    public List<AltriDatiGestionaliType> getAltriDatiGestionali() {
        if (altriDatiGestionali == null) {
            altriDatiGestionali = new ArrayList<AltriDatiGestionaliType>();
        }
        return this.altriDatiGestionali;
    }

}

