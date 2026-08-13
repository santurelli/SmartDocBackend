package it.tinna.smartdoc.server.delegate.documenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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

import it.tinna.smartdoc.server.dao.documenti.NoteCreditoDao;
import it.tinna.smartdoc.server.delegate.contabilita.RegistrazioneContabileDelegate;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;

/**
 * Unit tests for NoteCreditoDelegate.
 */
public class NoteCreditoDelegateTest {

    @InjectMocks
    private NoteCreditoDelegate noteCreditoDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RegistrazioneContabileDelegate registrazioneContabileDelegate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsert_Success() throws SQLException {
        NotaCreditoDto notaDto = new NotaCreditoDto();
        notaDto.setNumDocumento(50);
        notaDto.setDataDocumento("2026-03-21");
        notaDto.setFlFatturaElettronica(0); // FIXED: set field to avoid NPE
        
        List<ProdottoDocumentoDto> prodotti = new ArrayList<>();
        ProdottoDocumentoDto p = new ProdottoDocumentoDto();
        p.setIdProdotto(30);
        prodotti.add(p);
        notaDto.setProdotti(prodotti);

        try (MockedConstruction<NoteCreditoDao> mockedDao = mockConstruction(NoteCreditoDao.class, (mock, context) -> {
            when(mock.isExistentNumero(any(), any(), any(), anyInt(), anyLong())).thenReturn(false);
            when(mock.insert(any(NotaCreditoDto.class))).thenReturn(2001L);
            when(mock.getTotale(2001L)).thenReturn(100.0);
            when(mock.getTotalePagato(2001L)).thenReturn(0.0);
        })) {
            long id = noteCreditoDelegate.insert(notaDto);
            
            assertEquals(2001L, id);
            NoteCreditoDao daoMock = mockedDao.constructed().get(1);
            verify(daoMock, times(1)).insert(notaDto);
            verify(daoMock, times(1)).insertProdotto(any(ProdottoDocumentoDto.class));
        }
    }

    @Test
    public void testInsert_Duplicate_ThrowsException() throws SQLException {
        NotaCreditoDto notaDto = new NotaCreditoDto();
        notaDto.setNumDocumento(50);
        notaDto.setDataDocumento("2026-03-21");
        notaDto.setFlFatturaElettronica(0); // FIXED: set field to avoid NPE

        try (MockedConstruction<NoteCreditoDao> mockedDao = mockConstruction(NoteCreditoDao.class, (mock, context) -> {
            when(mock.isExistentNumero(any(), any(), any(), anyInt(), anyLong())).thenReturn(true);
        })) {
            assertThrows(SQLException.class, () -> noteCreditoDelegate.insert(notaDto));
        }
    }
}
