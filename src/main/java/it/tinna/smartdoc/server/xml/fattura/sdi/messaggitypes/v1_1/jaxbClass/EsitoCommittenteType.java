//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.01.25 alle 04:50:38 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per EsitoCommittente_Type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="EsitoCommittente_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EC01"/>
 *     &lt;enumeration value="EC02"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EsitoCommittente_Type", namespace = "http://www.fatturapa.gov.it/sdi/messaggi/v1.0")
@XmlEnum
public enum EsitoCommittenteType {


    /**
     * 
     * 						EC01 = ACCETTAZIONE
     * 					
     * 
     */
    @XmlEnumValue("EC01")
    EC_01("EC01"),

    /**
     * 
     * 						EC02 = RIFIUTO
     * 					
     * 
     */
    @XmlEnumValue("EC02")
    EC_02("EC02");
    private final String value;

    EsitoCommittenteType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EsitoCommittenteType fromValue(String v) {
        for (EsitoCommittenteType c: EsitoCommittenteType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}

