//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.01.03 alle 10:50:09 AM CET 
//


package it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FileQuadraturaFTP_QNAME = new QName("http://www.fatturapa.it/sdi/ftp/v2.0", "FileQuadraturaFTP");
    private final static QName _FileEsitoFTP_QNAME = new QName("http://www.fatturapa.it/sdi/ftp/v2.0", "FileEsitoFTP");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EsitoFTPType }
     * 
     */
    public EsitoFTPType createEsitoFTPType() {
        return new EsitoFTPType();
    }

    /**
     * Create an instance of {@link QuadraturaFTPType }
     * 
     */
    public QuadraturaFTPType createQuadraturaFTPType() {
        return new QuadraturaFTPType();
    }

    /**
     * Create an instance of {@link NumeroFileType }
     * 
     */
    public NumeroFileType createNumeroFileType() {
        return new NumeroFileType();
    }

    /**
     * Create an instance of {@link TipoFileType }
     * 
     */
    public TipoFileType createTipoFileType() {
        return new TipoFileType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QuadraturaFTPType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.it/sdi/ftp/v2.0", name = "FileQuadraturaFTP")
    public JAXBElement<QuadraturaFTPType> createFileQuadraturaFTP(QuadraturaFTPType value) {
        return new JAXBElement<QuadraturaFTPType>(_FileQuadraturaFTP_QNAME, QuadraturaFTPType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EsitoFTPType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.fatturapa.it/sdi/ftp/v2.0", name = "FileEsitoFTP")
    public JAXBElement<EsitoFTPType> createFileEsitoFTP(EsitoFTPType value) {
        return new JAXBElement<EsitoFTPType>(_FileEsitoFTP_QNAME, EsitoFTPType.class, null, value);
    }

}

