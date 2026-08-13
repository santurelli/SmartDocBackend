package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import it.tinna.smartdoc.server.dao.documenti.FattureDao;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.contabilita.RegistrazioneContabileDelegate;

/**
 * Unit test to verify that the article quantity update logic (stock decrease) 
 * is correctly triggered when a Fattura is saved.
 */
public class FattureQuantitaDelegateTest {

    @InjectMocks
    private FattureDelegate fattureDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ConfigurazioneDelegate configurazioneDelegate;

    @Mock
    private RegistrazioneContabileDelegate registrazioneContabileDelegate;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Skip automatic revenue stamp logic in this test
        org.mockito.Mockito.when(configurazioneDelegate.getByKey(
            it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_DOMAIN_DOCUMENTI,
            it.tinna.smartdoc.shared.constants.ISharedConstants.CONFIG_KEY_ABILITA_BOLLO_AUTOMATICO
        )).thenReturn("0");
    }

    @Test
    public void testInsertFattura_VerifyScaricoFlag() throws Exception {
        // 1. Setup Test Data
        FatturaDto fatturaDto = new FatturaDto();
        fatturaDto.setNumDocumento(100);
        fatturaDto.setDataDocumento("21/03/2026");
        fatturaDto.setTipoFattura(TipoFattura.FATTURA);
        fatturaDto.setFlFatturaElettronica(1);

        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p1 = new ProdottoDocumentoDto();
        p1.setIdProdotto(1);
        p1.setQuantita(5.0);
        p1.setScarica(1); // Enable stock decrease
        prodotti.add(p1);
        
        fatturaDto.setProdotti(prodotti);

        // 2. Mock the DAO construction and behavior
        try (MockedConstruction<FattureDao> mockedDao = mockConstruction(FattureDao.class, (mock, context) -> {
            // Mock isExistentNumero to return false (no duplicate)
            // Signature: Integer, String, String, int, TipoFattura, Long
            org.mockito.Mockito.when(mock.isExistentNumero(
                org.mockito.ArgumentMatchers.any(), 
                org.mockito.ArgumentMatchers.any(), 
                org.mockito.ArgumentMatchers.any(), 
                org.mockito.ArgumentMatchers.anyInt(), 
                org.mockito.ArgumentMatchers.any(), 
                org.mockito.ArgumentMatchers.anyLong())).thenReturn(false);
            org.mockito.Mockito.when(mock.insert(any(FatturaDto.class))).thenReturn(123L);
        })) {
            
            // 3. Execute the method under test
            fattureDelegate.insert(fatturaDto);

            // 4. Verification
            // There are two constructions of FattureDao in FattureDelegate.insert():
            // 1st in isExistentNumero()
            // 2nd in the insert() method itself
            FattureDao daoMock = mockedDao.constructed().get(1);
            
            // Verify that insertProdotto was called on the mock that did the insert
            ArgumentCaptor<ProdottoDocumentoDto> prodottoCaptor = ArgumentCaptor.forClass(ProdottoDocumentoDto.class);
            verify(daoMock, times(1)).insertProdotto(prodottoCaptor.capture());
            
            ProdottoDocumentoDto capturedProdotto = prodottoCaptor.getValue();
            assertEquals(1, capturedProdotto.getScarica(), "The scarica flag should be set to 1 to decrease stock");
            assertEquals(5.0, capturedProdotto.getQuantita(), "The quantity should match the input");
            assertEquals(123L, capturedProdotto.getIdDocumento(), "The product should be associated with the new invoice ID");
        }
    }

    /*
     * NOTE: To verify that the actual quantity in the database changes, 
     * an integration test would be needed in a real environment with a database.
     * 
     * Example logic for an integration test:
     * 
     * @Test
     * public void integrationTest_StockUpdate() {
     *     // 1. Get initial stock
     *     double initialStock = jdbcTemplate.queryForObject("SELECT get_totale_disponibile(?, 1)", Double.class, productId);
     *     
     *     // 2. Insert Fattura with fl_scarica = 1
     *     fattureDelegate.insert(myFatturaDto);
     *     
     *     // 3. Get final stock
     *     double finalStock = jdbcTemplate.queryForObject("SELECT get_totale_disponibile(?, 1)", Double.class, productId);
     *     
     *     // 4. Assert
     *     assertEquals(initialStock - myFatturaDto.getProdotti().get(0).getQuantita(), finalStock);
     * }
     */
}
