package it.tinna.smartdoc.batch.util;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureFactory;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignUtils
{

    public static XMLObject xadesProperty(String xadesNS,
                                          String IDSig,
                                          String IDSP,
                                          String dsNS,
                                          XMLSignatureFactory sigFactory,
                                          Document doc,
                                          X509Certificate cert) throws Exception
    {
        Element QElement = doc.createElementNS(xadesNS, "xades:QualifyingProperties");
        QElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xades", xadesNS);
        QElement.setAttributeNS(null, "Target", "#" + IDSig);
        Element SElement = doc.createElementNS(xadesNS, "xades:SignedProperties");
        SElement.setAttributeNS(null, "Id", IDSP);
        SElement.setIdAttribute("Id", true);
        QElement.appendChild(SElement);
        Element SignedSignaturePropertiesElement = doc.createElementNS(xadesNS, "xades:SignedSignatureProperties");
        SElement.appendChild(SignedSignaturePropertiesElement);
        Element SigningTimeElement = doc.createElementNS(xadesNS, "xades:SigningTime");
        // String data = "2016-06-28T16:47:50Z";
        String data = DateFormatUtils.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss");
        SigningTimeElement.setTextContent(data);
        SignedSignaturePropertiesElement.appendChild(SigningTimeElement);
        // Element SigningCertificateElement = doc.createElementNS(xadesNS, "xades:SigningCertificate");
        // SignedSignaturePropertiesElement.appendChild(SigningCertificateElement);
        // Element CertElement = doc.createElementNS(xadesNS, "xades:Cert");
        // SigningCertificateElement.appendChild(CertElement);
        // Element CertDigestElement = doc.createElementNS(xadesNS, "xades:CertDigest");
        // CertElement.appendChild(CertDigestElement);
        // Element DigestMethodElement = doc.createElementNS(dsNS, "ds:DigestMethod");
        // DigestMethodElement.setAttributeNS(null, "Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
        // CertDigestElement.appendChild(DigestMethodElement);
        // Element DigestValueElement = doc.createElementNS(dsNS, "ds:DigestValue");
        // String hash = DigestUtils.sha1Hex(cert.getEncoded());
        // DigestValueElement.setTextContent(hash);
        // CertDigestElement.appendChild(DigestValueElement);
        // Element IssuerSerialElement = doc.createElementNS(xadesNS, "xades:IssuerSerial");
        // CertElement.appendChild(IssuerSerialElement);
        // Element X509IssuerNameElement = doc.createElementNS(dsNS, "ds:X509IssuerName");
        // X509IssuerNameElement.setTextContent(cert.getIssuerDN().getName());
        // IssuerSerialElement.appendChild(X509IssuerNameElement);
        // Element X509SerialNumberElement = doc.createElementNS(dsNS, "ds:X509SerialNumber");
        // X509SerialNumberElement.setTextContent(cert.getSerialNumber().toString());
        // IssuerSerialElement.appendChild(X509SerialNumberElement);
        DOMStructure qualifPropStruct = new DOMStructure(QElement);
        List<DOMStructure> xmlObj = new ArrayList<>();
        xmlObj.add(qualifPropStruct);
        XMLObject object = sigFactory.newXMLObject(xmlObj, null, null, null);
        return object;
    }

}

