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
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;


/**
 * <p>Classe Java per DatiGeneraliDocumentoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiGeneraliDocumentoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}TipoDocumentoType"/&gt;
 *         &lt;element name="Divisa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DivisaType"/&gt;
 *         &lt;element name="Data" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DataFatturaType"/&gt;
 *         &lt;element name="Numero" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String20Type"/&gt;
 *         &lt;element name="DatiRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiRitenutaType" minOccurs="0"/&gt;
 *         &lt;element name="DatiBollo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiBolloType" minOccurs="0"/&gt;
 *         &lt;element name="DatiCassaPrevidenziale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DatiCassaPrevidenzialeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ScontoMaggiorazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}ScontoMaggiorazioneType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ImportoTotaleDocumento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="Arrotondamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="Causale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}String200LatinType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Art73" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Art73Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiGeneraliDocumentoType", propOrder = {
    "tipoDocumento",
    "divisa",
    "data",
    "numero",
    "datiRitenuta",
    "datiBollo",
    "datiCassaPrevidenziale",
    "scontoMaggiorazione",
    "importoTotaleDocumento",
    "arrotondamento",
    "causale",
    "art73"
})
public class DatiGeneraliDocumentoType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TipoDocumento", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter10 .class)
    protected TipoDocumentoEnum tipoDocumento;
    @XmlElement(name = "Divisa", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String divisa;
    @XmlElement(name = "Data", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date data;
    @XmlElement(name = "Numero", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String numero;
    @XmlElement(name = "DatiRitenuta")
    protected DatiRitenutaType datiRitenuta;
    @XmlElement(name = "DatiBollo")
    protected DatiBolloType datiBollo;
    @XmlElement(name = "DatiCassaPrevidenziale")
    protected List<DatiCassaPrevidenzialeType> datiCassaPrevidenziale;
    @XmlElement(name = "ScontoMaggiorazione")
    protected List<ScontoMaggiorazioneType> scontoMaggiorazione;
    @XmlElement(name = "ImportoTotaleDocumento", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double importoTotaleDocumento;
    @XmlElement(name = "Arrotondamento", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double arrotondamento;
    @XmlElement(name = "Causale")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected List<String> causale;
    @XmlElement(name = "Art73", type = String.class)
    @XmlJavaTypeAdapter(Adapter8 .class)
    protected Boolean art73;

    /**
     * Recupera il valore della proprietà tipoDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoDocumentoEnum getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Imposta il valore della proprietà tipoDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumento(TipoDocumentoEnum value) {
        this.tipoDocumento = value;
    }

    /**
     * Recupera il valore della proprietà divisa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDivisa() {
        return divisa;
    }

    /**
     * Imposta il valore della proprietà divisa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDivisa(String value) {
        this.divisa = value;
    }

    /**
     * Recupera il valore della proprietà data.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getData() {
        return data;
    }

    /**
     * Imposta il valore della proprietà data.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData(Date value) {
        this.data = value;
    }

    /**
     * Recupera il valore della proprietà numero.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Imposta il valore della proprietà numero.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumero(String value) {
        this.numero = value;
    }

    /**
     * Recupera il valore della proprietà datiRitenuta.
     * 
     * @return
     *     possible object is
     *     {@link DatiRitenutaType }
     *     
     */
    public DatiRitenutaType getDatiRitenuta() {
        return datiRitenuta;
    }

    /**
     * Imposta il valore della proprietà datiRitenuta.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiRitenutaType }
     *     
     */
    public void setDatiRitenuta(DatiRitenutaType value) {
        this.datiRitenuta = value;
    }

    /**
     * Recupera il valore della proprietà datiBollo.
     * 
     * @return
     *     possible object is
     *     {@link DatiBolloType }
     *     
     */
    public DatiBolloType getDatiBollo() {
        return datiBollo;
    }

    /**
     * Imposta il valore della proprietà datiBollo.
     * 
     * @param value
     *     allowed object is
     *     {@link DatiBolloType }
     *     
     */
    public void setDatiBollo(DatiBolloType value) {
        this.datiBollo = value;
    }

    /**
     * Gets the value of the datiCassaPrevidenziale property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiCassaPrevidenziale property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiCassaPrevidenziale().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatiCassaPrevidenzialeType }
     * 
     * 
     */
    public List<DatiCassaPrevidenzialeType> getDatiCassaPrevidenziale() {
        if (datiCassaPrevidenziale == null) {
            datiCassaPrevidenziale = new ArrayList<DatiCassaPrevidenzialeType>();
        }
        return this.datiCassaPrevidenziale;
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
     * Recupera il valore della proprietà importoTotaleDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImportoTotaleDocumento() {
        return importoTotaleDocumento;
    }

    /**
     * Imposta il valore della proprietà importoTotaleDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportoTotaleDocumento(Double value) {
        this.importoTotaleDocumento = value;
    }

    /**
     * Recupera il valore della proprietà arrotondamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getArrotondamento() {
        return arrotondamento;
    }

    /**
     * Imposta il valore della proprietà arrotondamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrotondamento(Double value) {
        this.arrotondamento = value;
    }

    /**
     * Gets the value of the causale property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the causale property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCausale().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCausale() {
        if (causale == null) {
            causale = new ArrayList<String>();
        }
        return this.causale;
    }

    /**
     * Recupera il valore della proprietà art73.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Boolean isArt73() {
        return art73;
    }

    /**
     * Imposta il valore della proprietà art73.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArt73(Boolean value) {
        this.art73 = value;
    }

}

