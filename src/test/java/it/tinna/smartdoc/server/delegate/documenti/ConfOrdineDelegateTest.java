package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import it.tinna.smartdoc.server.dao.documenti.ConfOrdineDao;
import it.tinna.smartdoc.shared.dto.documenti.ConfOrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;

/**
 * Unit tests for ConfOrdineDelegate.
 */
public class ConfOrdineDelegateTest {

    @InjectMocks
    private ConfOrdineDelegate confOrdineDelegate;

    @Mock
    private ConfOrdineDao confOrdineDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave_InsertSuccess() throws SQLException {
        ConfOrdineDto dto = new ConfOrdineDto();
        dto.setId(0); // New document
        dto.setNumDocumento(1);
        dto.setDataDocumento("2026-03-21");
        
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setIdProdotto(40);
        prodotti.add(p);
        dto.setProdotti(prodotti);

        when(confOrdineDao.isExistentNumero(anyInt(), any(), any(), anyLong())).thenReturn(false);
        when(confOrdineDao.insert(any(ConfOrdineDto.class))).thenReturn(3001);
        
        Integer id = confOrdineDelegate.save(dto);
        
        assertEquals(3001, id);
        verify(confOrdineDao, times(1)).insert(dto);
        verify(confOrdineDao, times(1)).insertProdotto(any(ProdottoDocumentoDto.class));
    }

    @Test
    public void testSave_UpdateSuccess() throws SQLException {
        ConfOrdineDto dto = new ConfOrdineDto();
        dto.setId(3001L); // Existing document
        dto.setNumDocumento(1);
        dto.setDataDocumento("2026-03-21");

        when(confOrdineDao.isExistentNumero(anyInt(), any(), any(), anyLong())).thenReturn(false);
        
        Integer id = confOrdineDelegate.save(dto);
        
        assertEquals(3001, id.intValue());
        verify(confOrdineDao, times(1)).update(dto);
    }

    @Test
    public void testSave_Duplicate_ThrowsException() throws SQLException {
        ConfOrdineDto dto = new ConfOrdineDto();
        dto.setNumDocumento(1);
        dto.setDataDocumento("2026-03-21");

        when(confOrdineDao.isExistentNumero(anyInt(), any(), any(), anyLong())).thenReturn(true);
        
        assertThrows(SQLException.class, () -> confOrdineDelegate.save(dto));
    }
}
