package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import it.tinna.smartdoc.server.dao.documenti.PreventiviDao;
import it.tinna.smartdoc.shared.dto.documenti.PreventivoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;

/**
 * Unit tests for PreventiviDelegate.
 */
public class PreventiviDelegateTest {

    @InjectMocks
    private PreventiviDelegate preventiviDelegate;

    @Mock
    private PreventiviDao preventiviDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsert_Success() throws SQLException {
        PreventivoDto dto = new PreventivoDto();
        dto.setId(0);
        dto.setNumDocumento(500);
        dto.setDataDocumento("2026-03-21");
        
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setIdProdotto(50);
        prodotti.add(p);
        dto.setProdotti(prodotti);

        when(preventiviDao.isExistentNumero(any(), any(), any(), anyInt())).thenReturn(false);
        when(preventiviDao.insert(any(PreventivoDto.class))).thenReturn(4001);
        
        Integer id = preventiviDelegate.insert(dto);
        
        assertEquals(4001, id.intValue());
        verify(preventiviDao, times(1)).insert(dto);
        verify(preventiviDao, times(1)).insertProdotto(any(ProdottoDocumentoDto.class));
    }

    @Test
    public void testInsert_Duplicate_ThrowsException() throws SQLException {
        PreventivoDto dto = new PreventivoDto();
        dto.setNumDocumento(500);
        dto.setDataDocumento("2026-03-21");

        when(preventiviDao.isExistentNumero(any(), any(), any(), anyInt())).thenReturn(true);
        
        assertThrows(SQLException.class, () -> preventiviDelegate.insert(dto));
    }
}
