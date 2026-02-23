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
import it.tinna.smartdoc.server.constants.CausalePagamentoEnum;
import it.tinna.smartdoc.server.constants.TipoRitenutaEnum;


/**
 * <p>Classe Java per DatiRitenutaType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiRitenutaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}TipoRitenutaType"/&gt;
 *         &lt;element name="ImportoRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}Amount2DecimalType"/&gt;
 *         &lt;element name="AliquotaRitenuta" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}RateType"/&gt;
 *         &lt;element name="CausalePagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}CausalePagamentoType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiRitenutaType", propOrder = {
    "tipoRitenuta",
    "importoRitenuta",
    "aliquotaRitenuta",
    "causalePagamento"
})
public class DatiRitenutaType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "TipoRitenuta", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter11 .class)
    protected TipoRitenutaEnum tipoRitenuta;
    @XmlElement(name = "ImportoRitenuta", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double importoRitenuta;
    @XmlElement(name = "AliquotaRitenuta", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double aliquotaRitenuta;
    @XmlElement(name = "CausalePagamento", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter6 .class)
    protected CausalePagamentoEnum causalePagamento;

    /**
     * Recupera il valore della proprietà tipoRitenuta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoRitenutaEnum getTipoRitenuta() {
        return tipoRitenuta;
    }

    /**
     * Imposta il valore della proprietà tipoRitenuta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoRitenuta(TipoRitenutaEnum value) {
        this.tipoRitenuta = value;
    }

    /**
     * Recupera il valore della proprietà importoRitenuta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImportoRitenuta() {
        return importoRitenuta;
    }

    /**
     * Imposta il valore della proprietà importoRitenuta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportoRitenuta(Double value) {
        this.importoRitenuta = value;
    }

    /**
     * Recupera il valore della proprietà aliquotaRitenuta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getAliquotaRitenuta() {
        return aliquotaRitenuta;
    }

    /**
     * Imposta il valore della proprietà aliquotaRitenuta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAliquotaRitenuta(Double value) {
        this.aliquotaRitenuta = value;
    }

    /**
     * Recupera il valore della proprietà causalePagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public CausalePagamentoEnum getCausalePagamento() {
        return causalePagamento;
    }

    /**
     * Imposta il valore della proprietà causalePagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCausalePagamento(CausalePagamentoEnum value) {
        this.causalePagamento = value;
    }

}

