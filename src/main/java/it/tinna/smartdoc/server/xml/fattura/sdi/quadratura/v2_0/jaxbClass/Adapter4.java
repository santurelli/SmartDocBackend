//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.01.03 alle 10:50:09 AM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter4
    extends XmlAdapter<String, Integer>
{


    public Integer unmarshal(String value) {
        return (it.tinna.smartdoc.server.xml.sdi.SdiFormatter.parseInt(value));
    }

    public String marshal(Integer value) {
        return (it.tinna.smartdoc.server.xml.sdi.SdiFormatter.formatInt(value));
    }

}

