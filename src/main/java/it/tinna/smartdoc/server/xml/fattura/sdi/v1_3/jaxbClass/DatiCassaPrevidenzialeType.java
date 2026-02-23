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
import it.tinna.smartdoc.server.constants.NaturaEsenzioneEnum;
import it.tinna.smartdoc.server.constants.TipoCassaEnum;


/**
 * <p>Classe Java per DatiCassaPrevidenzialeType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiCassaPrevidenzialeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoCassa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}TipoCassaType"/&gt;
 *         &lt;element name="AlCassa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}RateType"/&gt;
 *         &lt;element name="ImportoContributoCassa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType"/&gt;
 *         &lt;element name="ImponibileCassa" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="AliquotaIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}RateType"/&gt;
 *         &lt;element name="Ritenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}RitenutaType" minOccurs="0"/&gt;
 *         &lt;element name="Natura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}NaturaType" minOccurs="0"/&gt;
 *         &lt;element name="RiferimentoAmministrazione" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String20Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiCassaPrevidenzialeType", propOrder = {
    "tipoCassa",
    "alCassa",
    "importoContributoCassa",
    "imponibileCassa",
    "aliquotaIVA",
    "ritenuta",
    "natura",
    "riferimentoAmministrazione"
})
public class DatiCassaPrevidenzialeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TipoCassa", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter9 .class)
    protected TipoCassaEnum tipoCassa;
    @XmlElement(name = "AlCassa", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double alCassa;
    @XmlElement(name = "ImportoContributoCassa", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double importoContributoCassa;
    @XmlElement(name = "ImponibileCassa", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double imponibileCassa;
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

    /**
     * Recupera il valore della proprietà tipoCassa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoCassaEnum getTipoCassa() {
        return tipoCassa;
    }

    /**
     * Imposta il valore della proprietà tipoCassa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoCassa(TipoCassaEnum value) {
        this.tipoCassa = value;
    }

    /**
     * Recupera il valore della proprietà alCassa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getAlCassa() {
        return alCassa;
    }

    /**
     * Imposta il valore della proprietà alCassa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlCassa(Double value) {
        this.alCassa = value;
    }

    /**
     * Recupera il valore della proprietà importoContributoCassa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImportoContributoCassa() {
        return importoContributoCassa;
    }

    /**
     * Imposta il valore della proprietà importoContributoCassa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportoContributoCassa(Double value) {
        this.importoContributoCassa = value;
    }

    /**
     * Recupera il valore della proprietà imponibileCassa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImponibileCassa() {
        return imponibileCassa;
    }

    /**
     * Imposta il valore della proprietà imponibileCassa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImponibileCassa(Double value) {
        this.imponibileCassa = value;
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

}

