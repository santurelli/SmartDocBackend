package it.tinna.smartdoc.server.delegate.tipipagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;

/**
 * Unit tests for TipiPagamentoDelegate.
 */
public class TipiPagamentoDelegateTest {

    @InjectMocks
    private TipiPagamentoDelegate tipiPagamentoDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById_Success() throws SQLException {
        TipoPagamentoDto mockDto = new TipoPagamentoDto();
        mockDto.setId(10);
        mockDto.setDescrizione("Bonifico 30gg");

        try (MockedConstruction<TipiPagamentoDao> mockedDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
            when(mock.getById(10)).thenReturn(mockDto);
            when(mock.getScadenze(10)).thenReturn(new ArrayList<>());
        })) {
            
            TipoPagamentoDto result = tipiPagamentoDelegate.getById(10);
            
            assertNotNull(result);
            assertEquals(10, result.getId());
            assertEquals("Bonifico 30gg", result.getDescrizione());
            assertNotNull(result.getScadenze());
        }
    }

    @Test
    public void testInsert_Success() throws SQLException {
        TipoPagamentoDto dto = new TipoPagamentoDto();
        dto.setDescrizione("New Payment Type");
        dto.setPredefinito(0); // Fix: avoids NPE
        dto.setUserCreated(1L);
        dto.setScadenze(new ArrayList<>()); // Fix: avoid NPE in loop

        try (MockedConstruction<TipiPagamentoDao> mockedDao = mockConstruction(TipiPagamentoDao.class, (mock, context) -> {
            when(mock.insert(any(TipoPagamentoDto.class))).thenReturn(100);
        })) {
            tipiPagamentoDelegate.insert(dto);
            verify(mockedDao.constructed().get(0), times(1)).insert(dto);
        }
    }

    @Test
    public void testUpdate_Success() throws SQLException {
        TipoPagamentoDto dto = new TipoPagamentoDto();
        dto.setId(10);
        dto.setDescrizione("Updated Payment Type");
        dto.setPredefinito(1); // Fix: avoids NPE
        dto.setUserCreated(1L);
        dto.setScadenze(new ArrayList<>()); // Fix: avoid NPE in loop

        try (MockedConstruction<TipiPagamentoDao> mockedDao = mockConstruction(TipiPagamentoDao.class)) {
            tipiPagamentoDelegate.update(dto);
            verify(mockedDao.constructed().get(0), times(1)).update(dto);
            verify(mockedDao.constructed().get(0), times(1)).resetPredefinite(1L);
        }
    }
}
