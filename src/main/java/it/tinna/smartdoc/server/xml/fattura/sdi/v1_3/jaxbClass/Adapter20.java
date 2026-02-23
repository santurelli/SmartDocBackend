//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.02 alle 04:43:41 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_3.jaxbClass;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter20
    extends XmlAdapter<String, Boolean>
{


    public Boolean unmarshal(String value) {
        return (it.tinna.smartdoc.server.xml.sdi.SdiFormatter.parseBooleanLiquidazione(value));
    }

    public String marshal(Boolean value) {
        return (it.tinna.smartdoc.server.xml.sdi.SdiFormatter.formatBooleanLiquidazione(value));
    }

}

