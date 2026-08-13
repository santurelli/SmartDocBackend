package it.tinna.smartdoc.server.delegate.contabilita;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.contabilita.RegistrazioneContabileDao;
import it.tinna.smartdoc.server.service.contabilita.ContoResolverService;
import it.tinna.smartdoc.shared.dto.contabilita.MovimentoContabileRigaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;

/**
 * Unit test del motore di generazione automatica delle scritture contabili (Fase 3).
 * Copre Fatture, Note di Debito, Note di Credito clienti (ciclo attivo), Fatture e Note di Credito Fornitore
 * (ciclo passivo), e i casi limite (Proforma, conto mancante, rigenerazione su update).
 *
 * Nota: le Note di Debito Fornitore NON sono attualmente gestite da questo motore (a differenza delle
 * Note di Debito clienti, che sono un semplice TipoFattura sulla stessa tabella Fatture) - non esiste
 * un documento/tabella dedicato nel dominio applicativo per questo caso lato fornitori.
 */
public class RegistrazioneContabileDelegateTest {

    @InjectMocks
    private RegistrazioneContabileDelegate delegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ChiusuraEsercizioDelegate chiusuraEsercizioDelegate;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Di default nessun esercizio e' chiuso: i test esistenti verificano il comportamento "a esercizio aperto".
        when(chiusuraEsercizioDelegate.isEsercizioChiuso(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
    }

    private ProdottoDocumentoDto riga(Integer idProdotto, double prezzoImponibile, Integer idAliquotaIva) {
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setIdProdotto(idProdotto);
        p.setPrezzoImponibile(prezzoImponibile);
        p.setIdAliquotaIva(idAliquotaIva);
        return p;
    }

    private ProdottoDocumentoDto riga(Integer idProdotto, double prezzoImponibile, Integer idAliquotaIva, Integer idContoOverride) {
        ProdottoDocumentoDto p = riga(idProdotto, prezzoImponibile, idAliquotaIva);
        p.setIdContoOverride(idContoOverride);
        return p;
    }

    private FatturaDto fattura(TipoFattura tipo, List<ProdottoDocumentoDto> prodotti) {
        FatturaDto dto = new FatturaDto();
        dto.setId(500);
        dto.setNumDocumento(9);
        dto.setDataDocumento("12/08/2026");
        dto.setIdCliente(15);
        dto.setTipoFattura(tipo);
        dto.setProdotti(prodotti);
        dto.setUserCreated(7L);
        return dto;
    }

    @Test
    public void testGeneraDaFattura_RigheCorretteEBilanciate() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolveContoRicavoArticolo(801)).thenReturn(80);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFattura(dto);

            // La prima costruzione del DAO e' quella di pulizia incrociata (tipo alternativo), la seconda e' quella
            // effettivamente usata da generaScrittura per cancellare/creare la registrazione.
            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(1);

            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            List<MovimentoContabileRigaDto> righe = righeCaptor.getAllValues();
            assertEquals(3, righe.size());

            BigDecimal totaleDare = righe.stream().map(MovimentoContabileRigaDto::getImportoDare).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totaleAvere = righe.stream().map(MovimentoContabileRigaDto::getImportoAvere).reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(0, totaleDare.compareTo(totaleAvere), "La scrittura deve essere bilanciata: Dare == Avere");
            assertEquals(0, new BigDecimal("110.00").compareTo(totaleDare));

            // Riga cliente: Dare sul conto 30, importo 110
            MovimentoContabileRigaDto rigaCliente = righe.get(0);
            assertEquals(30, rigaCliente.getIdConto());
            assertEquals(0, new BigDecimal("110.00").compareTo(rigaCliente.getImportoDare()));
            assertEquals(0, BigDecimal.ZERO.compareTo(rigaCliente.getImportoAvere()));

            // Riga ricavo: Avere sul conto 80, importo 100
            MovimentoContabileRigaDto rigaRicavo = righe.get(1);
            assertEquals(80, rigaRicavo.getIdConto());
            assertEquals(0, new BigDecimal("100.00").compareTo(rigaRicavo.getImportoAvere()));

