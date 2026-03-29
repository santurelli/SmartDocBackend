package it.tinna.smartdoc.server.delegate.prodotti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.delegate.listini.PricingDelegate;
import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.server.dao.prodotti.PrezziProdottiDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import java.math.BigDecimal;

/**
 * Unit tests for ProdottiDelegate.
 */
public class ProdottiDelegateTest {

    @InjectMocks
    private ProdottiDelegate prodottiDelegate;

    @Mock
    private ProdottiDao prodottiDao; // Added for existing tests
    @Mock
    private PricingDelegate pricingDelegate;

    @Mock
    private PrezziProdottiDao prezziProdottiDao;

    @Mock
    private ListiniDelegate listiniDelegate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById_Success() throws SQLException {
        ProdottoDto mockDto = new ProdottoDto();
        mockDto.setId(101L);
        mockDto.setDescrizione("Test Prodotto");

        when(prodottiDao.getById(101L)).thenReturn(mockDto);
            
        ProdottoDto result = prodottiDelegate.getById(101L);
            
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Test Prodotto", result.getDescrizione());
        verify(prodottiDao, times(1)).getById(101L);
    }

    @Test
    public void testInsert_Success() throws SQLException {
        ProdottoDto dto = new ProdottoDto();
        dto.setDescrizione("New Prodotto");

        prodottiDelegate.insert(dto);
            
        verify(prodottiDao, times(1)).insert(dto);
    }

    @Test
    public void testUpdate_Success() throws SQLException {
        ProdottoDto dto = new ProdottoDto();
        dto.setId(101L);
        dto.setDescrizione("Updated Prodotto");

        prodottiDelegate.update(dto);
            
        verify(prodottiDao, times(1)).update(dto);
    }

    @Test
    public void testIsExistentCodice_Success() throws SQLException {
        when(prodottiDao.isExistentCodice("P001", null)).thenReturn(true);
            
        boolean exists = prodottiDelegate.isExistentCodice("P001", null);
            
        assertTrue(exists);
        verify(prodottiDao, times(1)).isExistentCodice("P001", null);
    }
    @Test
    public void testGetPrezzoDocumento_Success() throws SQLException {
        when(pricingDelegate.calculatePrice(101L, 2L)).thenReturn(20.0);
        
        Double result = prodottiDelegate.getPrezzoDocumento(101L, 2L);
        
        assertEquals(20.0, result);
        verify(pricingDelegate, times(1)).calculatePrice(101L, 2L);
    }

    @Test
    public void testGetPrezzi_Success() throws SQLException {
        List<PrezzoProdottoDto> mockPrezzi = new ArrayList<>();
        PrezzoProdottoDto p1 = new PrezzoProdottoDto();
        p1.setPrezzo(15.0);
        mockPrezzi.add(p1);

        when(prezziProdottiDao.getByIdProdotto(101L)).thenReturn(mockPrezzi);

        List<PrezzoProdottoDto> result = prodottiDelegate.getPrezzi(101L);

        assertEquals(1, result.size());
        assertEquals(15.0, result.get(0).getPrezzo());
        verify(prezziProdottiDao, times(1)).getByIdProdotto(101L);
    }

    @Test
    public void testSavePrezzi_Success() throws SQLException {
        List<PrezzoProdottoDto> prezziToSave = new ArrayList<>();
        PrezzoProdottoDto p1 = new PrezzoProdottoDto();
        p1.setIdListino(1);
        p1.setPrezzo(50.0);
        prezziToSave.add(p1);

        prodottiDelegate.savePrezzi(101L, prezziToSave);

        verify(prezziProdottiDao, times(1)).deleteByIdProdotto(101L);
        verify(prezziProdottiDao, times(1)).insert(any(PrezzoProdottoDto.class));
    }
}
