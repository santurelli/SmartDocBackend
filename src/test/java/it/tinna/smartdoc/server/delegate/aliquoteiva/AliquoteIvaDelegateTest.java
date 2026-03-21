package it.tinna.smartdoc.server.delegate.aliquoteiva;

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

import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;

/**
 * Unit tests for AliquoteIvaDelegate.
 */
public class AliquoteIvaDelegateTest {

    @InjectMocks
    private AliquoteIvaDelegate aliquoteIvaDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetById_Success() throws SQLException {
        AliquotaIvaDto mockDto = new AliquotaIvaDto();
        mockDto.setId(22);
        mockDto.setDescrizione("IVA 22%");

        try (MockedConstruction<AliquoteIvaDao> mockedDao = mockConstruction(AliquoteIvaDao.class, (mock, context) -> {
            when(mock.getById(22)).thenReturn(mockDto);
        })) {
            
            AliquotaIvaDto result = aliquoteIvaDelegate.getById(22);
            
            assertNotNull(result);
            assertEquals(22, result.getId());
            assertEquals("IVA 22%", result.getDescrizione());
        }
    }

    @Test
    public void testInsert_Success() throws SQLException {
        AliquotaIvaDto dto = new AliquotaIvaDto();
        dto.setDescrizione("IVA 4%");
        dto.setPredefinita(0); // Fix: avoids NPE on intValue()
        dto.setUserCreated(1L);

        try (MockedConstruction<AliquoteIvaDao> mockedDao = mockConstruction(AliquoteIvaDao.class)) {
            aliquoteIvaDelegate.insert(dto);
            verify(mockedDao.constructed().get(0), times(1)).insert(dto);
        }
    }

    @Test
    public void testUpdate_Success() throws SQLException {
        AliquotaIvaDto dto = new AliquotaIvaDto();
        dto.setId(22);
        dto.setDescrizione("Updated IVA 22%");
        dto.setPredefinita(1); // Fix: avoids NPE
        dto.setUserCreated(1L);

        try (MockedConstruction<AliquoteIvaDao> mockedDao = mockConstruction(AliquoteIvaDao.class)) {
            aliquoteIvaDelegate.update(dto);
            verify(mockedDao.constructed().get(0), times(1)).update(dto);
            verify(mockedDao.constructed().get(0), times(1)).resetPredefinite(1L);
        }
    }
}
