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
import it.tinna.smartdoc.server.constants.EsigibilitaIvaEnum;
import it.tinna.smartdoc.server.constants.NaturaEsenzioneEnum;


/**
 * <p>Classe Java per DatiRiepilogoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiRiepilogoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AliquotaIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}RateType"/&gt;
 *         &lt;element name="Natura" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}NaturaType" minOccurs="0"/&gt;
 *         &lt;element name="SpeseAccessorie" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="Arrotondamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount8DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="ImponibileImporto" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType"/&gt;
 *         &lt;element name="Imposta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType"/&gt;
 *         &lt;element name="EsigibilitaIVA" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}EsigibilitaIVAType" minOccurs="0"/&gt;
 *         &lt;element name="RiferimentoNormativo" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String100LatinType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiRiepilogoType", propOrder = {
    "aliquotaIVA",
    "natura",
    "speseAccessorie",
    "arrotondamento",
    "imponibileImporto",
    "imposta",
    "esigibilitaIVA",
    "riferimentoNormativo"
})
public class DatiRiepilogoType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "AliquotaIVA", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double aliquotaIVA;
    @XmlElement(name = "Natura", type = String.class)
    @XmlJavaTypeAdapter(Adapter18 .class)
    protected NaturaEsenzioneEnum natura;
    @XmlElement(name = "SpeseAccessorie", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double speseAccessorie;
    @XmlElement(name = "Arrotondamento", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double arrotondamento;
    @XmlElement(name = "ImponibileImporto", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double imponibileImporto;
    @XmlElement(name = "Imposta", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double imposta;
    @XmlElement(name = "EsigibilitaIVA", type = String.class)
    @XmlJavaTypeAdapter(Adapter17 .class)
    protected EsigibilitaIvaEnum esigibilitaIVA;
    @XmlElement(name = "RiferimentoNormativo")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String riferimentoNormativo;

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
     * Recupera il valore della proprietà speseAccessorie.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getSpeseAccessorie() {
        return speseAccessorie;
    }

    /**
     * Imposta il valore della proprietà speseAccessorie.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpeseAccessorie(Double value) {
        this.speseAccessorie = value;
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
     * Recupera il valore della proprietà imponibileImporto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImponibileImporto() {
        return imponibileImporto;
    }

    /**
     * Imposta il valore della proprietà imponibileImporto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImponibileImporto(Double value) {
        this.imponibileImporto = value;
    }

    /**
     * Recupera il valore della proprietà imposta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImposta() {
        return imposta;
    }

    /**
     * Imposta il valore della proprietà imposta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImposta(Double value) {
        this.imposta = value;
    }

    /**
     * Recupera il valore della proprietà esigibilitaIVA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public EsigibilitaIvaEnum getEsigibilitaIVA() {
        return esigibilitaIVA;
    }

    /**
     * Imposta il valore della proprietà esigibilitaIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsigibilitaIVA(EsigibilitaIvaEnum value) {
        this.esigibilitaIVA = value;
    }

    /**
     * Recupera il valore della proprietà riferimentoNormativo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiferimentoNormativo() {
        return riferimentoNormativo;
    }

    /**
     * Imposta il valore della proprietà riferimentoNormativo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoNormativo(String value) {
        this.riferimentoNormativo = value;
    }

}

