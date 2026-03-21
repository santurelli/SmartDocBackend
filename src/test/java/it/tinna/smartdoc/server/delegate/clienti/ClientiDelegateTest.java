package it.tinna.smartdoc.server.delegate.clienti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

/**
 * Unit tests for ClientiDelegate.
 */
public class ClientiDelegateTest {

    @InjectMocks
    private ClientiDelegate clientiDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInsert_Success() throws SQLException {
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setDenominazione("Test Client");
        
        List<IndirizzoDto> indirizziToAdd = new ArrayList<>();
        IndirizzoDto addr = new IndirizzoDto();
        addr.setIndirizzo("Via Roma 1");
        indirizziToAdd.add(addr);
        
        List<ContattoDto> contattiToAdd = new ArrayList<>();
        ContattoDto cont = new ContattoDto();
        cont.setEmail("test@example.com");
        contattiToAdd.add(cont);

        try (MockedConstruction<ClientiDao> mockedClientiDao = mockConstruction(ClientiDao.class, (mock, context) -> {
            when(mock.insert(any(ClienteDto.class))).thenReturn(100);
        });
        MockedConstruction<IndirizziDao> mockedIndirizziDao = mockConstruction(IndirizziDao.class);
        MockedConstruction<ContattiDao> mockedContattiDao = mockConstruction(ContattiDao.class)) {
            
            Integer id = clientiDelegate.insert(clienteDto, indirizziToAdd, new ArrayList<>(), contattiToAdd, new ArrayList<>());
            
            assertEquals(100, id);
            
            verify(mockedClientiDao.constructed().get(0), times(1)).insert(clienteDto);
            verify(mockedIndirizziDao.constructed().get(0), times(1)).insert(anyString(), any(IndirizzoDto.class));
            verify(mockedContattiDao.constructed().get(0), times(1)).insert(anyString(), any(ContattoDto.class));
        }
    }

    @Test
    public void testGetById_Success() throws SQLException {
        ClienteDto mockDto = new ClienteDto();
        mockDto.setId(100);
        mockDto.setDenominazione("Test Client");

        try (MockedConstruction<ClientiDao> mockedClientiDao = mockConstruction(ClientiDao.class, (mock, context) -> {
            when(mock.getById(100)).thenReturn(mockDto);
        });
        MockedConstruction<IndirizziDao> mockedIndirizziDao = mockConstruction(IndirizziDao.class, (mock, context) -> {
            when(mock.getListByIdRichiedente(anyString(), anyLong())).thenReturn(new ArrayList<>());
        });
        MockedConstruction<ContattiDao> mockedContattiDao = mockConstruction(ContattiDao.class, (mock, context) -> {
            when(mock.getListByIdRichiedente(anyString(), anyLong())).thenReturn(new ArrayList<>());
        })) {
            
            ClienteDto result = clientiDelegate.getById(100);
            
            assertEquals(100, result.getId());
            assertEquals("Test Client", result.getDenominazione());
        }
    }
}
