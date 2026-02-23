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
import it.tinna.smartdoc.server.constants.ModalitaPagamentoEnum;


/**
 * <p>Classe Java per DettaglioPagamentoType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="DettaglioPagamentoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Beneficiario" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String200LatinType" minOccurs="0"/&gt;
 *         &lt;element name="ModalitaPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}ModalitaPagamentoType"/&gt;
 *         &lt;element name="DataRiferimentoTerminiPagamento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="GiorniTerminiPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}GiorniTerminePagamentoType" minOccurs="0"/&gt;
 *         &lt;element name="DataScadenzaPagamento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="ImportoPagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType"/&gt;
 *         &lt;element name="CodUfficioPostale" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String20Type" minOccurs="0"/&gt;
 *         &lt;element name="CognomeQuietanzante" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String60LatinType" minOccurs="0"/&gt;
 *         &lt;element name="NomeQuietanzante" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String60LatinType" minOccurs="0"/&gt;
 *         &lt;element name="CFQuietanzante" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}CodiceFiscalePFType" minOccurs="0"/&gt;
 *         &lt;element name="TitoloQuietanzante" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}TitoloType" minOccurs="0"/&gt;
 *         &lt;element name="IstitutoFinanziario" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String80LatinType" minOccurs="0"/&gt;
 *         &lt;element name="IBAN" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}IBANType" minOccurs="0"/&gt;
 *         &lt;element name="ABI" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}ABIType" minOccurs="0"/&gt;
 *         &lt;element name="CAB" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}CABType" minOccurs="0"/&gt;
 *         &lt;element name="BIC" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}BICType" minOccurs="0"/&gt;
 *         &lt;element name="ScontoPagamentoAnticipato" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="DataLimitePagamentoAnticipato" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="PenalitaPagamentiRitardati" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}Amount2DecimalType" minOccurs="0"/&gt;
 *         &lt;element name="DataDecorrenzaPenale" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="CodicePagamento" type="{http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.3}String60Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DettaglioPagamentoType", propOrder = {
    "beneficiario",
    "modalitaPagamento",
    "dataRiferimentoTerminiPagamento",
    "giorniTerminiPagamento",
    "dataScadenzaPagamento",
    "importoPagamento",
    "codUfficioPostale",
    "cognomeQuietanzante",
    "nomeQuietanzante",
    "cfQuietanzante",
    "titoloQuietanzante",
    "istitutoFinanziario",
    "iban",
    "abi",
    "cab",
    "bic",
    "scontoPagamentoAnticipato",
    "dataLimitePagamentoAnticipato",
    "penalitaPagamentiRitardati",
    "dataDecorrenzaPenale",
    "codicePagamento"
})
public class DettaglioPagamentoType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "Beneficiario")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String beneficiario;
    @XmlElement(name = "ModalitaPagamento", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter15 .class)
    protected ModalitaPagamentoEnum modalitaPagamento;
    @XmlElement(name = "DataRiferimentoTerminiPagamento", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataRiferimentoTerminiPagamento;
    @XmlElement(name = "GiorniTerminiPagamento")
    @XmlSchemaType(name = "integer")
    protected Integer giorniTerminiPagamento;
    @XmlElement(name = "DataScadenzaPagamento", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataScadenzaPagamento;
    @XmlElement(name = "ImportoPagamento", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double importoPagamento;
    @XmlElement(name = "CodUfficioPostale")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String codUfficioPostale;
    @XmlElement(name = "CognomeQuietanzante")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String cognomeQuietanzante;
    @XmlElement(name = "NomeQuietanzante")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String nomeQuietanzante;
    @XmlElement(name = "CFQuietanzante")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String cfQuietanzante;
    @XmlElement(name = "TitoloQuietanzante")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String titoloQuietanzante;
    @XmlElement(name = "IstitutoFinanziario")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String istitutoFinanziario;
    @XmlElement(name = "IBAN")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String iban;
    @XmlElement(name = "ABI")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String abi;
    @XmlElement(name = "CAB")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String cab;
    @XmlElement(name = "BIC")
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String bic;
    @XmlElement(name = "ScontoPagamentoAnticipato", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double scontoPagamentoAnticipato;
    @XmlElement(name = "DataLimitePagamentoAnticipato", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataLimitePagamentoAnticipato;
    @XmlElement(name = "PenalitaPagamentiRitardati", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "decimal")
    protected Double penalitaPagamentiRitardati;
    @XmlElement(name = "DataDecorrenzaPenale", type = String.class)
    @XmlJavaTypeAdapter(Adapter4 .class)
    @XmlSchemaType(name = "date")
    protected Date dataDecorrenzaPenale;
    @XmlElement(name = "CodicePagamento")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String codicePagamento;

    /**
     * Recupera il valore della proprietà beneficiario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBeneficiario() {
        return beneficiario;
    }

    /**
     * Imposta il valore della proprietà beneficiario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBeneficiario(String value) {
        this.beneficiario = value;
    }

    /**
     * Recupera il valore della proprietà modalitaPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public ModalitaPagamentoEnum getModalitaPagamento() {
        return modalitaPagamento;
    }

    /**
     * Imposta il valore della proprietà modalitaPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModalitaPagamento(ModalitaPagamentoEnum value) {
        this.modalitaPagamento = value;
    }

    /**
     * Recupera il valore della proprietà dataRiferimentoTerminiPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataRiferimentoTerminiPagamento() {
        return dataRiferimentoTerminiPagamento;
    }

    /**
     * Imposta il valore della proprietà dataRiferimentoTerminiPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataRiferimentoTerminiPagamento(Date value) {
        this.dataRiferimentoTerminiPagamento = value;
    }

    /**
     * Recupera il valore della proprietà giorniTerminiPagamento.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGiorniTerminiPagamento() {
        return giorniTerminiPagamento;
    }

    /**
     * Imposta il valore della proprietà giorniTerminiPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGiorniTerminiPagamento(Integer value) {
        this.giorniTerminiPagamento = value;
    }

    /**
     * Recupera il valore della proprietà dataScadenzaPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataScadenzaPagamento() {
        return dataScadenzaPagamento;
    }

    /**
     * Imposta il valore della proprietà dataScadenzaPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataScadenzaPagamento(Date value) {
        this.dataScadenzaPagamento = value;
    }

    /**
     * Recupera il valore della proprietà importoPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getImportoPagamento() {
        return importoPagamento;
    }

    /**
     * Imposta il valore della proprietà importoPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportoPagamento(Double value) {
        this.importoPagamento = value;
    }

    /**
     * Recupera il valore della proprietà codUfficioPostale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodUfficioPostale() {
        return codUfficioPostale;
    }

    /**
     * Imposta il valore della proprietà codUfficioPostale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodUfficioPostale(String value) {
        this.codUfficioPostale = value;
    }

    /**
     * Recupera il valore della proprietà cognomeQuietanzante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCognomeQuietanzante() {
        return cognomeQuietanzante;
    }

    /**
     * Imposta il valore della proprietà cognomeQuietanzante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCognomeQuietanzante(String value) {
        this.cognomeQuietanzante = value;
    }

    /**
     * Recupera il valore della proprietà nomeQuietanzante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeQuietanzante() {
        return nomeQuietanzante;
    }

    /**
     * Imposta il valore della proprietà nomeQuietanzante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeQuietanzante(String value) {
        this.nomeQuietanzante = value;
    }

    /**
     * Recupera il valore della proprietà cfQuietanzante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCFQuietanzante() {
        return cfQuietanzante;
    }

    /**
     * Imposta il valore della proprietà cfQuietanzante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCFQuietanzante(String value) {
        this.cfQuietanzante = value;
    }

    /**
     * Recupera il valore della proprietà titoloQuietanzante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitoloQuietanzante() {
        return titoloQuietanzante;
    }

    /**
     * Imposta il valore della proprietà titoloQuietanzante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitoloQuietanzante(String value) {
        this.titoloQuietanzante = value;
    }

    /**
     * Recupera il valore della proprietà istitutoFinanziario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIstitutoFinanziario() {
        return istitutoFinanziario;
    }

    /**
     * Imposta il valore della proprietà istitutoFinanziario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIstitutoFinanziario(String value) {
        this.istitutoFinanziario = value;
    }

    /**
     * Recupera il valore della proprietà iban.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Imposta il valore della proprietà iban.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

    /**
     * Recupera il valore della proprietà abi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getABI() {
        return abi;
    }

    /**
     * Imposta il valore della proprietà abi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setABI(String value) {
        this.abi = value;
    }

    /**
     * Recupera il valore della proprietà cab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAB() {
        return cab;
    }

    /**
     * Imposta il valore della proprietà cab.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAB(String value) {
        this.cab = value;
    }

    /**
     * Recupera il valore della proprietà bic.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBIC() {
        return bic;
    }

    /**
     * Imposta il valore della proprietà bic.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBIC(String value) {
        this.bic = value;
    }

    /**
     * Recupera il valore della proprietà scontoPagamentoAnticipato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getScontoPagamentoAnticipato() {
        return scontoPagamentoAnticipato;
    }

    /**
     * Imposta il valore della proprietà scontoPagamentoAnticipato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScontoPagamentoAnticipato(Double value) {
        this.scontoPagamentoAnticipato = value;
    }

    /**
     * Recupera il valore della proprietà dataLimitePagamentoAnticipato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataLimitePagamentoAnticipato() {
        return dataLimitePagamentoAnticipato;
    }

    /**
     * Imposta il valore della proprietà dataLimitePagamentoAnticipato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataLimitePagamentoAnticipato(Date value) {
        this.dataLimitePagamentoAnticipato = value;
    }

    /**
     * Recupera il valore della proprietà penalitaPagamentiRitardati.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Double getPenalitaPagamentiRitardati() {
        return penalitaPagamentiRitardati;
    }

    /**
     * Imposta il valore della proprietà penalitaPagamentiRitardati.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPenalitaPagamentiRitardati(Double value) {
        this.penalitaPagamentiRitardati = value;
    }

    /**
     * Recupera il valore della proprietà dataDecorrenzaPenale.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDataDecorrenzaPenale() {
        return dataDecorrenzaPenale;
    }

    /**
     * Imposta il valore della proprietà dataDecorrenzaPenale.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataDecorrenzaPenale(Date value) {
        this.dataDecorrenzaPenale = value;
    }

    /**
     * Recupera il valore della proprietà codicePagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodicePagamento() {
        return codicePagamento;
    }

    /**
     * Imposta il valore della proprietà codicePagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodicePagamento(String value) {
        this.codicePagamento = value;
    }

}

