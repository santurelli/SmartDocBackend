package it.tinna.smartdoc.server.delegate.ubicazioni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import it.tinna.smartdoc.server.dao.ubicazioni.UbicazioniDao;
import it.tinna.smartdoc.server.delegate.movimentimagazzino.MovimentiMagazzinoDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneProdottoDto;

public class UbicazioniDelegateTest {

    @InjectMocks
    private UbicazioniDelegate ubicazioniDelegate;

    @Mock
    private UbicazioniDao ubicazioniDao;

    @Mock
    private MovimentiMagazzinoDelegate movimentiMagazzinoDelegate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(ubicazioniDelegate, "ubicazioniDao", ubicazioniDao);
        ReflectionTestUtils.setField(ubicazioniDelegate, "movimentiMagazzinoDelegate", movimentiMagazzinoDelegate);
        ReflectionTestUtils.setField(ubicazioniDelegate, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    public void testScaricaUbicazioniEGiacenza_ScalataParziale() throws SQLException {
        long idProdotto = 50L;
        long idUbi = 10L;
        String dot = "2423";

        // Prodotto nel db ha 4 pezzi in ubicazione 10
        UbicazioneProdottoDto up = new UbicazioneProdottoDto();
        up.setId(1L);
        up.setIdProdotto(idProdotto);
        up.setIdUbicazione(idUbi);
        up.setCodUbicazione("A-01");
        up.setDot(dot);
        up.setQuantita(4.0);

        List<UbicazioneProdottoDto> prods = new ArrayList<>();
        prods.add(up);
        when(ubicazioniDao.getUbicazioniByProdotto(idProdotto)).thenReturn(prods);

        // Mock magazzino predefinito
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        ProdottoDto prodotto = new ProdottoDto();
        prodotto.setId(idProdotto);
        prodotto.setCodice("TYRE01");
        prodotto.setPrezzo(85.0);

        // Prelievo di 2 pezzi
        List<Map<String, Object>> itemsList = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("idUbicazione", idUbi);
        item.put("dot", dot);
        item.put("quantita", 2.0);
        item.put("codUbicazione", "A-01");
        itemsList.add(item);

        ubicazioniDelegate.scaricaUbicazioniEGiacenza(idProdotto, itemsList, prodotto, 1L);

        // Verifica aggiornamento ubicazione (4 - 2 = 2 rimasti)
        ArgumentCaptor<UbicazioneProdottoDto> upCaptor = ArgumentCaptor.forClass(UbicazioneProdottoDto.class);
        verify(ubicazioniDao, times(1)).updateProdottoUbicazione(upCaptor.capture());
        assertEquals(2.0, upCaptor.getValue().getQuantita(), 0.001);

        // Verifica inserimento movimento di scarico (tipo U, quantità 2.0) per scalare giacenza generale
        ArgumentCaptor<MovimentoMagazzinoDto> movCaptor = ArgumentCaptor.forClass(MovimentoMagazzinoDto.class);
        verify(movimentiMagazzinoDelegate, times(1)).insertScarico(movCaptor.capture());
        MovimentoMagazzinoDto mov = movCaptor.getValue();
        assertEquals(idProdotto, mov.getIdProdotto());
        assertEquals("U", mov.getTipoMovimento());
        assertEquals(2.0, mov.getQuantita(), 0.001);
        assertEquals(85.0, mov.getPrezzoUnitario(), 0.001);
    }

    @Test
    public void testScaricaUbicazioniEGiacenza_ScalataTotale() throws SQLException {
        long idProdotto = 50L;
        long idUbi = 10L;
        String dot = "1022";

        UbicazioneProdottoDto up = new UbicazioneProdottoDto();
        up.setId(2L);
        up.setIdProdotto(idProdotto);
        up.setIdUbicazione(idUbi);
        up.setCodUbicazione("B-03");
        up.setDot(dot);
        up.setQuantita(4.0);

        List<UbicazioneProdottoDto> prods = new ArrayList<>();
        prods.add(up);
        when(ubicazioniDao.getUbicazioniByProdotto(idProdotto)).thenReturn(prods);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);

        ProdottoDto prodotto = new ProdottoDto();
        prodotto.setId(idProdotto);

        // Prelievo di tutti i 4 pezzi
        List<Map<String, Object>> itemsList = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("idUbicazione", idUbi);
        item.put("dot", dot);
        item.put("quantita", 4.0);
        itemsList.add(item);

        ubicazioniDelegate.scaricaUbicazioniEGiacenza(idProdotto, itemsList, prodotto, 1L);

        // Se quantita scende a 0, la riga dell'ubicazione viene cancellata
        verify(ubicazioniDao, times(1)).deleteProdottoUbicazione(2L, 1L);

        // E viene inserito scarico di 4.0 per la giacenza generale
        ArgumentCaptor<MovimentoMagazzinoDto> movCaptor = ArgumentCaptor.forClass(MovimentoMagazzinoDto.class);
        verify(movimentiMagazzinoDelegate, times(1)).insertScarico(movCaptor.capture());
        assertEquals(4.0, movCaptor.getValue().getQuantita(), 0.001);
        assertEquals("U", movCaptor.getValue().getTipoMovimento());
    }
}
