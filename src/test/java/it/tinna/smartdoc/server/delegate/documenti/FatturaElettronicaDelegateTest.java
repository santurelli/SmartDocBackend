package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.documenti.FatturaElettronicaDao;
import it.tinna.smartdoc.server.dao.nazioni.NazioniDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.RicevutaConsegnaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.DestinatarioType;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeFactory;

/**
 * Unit tests for FatturaElettronicaDelegate.
 */
public class FatturaElettronicaDelegateTest {

    @InjectMocks
    private FatturaElettronicaDelegate fatturaElettronicaDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private NumerazioneFatturaElettronicaDelegate numerazioneDelegate;
    
    @Mock
    private NazioniDao nazioniDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFatturaElettronica_BasicGeneration() throws Exception {
        FatturaDto dto = new FatturaDto();
        dto.setTipoFattura(TipoFattura.FATTURA); // Fix: ensures TD01
        dto.setNumDocumento(123);
        dto.setDataDocumento("21/03/2026");
        dto.setTotale(122.0);
        dto.setTotaleIva(22.0);
        dto.setSplitPayment(0);
        dto.setEsigibilitaDifferita(0);
        
        // Fix: provide installments to avoid auto-discount to 0.00
        List<ScadenzaPagamentoDocumentoDto> scadenze = new ArrayList<>();
        ScadenzaPagamentoDocumentoDto s = new ScadenzaPagamentoDocumentoDto();
        s.setImporto(122.0);
        s.setDtScadenza("21/04/2026");
        s.setModalitaPagamento("BONIFICO");
        scadenze.add(s);
        dto.setListaScadenzePagamentiDocumento(scadenze);
        dto.setIdTipoPagamento(1);
        
        ClienteDto cliente = new ClienteDto();
        cliente.setTipologia(TipologiaClienteFornitore.PRIVATO);
        cliente.setDenominazione("Test Cliente");
        cliente.setPartitaIva("12345678901");
        dto.setClienteDto(cliente);
        
        dto.setIndirizzoIntestazione("Via Test 1");
        dto.setCittaIntestazione("Milano");
        dto.setCapIntestazione("20100");
        dto.setProvinciaIntestazione("MI");
        dto.setNazioneIntestazione("Italia");
        
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setProdotto(true);
        p.setCodiceProdotto("P001");
        p.setDescProdotto("Prodotto Test");
        p.setQuantita(1.0);
        p.setPrezzo(100.0);
        p.setTotaleSenzaIva(100.0);
        p.setIdAliquotaIva(22);
        prodotti.add(p);
        dto.setProdotti(prodotti);

        DatiAziendaDto azienda = new DatiAziendaDto();
        azienda.setPartitaIva("09876543210");
        azienda.setDenominazione("Mia Azienda");
        azienda.setValoreRegimeFiscale("ORDINARIO"); 
        azienda.setIndirizzo("Via Azienda 1");
        azienda.setCap("00100");
        azienda.setCitta("Roma");
        azienda.setProvincia("RM");

        AliquotaIvaDto iva = new AliquotaIvaDto();
        iva.setImposta(22.0);
        iva.setClasse("IVA22");

        TipoPagamentoDto tp = new TipoPagamentoDto();
        tp.setModalita("MP05"); // BONIFICO SDI code

        try (MockedConstruction<DatiAziendaDao> mockedAziendaDao = mockConstruction(DatiAziendaDao.class, (mock, context) -> {
            when(mock.getDatiAzienda()).thenReturn(azienda);
        });
        MockedConstruction<AliquoteIvaDao> mockedIvaDao = mockConstruction(AliquoteIvaDao.class, (mock, context) -> {
            when(mock.getById(22)).thenReturn(iva);
        });
        MockedConstruction<TipiPagamentoDao> mockedTpDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
            when(mock.getById(1)).thenReturn(tp);
        })) {
            
            when(numerazioneDelegate.getNumero(2026)).thenReturn("00001");
            when(nazioniDao.getCodiceIsoByNome(anyString())).thenReturn("IT");
            
            fatturaElettronicaDelegate.getFatturaElettronica(dto, null);
            
            String xml = dto.getXmlFattura() != null ? dto.getXmlFattura() : dto.getXmlNonValido();
            
            assertNotNull(xml, "XML should be generated");
            assertTrue(xml.contains("<Denominazione>Mia Azienda</Denominazione>"));
            assertTrue(xml.contains("<Denominazione>Test Cliente</Denominazione>"));
            assertTrue(xml.contains("<TipoDocumento>TD01</TipoDocumento>"));
            assertTrue(xml.contains("<ImportoTotaleDocumento>122.00</ImportoTotaleDocumento>"));
        }
    }

    @Test
    public void testAggiornaDatiEsitoSdi_RicevutaConsegna() throws Exception {
        RicevutaConsegnaType ricevuta = new RicevutaConsegnaType();
        ricevuta.setIdentificativoSdI(new BigInteger("12345"));
        ricevuta.setMessageId("MSG-001");
        ricevuta.setNomeFile("IT09876543210_00001_RC_001.xml");
        ricevuta.setDataOraConsegna(DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-03-21T10:00:00"));
        DestinatarioType dest = new DestinatarioType();
        dest.setDescrizione("Destinatario Test");
        ricevuta.setDestinatario(dest);

        File mockFile = new File("test_rc.xml");

        try (MockedConstruction<FatturaElettronicaDao> mockedDao = mockConstruction(FatturaElettronicaDao.class)) {
            fatturaElettronicaDelegate.aggiornaDatiEsitoSdi(mockFile, ricevuta);
            
            verify(mockedDao.constructed().get(0), times(1)).aggiornaDatiRicevutaConsegna(
                eq("12345"), eq("MSG-001"), any(), eq("Destinatario Test"), anyString(), eq("00001")
            );
            verify(mockedDao.constructed().get(0), times(1)).memorizzaEsitoSdi("00001");
        }
    }
}
