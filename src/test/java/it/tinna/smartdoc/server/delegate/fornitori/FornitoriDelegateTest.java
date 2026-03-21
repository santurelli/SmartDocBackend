package it.tinna.smartdoc.server.delegate.fornitori;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;

/**
 * Unit tests for FornitoriDelegate.
 */
public class FornitoriDelegateTest {

    @InjectMocks
    private FornitoriDelegate fornitoriDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsert_Success() throws SQLException {
        FornitoreDto fornitoreDto = new FornitoreDto();
        fornitoreDto.setDenominazione("Test Fornitore");
        fornitoreDto.setUserCreated(1L);
        
        List<IndirizzoDto> indirizziToAdd = new ArrayList<>();
        IndirizzoDto addr = new IndirizzoDto();
        addr.setIndirizzo("Via Fornitori 1");
        indirizziToAdd.add(addr);
        
        List<ContattoDto> contattiToAdd = new ArrayList<>();
        ContattoDto cont = new ContattoDto();
        cont.setEmail("supplier@test.com");
        contattiToAdd.add(cont);

        try (MockedConstruction<FornitoriDao> mockedFornitoriDao = mockConstruction(FornitoriDao.class, (mock, context) -> {
            when(mock.insert(any(FornitoreDto.class))).thenReturn(500L);
        });
        MockedConstruction<IndirizziDao> mockedIndirizziDao = mockConstruction(IndirizziDao.class);
        MockedConstruction<ContattiDao> mockedContattiDao = mockConstruction(ContattiDao.class)) {
            
            long id = fornitoriDelegate.insert(fornitoreDto, indirizziToAdd, new ArrayList<>(), contattiToAdd, new ArrayList<>());
            
            assertEquals(500L, id);
            
            verify(mockedFornitoriDao.constructed().get(0), times(1)).insert(fornitoreDto);
            // Fix: Richiedente value is lowercase "fornitori"
            verify(mockedIndirizziDao.constructed().get(0), times(1)).insert(eq("fornitori"), any(IndirizzoDto.class));
            verify(mockedContattiDao.constructed().get(0), times(1)).insert(eq("fornitori"), any(ContattoDto.class));
        }
    }

    @Test
    public void testGetById_Success() throws SQLException {
        FornitoreDto mockDto = new FornitoreDto();
        mockDto.setId(500);
        mockDto.setDenominazione("Test Fornitore");

        try (MockedConstruction<FornitoriDao> mockedFornitoriDao = mockConstruction(FornitoriDao.class, (mock, context) -> {
            when(mock.getById(500)).thenReturn(mockDto);
        });
        MockedConstruction<IndirizziDao> mockedIndirizziDao = mockConstruction(IndirizziDao.class, (mock, context) -> {
            when(mock.getListByIdRichiedente(eq("fornitori"), anyLong())).thenReturn(new ArrayList<>());
        });
        MockedConstruction<ContattiDao> mockedContattiDao = mockConstruction(ContattiDao.class, (mock, context) -> {
            when(mock.getListByIdRichiedente(eq("fornitori"), anyLong())).thenReturn(new ArrayList<>());
        })) {
            
            FornitoreDto result = fornitoriDelegate.getById(500);
            
            assertNotNull(result);
            assertEquals(500, result.getId());
            assertEquals("Test Fornitore", result.getDenominazione());
            assertNotNull(result.getElencoIndirizzi());
            assertNotNull(result.getElencoContatti());
        }
    }

    @Test
    public void testUpdate_Success() throws SQLException {
        FornitoreDto fornitoreDto = new FornitoreDto();
        fornitoreDto.setId(500);
        fornitoreDto.setDenominazione("Test Fornitore Updated");
        fornitoreDto.setElencoIndirizzi(new ArrayList<>());
        fornitoreDto.setElencoContatti(new ArrayList<>()); // Fix: initialize to avoid NPE in subtract
        
        // Mocking existing data to test subtract logic
        List<IndirizzoDto> existingIndirizzi = new ArrayList<>();
        IndirizzoDto existingAddr = new IndirizzoDto();
        existingAddr.setId(999L);
        existingIndirizzi.add(existingAddr);

        try (MockedConstruction<FornitoriDao> mockedFornitoriDao = mockConstruction(FornitoriDao.class);
        MockedConstruction<IndirizziDao> mockedIndirizziDao = mockConstruction(IndirizziDao.class, (mock, context) -> {
            when(mock.getListByIdRichiedente(eq("fornitori"), eq(500L))).thenReturn(existingIndirizzi);
        });
        MockedConstruction<ContattiDao> mockedContattiDao = mockConstruction(ContattiDao.class, (mock, context) -> {
            when(mock.getListByIdRichiedente(eq("fornitori"), anyLong())).thenReturn(new ArrayList<>());
        })) {
            
            fornitoriDelegate.update(fornitoreDto, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            
            verify(mockedFornitoriDao.constructed().get(0), times(1)).update(fornitoreDto);
            // Verify deletion of the address that's no longer in the list
            verify(mockedIndirizziDao.constructed().get(0), times(1)).deleteByIdRichiedente(eq("fornitori"), eq(500L), eq(999L));
        }
    }
}
