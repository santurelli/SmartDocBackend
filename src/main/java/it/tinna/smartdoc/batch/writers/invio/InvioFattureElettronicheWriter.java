package it.tinna.smartdoc.batch.writers.invio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.batch.dto.DatiFatturaInviataSdiDto;
import it.tinna.smartdoc.batch.util.SignUtils;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import lombok.Setter;

public class InvioFattureElettronicheWriter implements ItemStreamWriter<FatturaElettronicaWrapperDto>, StepExecutionListener
{

    private static final String PRIVATE_KEY_ALIAS = "alias_firma";

    private static final String PRIVATE_KEY_PASS  = "$toCazz0";

    private static final String KEY_STORE_TYPE    = "pkcs12";

    private static final String KEY_STORE_PASS    = "$toCazz0";

    private static KeyStore loadKeyStore(File privateKeyFile) throws Exception
    {
        final InputStream fileInputStream = new FileInputStream(privateKeyFile);
        try
        {
            final KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(fileInputStream, KEY_STORE_PASS.toCharArray());
            return keyStore;
        }
        finally
        {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private Logger                     logger     = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String                     comandoFirma;

    @Setter
    private String                     dbKey;

    @Setter
    private StepExecution              stepExecution;

    @Setter
    private String                     workingFolder;

    @Setter
    private boolean                    firmaCades = true;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution)
    {
        DatabaseContextHolder.clear();
        return ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws ItemStreamException
    {

    }

    private void firmaCades(KeyStore keyStore,
                            String workFolder,
                            FatturaElettronicaWrapperDto dto,
                            File fileDaFirmare) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, CertificateEncodingException, OperatorCreationException, IOException, CMSException
    {

    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {

    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException
    {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(org.springframework.batch.item.Chunk<? extends it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto> chunk) throws Exception
    {
        List<DatiFatturaInviataSdiDto> datiFattureList = null;
        if ( !stepExecution.getJobExecution().getExecutionContext().containsKey(BatchConstants.EXECUTIONCONTEXT_ELENCO_FATTURE) )
        {
            datiFattureList = new ArrayList<DatiFatturaInviataSdiDto>();
            stepExecution.getJobExecution().getExecutionContext().put(BatchConstants.EXECUTIONCONTEXT_ELENCO_FATTURE, datiFattureList);
        }
        else
        {
            datiFattureList = (List<DatiFatturaInviataSdiDto>) stepExecution.getJobExecution().getExecutionContext().get(BatchConstants.EXECUTIONCONTEXT_ELENCO_FATTURE);
        }
        String workFolder = stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR);
        String xadesNS = "http://uri.etsi.org/01903/v1.3.2#";
        String dsNS = "http://www.w3.org/2000/09/xmldsig#";
        String signedPropID = "SignedProperties_1";
        String signatureID = "Signature1";
        String signatureValueID = "SignatureValue1";
        String keyInfoID = "KeyInfoId";
        String prefixNameSpaceXades = "xades:";

        final KeyStore keyStore = loadKeyStore(ResourceUtils.getFile("classpath:chiavi/certificate.p12"));
        final Key privateKey = keyStore.getKey(PRIVATE_KEY_ALIAS, PRIVATE_KEY_PASS.toCharArray());
        final X509Certificate cert = (X509Certificate) keyStore.getCertificate(PRIVATE_KEY_ALIAS);

        for ( FatturaElettronicaWrapperDto dto : chunk )
        {
            if ( StringUtils.isBlank(dto.getFattura().getErroreValidazioneXml()) )
            {
                DatiFatturaInviataSdiDto dfiDto = new DatiFatturaInviataSdiDto();
                dfiDto.setProgressivoFile(dto.getProgressivoFile());
                dfiDto.setIdFattura(dto.getFattura().getId());
                dfiDto.setNomePacchetto(dto.getNomeFileFattura());
                dfiDto.setIdFatturaElettronica(dto.getIdFatturaElettronica());
                dfiDto.setTipoDocumento(dto.getFattura() instanceof FatturaDto ? TipoDocumentoEnum.FATTURA : TipoDocumentoEnum.NOTA_CREDITO);
                datiFattureList.add(dfiDto);
                File f = new File(workFolder, dto.getNomeFileFattura());
                FileUtils.writeByteArrayToFile(f, dto.getFlussoFatturaElettronica());

                if ( dto.getFattura().getClienteDto().getTipologia() == TipologiaClienteFornitore.PUBBLICA_AMMINISTRAZIONE )
                {
                    if ( firmaCades )
                    {
                        firmaCades(keyStore, workFolder, dto, f);
                    }
                    else
                    {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = dbf.newDocumentBuilder();
                        final Document doc = builder.parse(FileUtils.openInputStream(new File(workFolder, dto.getNomeFileFattura())));

                        String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
                        Provider provider = (Provider) Class.forName(providerName).newInstance();
                        XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM", provider);

                        final Document docDest = builder.newDocument();
                        Node objContent = docDest.importNode(doc.getDocumentElement(), true);
                        docDest.appendChild(objContent);

                        List<Reference> refs = new ArrayList<Reference>();
                        List<XPathType> xpaths = new ArrayList<XPathType>();
                        xpaths.add(new XPathType("/descendant::ds:Signature", XPathType.Filter.SUBTRACT));

                        Reference ref1 = sigFactory.newReference("", sigFactory.newDigestMethod(DigestMethod.SHA256, null), Collections.singletonList(sigFactory.newTransform(Transform.XPATH2, new XPathFilter2ParameterSpec(xpaths))), null, "reference-document");
                        refs.add(ref1);

                        Reference ref2 = sigFactory.newReference("#" + signedPropID, sigFactory.newDigestMethod(DigestMethod.SHA256, null), null, "http://uri.etsi.org/01903#SignedProperties", "reference-signedpropeties");
                        refs.add(ref2);

                        Reference ref3 = sigFactory.newReference("#" + keyInfoID, sigFactory.newDigestMethod(DigestMethod.SHA256, null), null, null, "reference-keyinfo");
                        refs.add(ref3);

                        CanonicalizationMethod cm = sigFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null);
                        SignatureMethod sm = sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
                        SignedInfo si = sigFactory.newSignedInfo(cm, sm, refs);
                        KeyInfoFactory kif = sigFactory.getKeyInfoFactory();
                        X509Data xd = kif.newX509Data(Collections.singletonList(cert));
                        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd), keyInfoID);
                        DOMSignContext dsc = new DOMSignContext(privateKey, objContent);
                        dsc.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
                        dsc.putNamespacePrefix(dsNS, "ds");
                        XMLObject xades = SignUtils.xadesProperty(xadesNS, signatureID, signedPropID, dsNS, sigFactory, docDest, cert);
                        List<XMLObject> objects = new ArrayList<XMLObject>();
                        objects.add(xades);
                        XMLSignature signature = sigFactory.newXMLSignature(si, ki, objects, signatureID, signatureValueID);
                        signature.sign(dsc);
                        FileOutputStream out = new FileOutputStream(new StringBuilder(workFolder).append(File.separator).append("firmati").append(File.separator).append(dto.getNomeFileFattura()).toString());
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer trans = tf.newTransformer();
                        trans.setOutputProperty(OutputKeys.INDENT, "yes");
                        trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                        trans.setOutputProperty(OutputKeys.STANDALONE, "no");
                        trans.transform(new DOMSource(docDest), new StreamResult(out));
                        out.close();
                    }
                }
                else
                {
                    FileUtils.copyFile(f, new File(new StringBuilder(workFolder).append(File.separator).append("firmati").toString(), FilenameUtils.getName(f.getAbsolutePath())));
                }
            }
        }
    }

    // private void firmaCades(KeyStore keyStore,
    // String workFolder,
    // FatturaElettronicaWrapperDto dto,
    // File fileDaFirmare) throws KeyStoreException, UnrecoverableEntryException, NoSuchAlgorithmException, CertificateEncodingException, OperatorCreationException, IOException, CMSException
    // {
    // Security.addProvider(new BouncyCastleProvider());
    // PasswordProtection pp = new PasswordProtection(PRIVATE_KEY_PASS.toCharArray());
    // KeyStore.Entry entry = keyStore.getEntry(PRIVATE_KEY_ALIAS, pp);
    // // boolean isPrivateKeyEntry = keyStore.entryInstanceOf(PRIVATE_KEY_ALIAS, KeyStore.PrivateKeyEntry.class);
    //
    // KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
    // PrivateKey pk = privateKeyEntry.getPrivateKey();
    // java.security.cert.Certificate[] chainx = privateKeyEntry.getCertificateChain();
    //
    // ArrayList<X509Certificate> certsin = new ArrayList<X509Certificate>();
    // for ( int i = 0; i < chainx.length; i++ )
    // {
    // certsin.add((X509Certificate) chainx[i]);
    // }
    // X509Certificate cert = certsin.get(0);
    //
    // String digestAlgorithm = "SHA-256";
    // // String digitalSignatureAlgorithmName = "SHA256withRSA";
    // MessageDigest sha = MessageDigest.getInstance(digestAlgorithm);
    // byte[] digestedCert = sha.digest(cert.getEncoded());
    //
    // AlgorithmIdentifier aiSha256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    // ESSCertIDv2 essCert1 = new ESSCertIDv2(aiSha256, digestedCert);
    // ESSCertIDv2[] essCert1Arr =
    // { essCert1 };
    // SigningCertificateV2 scv2 = new SigningCertificateV2(essCert1Arr);
    // Attribute certHAttribute = new Attribute(PKCSObjectIdentifiers.id_aa_signingCertificateV2, new DERSet(scv2));
    //
    // ASN1EncodableVector v = new ASN1EncodableVector();
    // v.add(certHAttribute);
    // AttributeTable at = new AttributeTable(v);
    // CMSAttributeTableGenerator attrGen = new DefaultSignedAttributeTableGenerator(at);
    //
    // SignerInfoGeneratorBuilder genBuild = new SignerInfoGeneratorBuilder(new BcDigestCalculatorProvider());
    // genBuild.setSignedAttributeGenerator(attrGen);
    //
    // CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
    // ContentSigner shaSigner = new JcaContentSignerBuilder("SHA256withRSA").build(pk);
    // SignerInfoGenerator sifGen = genBuild.build(shaSigner, new X509CertificateHolder(cert.getEncoded()));
    // gen.addSignerInfoGenerator(sifGen);
    // // X509CollectionStoreParameters x509CollectionStoreParameters = new X509CollectionStoreParameters(certsin);
    // JcaCertStore jcaCertStore = new JcaCertStore(certsin);
    // gen.addCertificates(jcaCertStore);
    // gen.addCRLs(jcaCertStore);
    // // gen.addAttributeCertificates(jcaCertStore);
    //
    // byte[] plainfile = FileUtils.readFileToByteArray(fileDaFirmare);
    // CMSTypedData msg = new CMSProcessableByteArray(plainfile);
    // CMSSignedData sigData = gen.generate(msg, true);
    // byte[] encoded = sigData.getEncoded();
    // FileOutputStream fos = new FileOutputStream(new StringBuilder(workFolder).append(File.separator).append("firmati").append(File.separator).append(dto.getNomeFileFattura()).toString());
    // // FileOutputStream fos = new FileOutputStream("F:\\text.rtf.7.p7m");
    // fos.write(encoded);
    // fos.flush();
    // fos.close();
    //
    // }

}

