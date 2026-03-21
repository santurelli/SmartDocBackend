package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import it.tinna.smartdoc.server.dao.documenti.DdtDao;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;

/**
 * Unit tests for DdtDelegate.
 */
public class DdtDelegateTest {

    @InjectMocks
    private DdtDelegate ddtDelegate;

    @Mock
    private DdtDao ddtDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsert_Success() throws SQLException {
        DdtDto ddtDto = new DdtDto();
        ddtDto.setNumDocumento(1);
        ddtDto.setDataDocumento("2026-03-21");
        
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setIdProdotto(10);
        prodotti.add(p);
        ddtDto.setProdotti(prodotti);

        when(ddtDao.isExistentNumero(any(), any(), any(), anyLong())).thenReturn(false);
        when(ddtDao.insert(any(DdtDto.class))).thenReturn(1001L);
        
        long id = ddtDelegate.insert(ddtDto);
        
        assertEquals(1001L, id);
        verify(ddtDao, times(1)).insert(ddtDto);
        verify(ddtDao, times(1)).insertProdotto(any(ProdottoDocumentoDto.class));
    }

    @Test
    public void testInsert_DuplicateNumber_ThrowsException() throws SQLException {
        DdtDto ddtDto = new DdtDto();
        ddtDto.setNumDocumento(1);
        ddtDto.setDataDocumento("2026-03-21");

        when(ddtDao.isExistentNumero(any(), any(), any(), anyLong())).thenReturn(true);
        
        assertThrows(SQLException.class, () -> ddtDelegate.insert(ddtDto));
    }

    @Test
    public void testUpdate_Success() throws SQLException {
        DdtDto ddtDto = new DdtDto();
        ddtDto.setId(1001L);
        ddtDto.setNumDocumento(1);
        ddtDto.setDataDocumento("2026-03-21");
        
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setIdProdotto(20);
        prodotti.add(p);
        ddtDto.setProdotti(prodotti);

        when(ddtDao.isExistentNumero(any(), any(), any(), anyLong())).thenReturn(false);
        
        ddtDelegate.update(ddtDto);
        
        verify(ddtDao, times(1)).update(ddtDto);
        verify(ddtDao, times(1)).deleteProdottiById(1001L);
        verify(ddtDao, times(1)).insertProdotto(any(ProdottoDocumentoDto.class));
    }
}
