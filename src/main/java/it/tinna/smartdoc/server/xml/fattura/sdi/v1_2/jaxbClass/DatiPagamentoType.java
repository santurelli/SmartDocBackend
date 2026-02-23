//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.05 alle 12:23:20 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import it.tinna.smartdoc.server.constants.TipoPagamentoEnum;


/**
 * 
 * 				Blocco relativo ai dati di Pagamento della Fattura	Elettronica
 * 			
 * 
 * <p>Classe Java per DatiPagamentoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DatiPagamentoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CondizioniPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}CondizioniPagamentoType"/&gt;
 *         &lt;element name="DettaglioPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2}DettaglioPagamentoType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatiPagamentoType", propOrder = {
    "condizioniPagamento",
    "dettaglioPagamento"
})
public class DatiPagamentoType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "CondizioniPagamento", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter14 .class)
    protected TipoPagamentoEnum condizioniPagamento;
    @XmlElement(name = "DettaglioPagamento", required = true)
    protected List<DettaglioPagamentoType> dettaglioPagamento;

    /**
     * Recupera il valore della proprietà condizioniPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public TipoPagamentoEnum getCondizioniPagamento() {
        return condizioniPagamento;
    }

    /**
     * Imposta il valore della proprietà condizioniPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondizioniPagamento(TipoPagamentoEnum value) {
        this.condizioniPagamento = value;
    }

    /**
     * Gets the value of the dettaglioPagamento property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dettaglioPagamento property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDettaglioPagamento().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DettaglioPagamentoType }
     * 
     * 
     */
    public List<DettaglioPagamentoType> getDettaglioPagamento() {
        if (dettaglioPagamento == null) {
            dettaglioPagamento = new ArrayList<DettaglioPagamentoType>();
        }
        return this.dettaglioPagamento;
    }

}