            // Riga IVA: Avere sul conto 95, importo 10
            MovimentoContabileRigaDto rigaIva = righe.get(2);
            assertEquals(95, rigaIva.getIdConto());
            assertEquals(0, new BigDecimal("10.00").compareTo(rigaIva.getImportoAvere()));
        }
    }

    @Test
    public void testGeneraDaFattura_RaggruppaRicaviSulloStessoConto() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        prodotti.add(riga(802, 50.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolveContoRicavoArticolo(801)).thenReturn(80);
            when(mock.resolveContoRicavoArticolo(802)).thenReturn(80); // stesso conto ricavo del primo articolo
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFattura(dto);

            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(1);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            // 2 articoli sullo stesso conto ricavo devono generare UNA sola riga ricavo (sommata), non due:
            // totale righe attese = 1 cliente + 1 ricavo (sommato) + 1 iva = 3
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            MovimentoContabileRigaDto rigaRicavo = righeCaptor.getAllValues().get(1);
            assertEquals(0, new BigDecimal("150.00").compareTo(rigaRicavo.getImportoAvere()), "I due articoli sullo stesso conto devono sommarsi in un'unica riga");
        }
    }

    @Test
    public void testGeneraDaFattura_RigaFuoriMagazzino_UsaContoRuoloGenericoInveceDiSaltarla() throws Exception {
        // Riga fuori magazzino: idProdotto nullo (non e' un articolo di catalogo, niente cascata da risolvere)
        // ma con un imponibile reale - deve finire sul conto di ruolo generico, non essere saltata in silenzio.
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(null, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolveContoRuolo("RICAVI_VENDITE")).thenReturn(80);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFattura(dto);

            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(1);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            ContoResolverService resolver = mockedResolver.constructed().get(0);
            verify(resolver, never()).resolveContoRicavoArticolo(org.mockito.ArgumentMatchers.anyLong());
            verify(resolver, times(1)).resolveContoRuolo("RICAVI_VENDITE");

            MovimentoContabileRigaDto rigaRicavo = righeCaptor.getAllValues().get(1);
            assertEquals(80, rigaRicavo.getIdConto());
            assertEquals(0, new BigDecimal("100.00").compareTo(rigaRicavo.getImportoAvere()));
        }
    }

    @Test
    public void testGeneraDaFattura_ContoOverride_VinceSullaCascataAutomatica() throws Exception {
        // Un articolo di catalogo con un override manuale sulla riga: l'override deve vincere,
        // la cascata automatica (resolveContoRicavoArticolo) non deve nemmeno essere interrogata.
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1, 77));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFattura(dto);

            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(1);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            ContoResolverService resolver = mockedResolver.constructed().get(0);
            verify(resolver, never()).resolveContoRicavoArticolo(org.mockito.ArgumentMatchers.anyLong());

            MovimentoContabileRigaDto rigaRicavo = righeCaptor.getAllValues().get(1);
            assertEquals(77, rigaRicavo.getIdConto(), "Il conto usato deve essere quello dell'override (77), non quello della cascata automatica");
            assertEquals(0, new BigDecimal("100.00").compareTo(rigaRicavo.getImportoAvere()));
        }
    }

    @Test
    public void testGeneraDaNotaCredito_InvertitaRispettoAllaFattura() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        NotaCreditoDto dto = new NotaCreditoDto();
        dto.setId(600);
        dto.setNumDocumento(1);
        dto.setDataDocumento("12/08/2026");
        dto.setIdCliente(15);
        dto.setProdotti(prodotti);
        dto.setUserCreated(7L);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolveContoRicavoArticolo(801)).thenReturn(80);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaNotaCredito(dto);

            // La Nota di Credito non fa la pulizia incrociata FATTURA/NOTA_DEBITO: un solo DAO costruito.
            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(0);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            List<MovimentoContabileRigaDto> righe = righeCaptor.getAllValues();
            // Cliente: Avere (invertito rispetto alla Fattura)
            assertEquals(0, new BigDecimal("110.00").compareTo(righe.get(0).getImportoAvere()));
            assertEquals(0, BigDecimal.ZERO.compareTo(righe.get(0).getImportoDare()));
            // Ricavo: Dare (invertito)
            assertEquals(0, new BigDecimal("100.00").compareTo(righe.get(1).getImportoDare()));
            // IVA: Dare (invertito)
            assertEquals(0, new BigDecimal("10.00").compareTo(righe.get(2).getImportoDare()));

            verify(daoUsato, times(1)).insertTestata(any());
        }
    }

    @Test
    public void testGeneraDaFattura_NotaDiDebito_EtichettataCorrettamenteENonInvertita() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.NOTA_DEBITO, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolveContoRicavoArticolo(801)).thenReturn(80);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFattura(dto);

            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(1);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());
            // Stessa direzione della Fattura: cliente in Dare (non invertito come la Nota di Credito)
            assertEquals(0, new BigDecimal("110.00").compareTo(righeCaptor.getAllValues().get(0).getImportoDare()));
        }
    }

    @Test
    public void testGeneraDaFattura_Proforma_NonGeneraAlcunaScrittura() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA_PROFORMA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
             })) {

            delegate.generaDaFattura(dto);

            // Nessun ContoResolverService dovrebbe mai essere costruito per una Proforma (si esce prima).
            assertEquals(0, mockedResolver.constructed().size());
            // L'unico DAO costruito serve solo per l'eventuale pulizia di una registrazione preesistente,
            // ma insertTestata non deve mai essere invocato.
            for (RegistrazioneContabileDao dao : mockedDao.constructed()) {
                verify(dao, never()).insertTestata(any());
            }
        }
    }

    @Test
    public void testGeneraDaFattura_EsercizioChiuso_NonGeneraENonFallisce() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);
        when(chiusuraEsercizioDelegate.isEsercizioChiuso("12/08/2026")).thenReturn(true);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
             })) {

            // Non deve lanciare eccezioni ne' costruire il resolver: si esce prima, appena si sa che l'esercizio e' chiuso.
            delegate.generaDaFattura(dto);

            assertEquals(0, mockedResolver.constructed().size());
            for (RegistrazioneContabileDao dao : mockedDao.constructed()) {
                verify(dao, never()).insertTestata(any());
            }
        }
    }

    @Test
    public void testGeneraDaFattura_NessunContoClienteRisolto_NonGeneraENonFallisce() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(null);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
             })) {

            // Non deve lanciare eccezioni: la generazione e' additiva, un piano dei conti incompleto
            // non deve mai impedire il salvataggio del documento chiamante.
            delegate.generaDaFattura(dto);

            for (RegistrazioneContabileDao dao : mockedDao.constructed()) {
                verify(dao, never()).insertTestata(any());
            }
        }
    }

    @Test
    public void testGeneraDaFattura_RigenerazioneIdempotente_CancellaPrimaDiRicreare() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaDto dto = fattura(TipoFattura.FATTURA, prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoCliente(15)).thenReturn(30);
            when(mock.resolveContoRicavoArticolo(801)).thenReturn(80);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_DEBITO")).thenReturn(95);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFattura(dto);

            // Chiamata di pulizia incrociata (verso NOTA_DEBITO, dato che il documento e' una FATTURA)
            RegistrazioneContabileDao daoPulizia = mockedDao.constructed().get(0);
            verify(daoPulizia, times(1)).deleteByDocumento("NOTA_DEBITO", 500);

            // Il DAO usato dentro generaScrittura deve sempre cancellare prima di reinserire, cosi' un
            // successivo update non genera una seconda registrazione duplicata per lo stesso documento.
            RegistrazioneContabileDao daoScrittura = mockedDao.constructed().get(1);
            verify(daoScrittura, times(1)).deleteByDocumento("FATTURA", 500);
            verify(daoScrittura, times(1)).insertTestata(any());
        }
    }

    @Test
    public void testEliminaRegistrazione_Fattura_PuliceAncheEtichettaNotaDebito() throws Exception {
        try (MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class)) {
            delegate.eliminaRegistrazione("FATTURA", 123L);

            RegistrazioneContabileDao dao = mockedDao.constructed().get(0);
            verify(dao, times(1)).deleteByDocumento("FATTURA", 123L);
            verify(dao, times(1)).deleteByDocumento("NOTA_DEBITO", 123L);
        }
    }

    // --- Ciclo passivo: Fatture Fornitore e Note di Credito Fornitore ---

    private FatturaFornitoreDto fatturaFornitore(List<ProdottoDocumentoDto> prodotti) {
        FatturaFornitoreDto dto = new FatturaFornitoreDto();
        dto.setId(700);
        dto.setNumDocumento(11);
        dto.setDataDocumento("12/08/2026");
        dto.setIdFornitore(20);
        dto.setProdotti(prodotti);
        dto.setUserCreated(7L);
        return dto;
    }

    @Test
    public void testGeneraDaFatturaFornitore_RigheCorretteEBilanciate() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaFornitoreDto dto = fatturaFornitore(prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoFornitore(20)).thenReturn(70);
            when(mock.resolveContoCostoArticolo(801)).thenReturn(90);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_CREDITO")).thenReturn(96);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFatturaFornitore(dto);

            // La Fattura Fornitore non fa alcuna pulizia incrociata (non condivide la tabella con altri tipi
            // documento come invece fanno Fatture/Note di Debito clienti): un solo DAO costruito.
            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(0);

            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            List<MovimentoContabileRigaDto> righe = righeCaptor.getAllValues();
            assertEquals(3, righe.size());

            BigDecimal totaleDare = righe.stream().map(MovimentoContabileRigaDto::getImportoDare).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totaleAvere = righe.stream().map(MovimentoContabileRigaDto::getImportoAvere).reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(0, totaleDare.compareTo(totaleAvere), "La scrittura deve essere bilanciata: Dare == Avere");
            assertEquals(0, new BigDecimal("110.00").compareTo(totaleDare));

            // Riga costo: Dare sul conto 90, importo 100
            MovimentoContabileRigaDto rigaCosto = righe.get(0);
            assertEquals(90, rigaCosto.getIdConto());
            assertEquals(0, new BigDecimal("100.00").compareTo(rigaCosto.getImportoDare()));
            assertEquals(0, BigDecimal.ZERO.compareTo(rigaCosto.getImportoAvere()));

            // Riga IVA: Dare sul conto 96, importo 10
            MovimentoContabileRigaDto rigaIva = righe.get(1);
            assertEquals(96, rigaIva.getIdConto());
            assertEquals(0, new BigDecimal("10.00").compareTo(rigaIva.getImportoDare()));

            // Riga fornitore: Avere sul conto 70, importo 110
            MovimentoContabileRigaDto rigaFornitore = righe.get(2);
            assertEquals(70, rigaFornitore.getIdConto());
            assertEquals(0, new BigDecimal("110.00").compareTo(rigaFornitore.getImportoAvere()));
            assertEquals(0, BigDecimal.ZERO.compareTo(rigaFornitore.getImportoDare()));
        }
    }

    @Test
    public void testGeneraDaFatturaFornitore_RaggruppaCostiSulloStessoConto() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        prodotti.add(riga(802, 50.0, 1));
        FatturaFornitoreDto dto = fatturaFornitore(prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoFornitore(20)).thenReturn(70);
            when(mock.resolveContoCostoArticolo(801)).thenReturn(90);
            when(mock.resolveContoCostoArticolo(802)).thenReturn(90); // stesso conto costo del primo articolo
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_CREDITO")).thenReturn(96);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFatturaFornitore(dto);

            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(0);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            // 2 articoli sullo stesso conto costo devono generare UNA sola riga costo (sommata), non due:
            // totale righe attese = 1 costo (sommato) + 1 iva + 1 fornitore = 3
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            MovimentoContabileRigaDto rigaCosto = righeCaptor.getAllValues().get(0);
            assertEquals(0, new BigDecimal("150.00").compareTo(rigaCosto.getImportoDare()), "I due articoli sullo stesso conto devono sommarsi in un'unica riga");
        }
    }

    @Test
    public void testGeneraDaNotaCreditoFornitore_InvertitaRispettoAllaFatturaFornitore() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        NotaCreditoFornitoreDto dto = new NotaCreditoFornitoreDto();
        dto.setId(800);
        dto.setNumDocumento(1);
        dto.setDataDocumento("12/08/2026");
        dto.setIdFornitore(20);
        dto.setProdotti(prodotti);
        dto.setUserCreated(7L);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoFornitore(20)).thenReturn(70);
            when(mock.resolveContoCostoArticolo(801)).thenReturn(90);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_CREDITO")).thenReturn(96);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaNotaCreditoFornitore(dto);

            RegistrazioneContabileDao daoUsato = mockedDao.constructed().get(0);
            ArgumentCaptor<MovimentoContabileRigaDto> righeCaptor = ArgumentCaptor.forClass(MovimentoContabileRigaDto.class);
            verify(daoUsato, times(3)).insertRiga(anyLong(), righeCaptor.capture());

            List<MovimentoContabileRigaDto> righe = righeCaptor.getAllValues();
            // Costo: Avere (storno, invertito)
            assertEquals(0, new BigDecimal("100.00").compareTo(righe.get(0).getImportoAvere()));
            // IVA: Avere (storno, invertito)
            assertEquals(0, new BigDecimal("10.00").compareTo(righe.get(1).getImportoAvere()));
            // Fornitore: Dare (storno debito, invertito rispetto alla Fattura Fornitore)
            assertEquals(0, new BigDecimal("110.00").compareTo(righe.get(2).getImportoDare()));
            assertEquals(0, BigDecimal.ZERO.compareTo(righe.get(2).getImportoAvere()));

            verify(daoUsato, times(1)).insertTestata(any());
        }
    }

    @Test
    public void testGeneraDaFatturaFornitore_NessunContoFornitoreRisolto_NonGeneraENonFallisce() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaFornitoreDto dto = fatturaFornitore(prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoFornitore(20)).thenReturn(null);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
             })) {

            // Non deve lanciare eccezioni: stesso principio "mai bloccante" del ciclo attivo.
            delegate.generaDaFatturaFornitore(dto);

            for (RegistrazioneContabileDao dao : mockedDao.constructed()) {
                verify(dao, never()).insertTestata(any());
            }
        }
    }

    @Test
    public void testGeneraDaFatturaFornitore_RigenerazioneIdempotente_CancellaPrimaDiRicreare() throws Exception {
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        prodotti.add(riga(801, 100.0, 1));
        FatturaFornitoreDto dto = fatturaFornitore(prodotti);

        try (MockedConstruction<ContoResolverService> mockedResolver = mockConstruction(ContoResolverService.class, (mock, ctx) -> {
            when(mock.resolveContoFornitore(20)).thenReturn(70);
            when(mock.resolveContoCostoArticolo(801)).thenReturn(90);
            when(mock.resolvePercentualeIva(1)).thenReturn(BigDecimal.TEN);
            when(mock.resolveContoRuolo("IVA_CREDITO")).thenReturn(96);
        });
             MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class, (mock, ctx) -> {
                 when(mock.insertTestata(any())).thenReturn(1L);
             })) {

            delegate.generaDaFatturaFornitore(dto);

            RegistrazioneContabileDao daoScrittura = mockedDao.constructed().get(0);
            verify(daoScrittura, times(1)).deleteByDocumento("FATTURA_FORNITORE", 700);
            verify(daoScrittura, times(1)).insertTestata(any());
        }
    }

    @Test
    public void testEliminaRegistrazione_FatturaFornitore_NonPuliceEtichettaNotaDebito() throws Exception {
        // A differenza di "FATTURA" (che condivide la tabella con le Note di Debito clienti),
        // "FATTURA_FORNITORE" non ha etichette alternative da ripulire: le Note di Debito Fornitore
        // non sono attualmente gestite da questo motore.
        try (MockedConstruction<RegistrazioneContabileDao> mockedDao = mockConstruction(RegistrazioneContabileDao.class)) {
            delegate.eliminaRegistrazione("FATTURA_FORNITORE", 456L);

            RegistrazioneContabileDao dao = mockedDao.constructed().get(0);
            verify(dao, times(1)).deleteByDocumento("FATTURA_FORNITORE", 456L);
            verify(dao, never()).deleteByDocumento("NOTA_DEBITO", 456L);
        }
    }
}
