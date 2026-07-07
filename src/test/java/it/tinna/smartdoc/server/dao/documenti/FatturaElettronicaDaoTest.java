package it.tinna.smartdoc.server.dao.documenti;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;

public class FatturaElettronicaDaoTest {

    private FatturaElettronicaDao dao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dao = new FatturaElettronicaDao(jdbcTemplate);
    }

    @Test
    public void testAggiornaStatoFattura_BypassesConnectionReuse() throws SQLException {
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        dao.aggiornaStatoFattura(123L, StatoFatturaElettronica.RC);

        verify(jdbcTemplate, times(1)).getDataSource();
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setString(1, "RC");
        verify(preparedStatement, times(1)).setLong(2, 123L);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    public void testAggiornaStatoNotaCredito_BypassesConnectionReuse() throws SQLException {
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        dao.aggiornaStatoNotaCredito(456L, StatoFatturaElettronica.RC);

        verify(jdbcTemplate, times(1)).getDataSource();
        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setString(1, "RC");
        verify(preparedStatement, times(1)).setLong(2, 456L);
        verify(preparedStatement, times(1)).executeUpdate();
        verify(preparedStatement, times(1)).close();
        verify(connection, times(1)).close();
    }

    @Test
    public void testMemorizzaFatturaElettronica_ExistingRecord_UpdatesInsteadOfInserts() throws SQLException {
        FatturaElettronicaWrapperDto wrapper = new FatturaElettronicaWrapperDto();
        FatturaDto fattura = new FatturaDto();
        fattura.setId(999L);
        fattura.setNumDocumento(12345);
        fattura.setDataDocumento("26/06/2026");
        ClienteDto cliente = new ClienteDto();
        cliente.setDenominazione("Test Customer");
        fattura.setClienteDto(cliente);
        wrapper.setFattura(fattura);

        // Simulate that the record already exists with ID 777
        when(jdbcTemplate.queryForList(anyString(), eq(Long.class), anyString(), eq(999L), anyString()))
            .thenReturn(Collections.singletonList(777L));

        long id = dao.memorizzaFatturaElettronica("sd_justeat", wrapper);

        assertEquals(777L, id);
        // Verify that update is run and not insert
        verify(jdbcTemplate, times(1)).update(
            anyString(),
            any(), any(), any(), anyString(), any(), anyString(), eq(777L)
        );
        verify(jdbcTemplate, never()).queryForObject(anyString(), eq(Long.class), any(Object[].class));
    }

    @Test
    public void testMemorizzaFatturaElettronica_NewRecord_InsertsNewRow() throws SQLException {
        FatturaElettronicaWrapperDto wrapper = new FatturaElettronicaWrapperDto();
        FatturaDto fattura = new FatturaDto();
        fattura.setId(999L);
        fattura.setNumDocumento(12345);
        fattura.setDataDocumento("26/06/2026");
        ClienteDto cliente = new ClienteDto();
        cliente.setDenominazione("Test Customer");
        fattura.setClienteDto(cliente);
        wrapper.setFattura(fattura);

        // Simulate that no record exists
        when(jdbcTemplate.queryForList(anyString(), eq(Long.class), anyString(), eq(999L), anyString()))
            .thenReturn(Collections.emptyList());

        // Simulate the insert query
        when(jdbcTemplate.queryForObject(
            anyString(), 
            eq(Long.class), 
            any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(888L);

        long id = dao.memorizzaFatturaElettronica("sd_justeat", wrapper);

        assertEquals(888L, id);
        // Verify that update was never called
        verify(jdbcTemplate, never()).update(anyString(), any(), any(), any(), any(), any(), any(), any());
    }
}
