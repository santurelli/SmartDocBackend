package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;

public class FatturaElettronicaDelegateTest
{

    @InjectMocks
    private FatturaElettronicaDelegate delegate;

    @Mock
    private NumerazioneFatturaElettronicaDelegate numerazioneFatturaElettronicaDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() throws Exception
    {
        MockitoAnnotations.openMocks(this);
        when(numerazioneFatturaElettronicaDelegate.getNumero(anyInt())).thenReturn("00001");
    }

    @Test
    public void testGetFatturaElettronica_NoRitenuta() throws Exception
    {
        FatturaDto dto = createBaseFattura();
        dto.setFlRitenutaAcconto(0);

        DatiAziendaDto datiAziendaDto = createBaseDatiAzienda();

        try (MockedConstruction<AliquoteIvaDao> mockedAiDao = mockConstruction(AliquoteIvaDao.class, (mock, context) -> {
            AliquotaIvaDto ai = new AliquotaIvaDto();
            ai.setImposta(22.0);
            ai.setClasse("IVA22");
            when(mock.getById(anyInt())).thenReturn(ai);
        });
             MockedConstruction<TipiPagamentoDao> mockedTpDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
                 TipoPagamentoDto tp = new TipoPagamentoDto();
                 tp.setModalita("BONIFICO");
                 when(mock.getById(anyInt())).thenReturn(tp);
             });
             MockedConstruction<DatiAziendaDao> mockedDaDao = mockConstruction(DatiAziendaDao.class, (mock, context) -> {
                 when(mock.getDatiAzienda()).thenReturn(datiAziendaDto);
             }))
        {
            delegate.getFatturaElettronica(dto, "testStore");
        }

        assertNotNull(dto.getXmlFattura(), "Validation error: " + dto.getErroreValidazioneXml() + "\nXML: " + dto.getXmlNonValido());
        assertTrue(dto.getXmlFattura().contains("<FatturaElettronica"));
        assertTrue(!dto.getXmlFattura().contains("<DatiRitenuta>"));
    }

    @Test
    public void testGetFatturaElettronica_WithRitenuta() throws Exception
    {
        FatturaDto dto = createBaseFattura();
        dto.setFlRitenutaAcconto(1);
        dto.setTipoRitenuta("PERSONE_FISICHE");
        dto.setPercRitenutaAcconto(20.0);
        dto.setImportoRitenutaAcconto(new java.math.BigDecimal("200.00"));
        dto.getProdotti().get(0).setFlRitenuta(1);

        DatiAziendaDto datiAziendaDto = createBaseDatiAzienda();

        try (MockedConstruction<AliquoteIvaDao> mockedAiDao = mockConstruction(AliquoteIvaDao.class, (mock, context) -> {
            AliquotaIvaDto ai = new AliquotaIvaDto();
            ai.setImposta(22.0);
            ai.setClasse("IVA22");
            when(mock.getById(anyInt())).thenReturn(ai);
        });
             MockedConstruction<TipiPagamentoDao> mockedTpDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
                 TipoPagamentoDto tp = new TipoPagamentoDto();
                 tp.setModalita("BONIFICO");
                 when(mock.getById(anyInt())).thenReturn(tp);
             });
             MockedConstruction<DatiAziendaDao> mockedDaDao = mockConstruction(DatiAziendaDao.class, (mock, context) -> {
                 when(mock.getDatiAzienda()).thenReturn(datiAziendaDto);
             }))
        {
            delegate.getFatturaElettronica(dto, "testStore");
        }

        assertNotNull(dto.getXmlFattura());
        assertTrue(dto.getXmlFattura().contains("<DatiRitenuta>"));
        assertTrue(dto.getXmlFattura().contains("<TipoRitenuta>RT01</TipoRitenuta>"));
        assertTrue(dto.getXmlFattura().contains("<ImportoRitenuta>200.00</ImportoRitenuta>"));
        assertTrue(dto.getXmlFattura().contains("<Ritenuta>SI</Ritenuta>"));
    }

    @Test
    public void testGetFatturaElettronica_FromScontrino() throws Exception
    {
        FatturaDto dto = createBaseFattura();
        dto.setNumeroScontrino(123);
        dto.setDataScontrino("01/01/2024");

        DatiAziendaDto datiAziendaDto = createBaseDatiAzienda();

        try (MockedConstruction<AliquoteIvaDao> mockedAiDao = mockConstruction(AliquoteIvaDao.class, (mock, context) -> {
            AliquotaIvaDto ai = new AliquotaIvaDto();
            ai.setImposta(22.0);
            ai.setClasse("IVA22");
            when(mock.getById(anyInt())).thenReturn(ai);
        });
             MockedConstruction<TipiPagamentoDao> mockedTpDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
                 TipoPagamentoDto tp = new TipoPagamentoDto();
                 tp.setModalita("BONIFICO");
                 when(mock.getById(anyInt())).thenReturn(tp);
             });
             MockedConstruction<DatiAziendaDao> mockedDaDao = mockConstruction(DatiAziendaDao.class, (mock, context) -> {
                 when(mock.getDatiAzienda()).thenReturn(datiAziendaDto);
             }))
        {
            delegate.getFatturaElettronica(dto, "testStore");
        }

        assertNotNull(dto.getXmlFattura());
        assertTrue(dto.getXmlFattura().contains("RIFERIMENTO SCONTRINO N. 123 DEL 01/01/2024"));
        
        // Verifica la presenza della sezione AltriDatiGestionali a livello di riga
        assertTrue(dto.getXmlFattura().contains("<AltriDatiGestionali>"));
        assertTrue(dto.getXmlFattura().contains("<TipoDato>SCONTRINO</TipoDato>"));
        assertTrue(dto.getXmlFattura().contains("<RiferimentoTesto>123</RiferimentoTesto>"));
        assertTrue(dto.getXmlFattura().contains("<RiferimentoData>2024-01-01</RiferimentoData>"));

        // Verifica che il TipoDocumento sia TD24 (Fattura Differita)
        assertTrue(dto.getXmlFattura().contains("<TipoDocumento>TD24</TipoDocumento>"));
    }

    @Test
    public void testGetFatturaElettronica_MixedRitenuta() throws Exception
    {
        FatturaDto dto = createBaseFattura();
        dto.setFlRitenutaAcconto(1);
        dto.setTipoRitenuta("PERSONE_FISICHE");
        dto.setPercRitenutaAcconto(20.0);
        dto.setImportoRitenutaAcconto(new java.math.BigDecimal("200.00"));

        // Primo prodotto ha la ritenuta abilitata
        dto.getProdotti().get(0).setFlRitenuta(1);

        // Secondo prodotto non ha la ritenuta
        ProdottoDocumentoDto p2 = new ProdottoDocumentoDto();
        p2.setProdotto(true);
        p2.setCodiceProdotto("P002");
        p2.setDescProdotto("Prodotto Esente Test");
        p2.setQuantita(2.0);
        p2.setPrezzo(50.0);
        p2.setTotaleSenzaIva(100.0);
        p2.setIdAliquotaIva(1);
        p2.setFlRitenuta(0);
        dto.getProdotti().add(p2);

        DatiAziendaDto datiAziendaDto = createBaseDatiAzienda();

        try (MockedConstruction<AliquoteIvaDao> mockedAiDao = mockConstruction(AliquoteIvaDao.class, (mock, context) -> {
            AliquotaIvaDto ai = new AliquotaIvaDto();
            ai.setImposta(22.0);
            ai.setClasse("IVA22");
            when(mock.getById(anyInt())).thenReturn(ai);
        });
             MockedConstruction<TipiPagamentoDao> mockedTpDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
                 TipoPagamentoDto tp = new TipoPagamentoDto();
                 tp.setModalita("BONIFICO");
                 when(mock.getById(anyInt())).thenReturn(tp);
             });
             MockedConstruction<DatiAziendaDao> mockedDaDao = mockConstruction(DatiAziendaDao.class, (mock, context) -> {
                 when(mock.getDatiAzienda()).thenReturn(datiAziendaDto);
             }))
        {
            delegate.getFatturaElettronica(dto, "testStore");
        }

        assertNotNull(dto.getXmlFattura());
        assertTrue(dto.getXmlFattura().contains("<DatiRitenuta>"));
        assertTrue(dto.getXmlFattura().contains("<TipoRitenuta>RT01</TipoRitenuta>"));
        assertTrue(dto.getXmlFattura().contains("<ImportoRitenuta>200.00</ImportoRitenuta>"));
        
        // Verifica che la Ritenuta di linea sia aggiunta ESATTAMENTE una volta (solo al primo prodotto)
        String xml = dto.getXmlFattura();
        int count = xml.length() - xml.replace("<Ritenuta>SI</Ritenuta>", "").length();
        int occurences = count / "<Ritenuta>SI</Ritenuta>".length();
        org.junit.jupiter.api.Assertions.assertEquals(1, occurences, "La Ritenuta dovrebbe essere applicata solo a una delle due linee");
    }

    private FatturaDto createBaseFattura()
    {
        FatturaDto dto = new FatturaDto();
        dto.setDataDocumento("02/03/2026");
        dto.setNumDocumento(1);
        dto.setParticella("/A");
        dto.setTotale(1220.0);
        dto.setTotaleIva(220.0);
        dto.setSplitPayment(0);
        dto.setEsigibilitaDifferita(0);
        dto.setIdTipoPagamento(1);
        dto.setIbanNsBanca("IT1234567890123456789012345");

        ClienteDto cliente = new ClienteDto();
        cliente.setTipologia(TipologiaClienteFornitore.AZIENDA);
        cliente.setDenominazione("Cliente Test");
        cliente.setPartitaIva("09876543210");
        cliente.setCodiceFiscale("09876543210");
        dto.setClienteDto(cliente);

        dto.setIndirizzoIntestazione("Via Cliente 1");
        dto.setCapIntestazione("00100");
        dto.setCittaIntestazione("Roma");
        dto.setProvinciaIntestazione("RM");
        dto.setNazioneIntestazione("IT");

        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setProdotto(true);
        p.setCodiceProdotto("P001");
        p.setDescProdotto("Prodotto Test");
        p.setQuantita(10.0);
        p.setPrezzo(100.0);
        p.setTotaleSenzaIva(1000.0);
        p.setIdAliquotaIva(1);
        p.setFlRitenuta(0);
        prodotti.add(p);
        dto.setProdotti(prodotti);

        List<ScadenzaPagamentoDocumentoDto> scadenze = new ArrayList<>();
        ScadenzaPagamentoDocumentoDto s = new ScadenzaPagamentoDocumentoDto();
        s.setDtScadenza("02/03/2026");
        s.setImporto(1220.0);
        scadenze.add(s);
        dto.setListaScadenzePagamentiDocumento(scadenze);

        return dto;
    }

    private DatiAziendaDto createBaseDatiAzienda()
    {
        DatiAziendaDto dati = new DatiAziendaDto();
        dati.setPartitaIva("12345678901");
        dati.setCodiceFiscale("12345678901");
        dati.setDenominazione("Azienda Test");
        dati.setIndirizzo("Via Test 1");
        dati.setCap("00100");
        dati.setCitta("Roma");
        dati.setProvincia("RM");
        dati.setNazione("IT");
        dati.setIdRegimeFiscale(1);
        dati.setValoreRegimeFiscale("ORDINARIO");
        return dati;
    }
}
