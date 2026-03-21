package it.tinna.smartdoc.server.delegate.agenti;

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
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.agenti.AgentiDao;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;

/**
 * Unit tests for AgentiDelegate.
 */
public class AgentiDelegateTest {

    @InjectMocks
    private AgentiDelegate agentiDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById_Success() throws SQLException {
        AgenteDto mockDto = new AgenteDto();
        mockDto.setId(200);
        mockDto.setDenominazione("Agente Test");

        try (MockedConstruction<AgentiDao> mockedDao = mockConstruction(AgentiDao.class, (mock, context) -> {
            when(mock.getById(200)).thenReturn(mockDto);
        })) {
            
            AgenteDto result = agentiDelegate.getById(200);
            
            assertNotNull(result);
            assertEquals(200, result.getId());
            assertEquals("Agente Test", result.getDenominazione());
        }
    }

    @Test
    public void testDeleteBatch_Success() throws SQLException {
        List<Long> idsToDelete = Arrays.asList(1L, 2L, 3L);

        try (MockedConstruction<AgentiDao> mockedDao = mockConstruction(AgentiDao.class)) {
            agentiDelegate.delete(99L, idsToDelete);
            
            AgentiDao daoMock = mockedDao.constructed().get(0);
            verify(daoMock, times(1)).delete(99L, 1L);
            verify(daoMock, times(1)).delete(99L, 2L);
            verify(daoMock, times(1)).delete(99L, 3L);
        }
    }

    @Test
    public void testInsert_Success() throws SQLException {
        AgenteDto dto = new AgenteDto();
        dto.setDenominazione("New Agente");

        try (MockedConstruction<AgentiDao> mockedDao = mockConstruction(AgentiDao.class)) {
            agentiDelegate.insert(dto);
            
            verify(mockedDao.constructed().get(0), times(1)).insert(dto);
        }
    }
}
