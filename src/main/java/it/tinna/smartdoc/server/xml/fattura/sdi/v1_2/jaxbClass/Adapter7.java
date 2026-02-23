//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.05 alle 12:23:20 PM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import it.tinna.smartdoc.server.constants.TipoScontoDocumentoEnum;

public class Adapter7
    extends XmlAdapter<String, TipoScontoDocumentoEnum>
{


    public TipoScontoDocumentoEnum unmarshal(String value) {
        return (it.tinna.smartdoc.server.xml.sdi.SdiFormatter.parseTipoSconto(value));
    }

    public String marshal(TipoScontoDocumentoEnum value) {
        return (it.tinna.smartdoc.server.xml.sdi.SdiFormatter.formatTipoSconto(value));
    }

}

