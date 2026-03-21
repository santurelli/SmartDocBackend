package it.tinna.smartdoc.server.delegate.primanota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import it.tinna.smartdoc.server.dao.primanota.PrimaNotaDao;
import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.dipendenti.DipendentiDao;
import it.tinna.smartdoc.shared.dto.primanota.PrimaNotaDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.primanota.PagamentoPrimaNotaDto;

/**
 * Unit tests for PrimaNotaDelegate.
 */
public class PrimaNotaDelegateTest {

    @InjectMocks
    private PrimaNotaDelegate primaNotaDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetList_SoggettoHydration_Cliente() throws SQLException {
        PrimaNotaDto mockDto = new PrimaNotaDto();
        mockDto.setId(1L);
        mockDto.setIdSoggetto("C-100"); // Customer with ID 100
        
        List<PrimaNotaDto> dtoList = new ArrayList<>();
        dtoList.add(mockDto);

        ClienteDto mockCliente = new ClienteDto();
        mockCliente.setId(100);
        mockCliente.setDenominazione("Test Cliente");

        try (MockedConstruction<PrimaNotaDao> mockedPrimaNotaDao = mockConstruction(PrimaNotaDao.class, (mock, context) -> {
            when(mock.getList(any(), any(), any(), any(), any(), any(), anyLong(), any(), any(), any(), any())).thenReturn(dtoList);
        });
        MockedConstruction<ClientiDao> mockedClientiDao = mockConstruction(ClientiDao.class, (mock, context) -> {
            when(mock.getById(100L)).thenReturn(mockCliente);
        });
        MockedConstruction<FornitoriDao> mockedFornitoriDao = mockConstruction(FornitoriDao.class);
        MockedConstruction<DipendentiDao> mockedDipendentiDao = mockConstruction(DipendentiDao.class)) {
            
            List<PrimaNotaDto> result = primaNotaDelegate.getList(null, null, null, null, null, null, 0L, null, null, 0, "asc");
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getObjSoggetto());
            assertEquals("C-100", result.get(0).getObjSoggetto().getId());
            assertEquals("Test Cliente", result.get(0).getObjSoggetto().getDenominazione());
        }
    }

    @Test
    public void testGetList_SoggettoHydration_Fornitore() throws SQLException {
        PrimaNotaDto mockDto = new PrimaNotaDto();
        mockDto.setId(2L);
        mockDto.setIdSoggetto("F-200"); // Supplier with ID 200
        
        List<PrimaNotaDto> dtoList = new ArrayList<>();
        dtoList.add(mockDto);

        FornitoreDto mockFornitore = new FornitoreDto();
        mockFornitore.setId(200);
        mockFornitore.setDenominazione("Test Fornitore");

        try (MockedConstruction<PrimaNotaDao> mockedPrimaNotaDao = mockConstruction(PrimaNotaDao.class, (mock, context) -> {
            when(mock.getList(any(), any(), any(), any(), any(), any(), anyLong(), any(), any(), any(), any())).thenReturn(dtoList);
        });
        MockedConstruction<FornitoriDao> mockedFornitoriDao = mockConstruction(FornitoriDao.class, (mock, context) -> {
            when(mock.getById(200)).thenReturn(mockFornitore);
        });
        MockedConstruction<ClientiDao> mockedClientiDao = mockConstruction(ClientiDao.class);
        MockedConstruction<DipendentiDao> mockedDipendentiDao = mockConstruction(DipendentiDao.class)) {
            
            List<PrimaNotaDto> result = primaNotaDelegate.getList(null, null, null, null, null, null, 0L, null, null, 0, "asc");
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getObjSoggetto());
            assertEquals("F-200", result.get(0).getObjSoggetto().getId());
            assertEquals("Test Fornitore", result.get(0).getObjSoggetto().getDenominazione());
        }
    }

    @Test
    public void testInsertPagamento_Success() throws SQLException {
        PagamentoPrimaNotaDto dto = new PagamentoPrimaNotaDto();
        
        try (MockedConstruction<PrimaNotaDao> mockedDao = mockConstruction(PrimaNotaDao.class)) {
            primaNotaDelegate.insertPagamento(dto);
            verify(mockedDao.constructed().get(0), times(1)).insertPagamento(dto);
        }
    }
}
