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
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

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
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById_Success() throws SQLException {
        ProdottoDto mockDto = new ProdottoDto();
        mockDto.setId(101L);
        mockDto.setDescrizione("Test Prodotto");

        try (MockedConstruction<ProdottiDao> mockedDao = mockConstruction(ProdottiDao.class, (mock, context) -> {
            when(mock.getById(101L)).thenReturn(mockDto);
        })) {
            
            ProdottoDto result = prodottiDelegate.getById(101L);
            
            assertNotNull(result);
            assertEquals(101L, result.getId());
            assertEquals("Test Prodotto", result.getDescrizione());
        }
    }

    @Test
    public void testInsert_Success() throws SQLException {
        ProdottoDto dto = new ProdottoDto();
        dto.setDescrizione("New Prodotto");

        try (MockedConstruction<ProdottiDao> mockedDao = mockConstruction(ProdottiDao.class)) {
            prodottiDelegate.insert(dto);
            
            verify(mockedDao.constructed().get(0), times(1)).insert(dto);
        }
    }

    @Test
    public void testUpdate_Success() throws SQLException {
        ProdottoDto dto = new ProdottoDto();
        dto.setId(101L);
        dto.setDescrizione("Updated Prodotto");

        try (MockedConstruction<ProdottiDao> mockedDao = mockConstruction(ProdottiDao.class)) {
            prodottiDelegate.update(dto);
            
            verify(mockedDao.constructed().get(0), times(1)).update(dto);
        }
    }

    @Test
    public void testIsExistentCodice_Success() throws SQLException {
        try (MockedConstruction<ProdottiDao> mockedDao = mockConstruction(ProdottiDao.class, (mock, context) -> {
            when(mock.isExistentCodice("P001", null)).thenReturn(true);
        })) {
            
            boolean exists = prodottiDelegate.isExistentCodice("P001", null);
            
            assertTrue(exists);
            verify(mockedDao.constructed().get(0), times(1)).isExistentCodice("P001", null);
        }
    }
    @Test
    public void testGetPrezzoDocumento_Success() throws SQLException {
        List<PrezzoProdottoDto> mockPrezzi = new ArrayList<>();
        
        PrezzoProdottoDto p1 = new PrezzoProdottoDto();
        p1.setIdListino(1);
        p1.setPrezzo(10.0);
        
        PrezzoProdottoDto p2 = new PrezzoProdottoDto();
        p2.setIdListino(2);
        p2.setPrezzo(20.0);
        
        mockPrezzi.add(p1);
        mockPrezzi.add(p2);

        try (MockedConstruction<PrezziProdottiDao> mockedDao = mockConstruction(PrezziProdottiDao.class, (mock, context) -> {
            when(mock.getByIdProdotto(101L)).thenReturn(mockPrezzi);
        })) {
            
            // Test explicit listino 2
            Double result2 = prodottiDelegate.getPrezzoDocumento(101L, 2L);
            assertEquals(20.0, result2);
            
            // Test fallback to listino 1
            Double resultFallback = prodottiDelegate.getPrezzoDocumento(101L, 3L);
            assertEquals(10.0, resultFallback);
            
            // Test default when no listino provided
            Double resultDefault = prodottiDelegate.getPrezzoDocumento(101L, null);
            assertEquals(10.0, resultDefault);
        }
    }
}
