package it.tinna.smartdoc.server.delegate.movimentimagazzino;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import it.tinna.smartdoc.server.dao.prodotti.MovimentiMagazzinoDao;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;

/**
 * Unit tests for MovimentiMagazzinoDelegate.
 */
public class MovimentiMagazzinoDelegateTest {

    @InjectMocks
    private MovimentiMagazzinoDelegate movimentiMagazzinoDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Explicitly set the private field to be sure injection worked
        ReflectionTestUtils.setField(movimentiMagazzinoDelegate, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    public void testInsertRettifica_Carico() throws SQLException {
        MovimentoMagazzinoDto dto = new MovimentoMagazzinoDto();
        dto.setIdProdotto(101L);
        dto.setQuantita(15.0); // Target quantity
        
        // Mock current stock to 10.0 -> Delta = +5.0 (Carico)
        // Use doReturn and any() for the varargs array
        doReturn(10.0).when(jdbcTemplate).queryForObject(anyString(), eq(Double.class), any(Object[].class));

        try (MockedConstruction<MovimentiMagazzinoDao> mockedDao = mockConstruction(MovimentiMagazzinoDao.class)) {
            movimentiMagazzinoDelegate.insertRettifica(dto);
            
            MovimentiMagazzinoDao daoMock = mockedDao.constructed().get(0);
            verify(daoMock, times(1)).insertCarico(dto);
            assertEquals(5.0, dto.getQuantita(), 0.001);
            assertEquals("I", dto.getTipoMovimento());
        }
    }

    @Test
    public void testInsertRettifica_Scarico() throws SQLException {
        MovimentoMagazzinoDto dto = new MovimentoMagazzinoDto();
        dto.setIdProdotto(101L);
        dto.setQuantita(5.0); // Target quantity
        
        // Mock current stock to 10.0 -> Delta = -5.0 (Scarico)
        doReturn(10.0).when(jdbcTemplate).queryForObject(anyString(), eq(Double.class), any(Object[].class));

        try (MockedConstruction<MovimentiMagazzinoDao> mockedDao = mockConstruction(MovimentiMagazzinoDao.class)) {
            movimentiMagazzinoDelegate.insertRettifica(dto);
            
            MovimentiMagazzinoDao daoMock = mockedDao.constructed().get(0);
            verify(daoMock, times(1)).insertScarico(dto);
            assertEquals(5.0, dto.getQuantita(), 0.001);
            assertEquals("U", dto.getTipoMovimento());
        }
    }
}
