package it.tinna.smartdoc.server.delegate.listini;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.tinna.smartdoc.server.dao.listini.ListiniDao;
import it.tinna.smartdoc.server.dao.prodotti.PrezziProdottiDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.shared.dto.listini.ListinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;

/**
 * Unit tests for the new PricingDelegate logic.
 */
public class PricingDelegateTest {

    @InjectMocks
    private PricingDelegate pricingDelegate;

    @Mock
    private ListiniDao listiniDao;

    @Mock
    private ProdottiDao prodottiDao;

    @Mock
    private PrezziProdottiDao prezziProdottiDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculatePrice_DirectOverride() throws SQLException {
        Long productId = 100L;
        Long listId = 2L;
        
        List<PrezzoProdottoDto> overrides = new ArrayList<>();
        PrezzoProdottoDto p = new PrezzoProdottoDto();
        p.setIdListino(listId.intValue());
        p.setPrezzo(50.0);
        overrides.add(p);
        
        when(prezziProdottiDao.getByIdProdotto(productId)).thenReturn(overrides);
        
        Double result = pricingDelegate.calculatePrice(productId, listId);
        assertEquals(50.0, result);
    }

    @Test
    public void testCalculatePrice_DerivationFromParent() throws SQLException {
        Long productId = 100L;
        Long listIdBase = 1L;
        Long listIdDerived = 2L;
        
        // Base Listino (Manual price)
        ListinoDto baseList = new ListinoDto();
        baseList.setId(listIdBase);
        baseList.setDerivationType("NONE");
        
        // Derived Listino (+10%)
        ListinoDto derivedList = new ListinoDto();
        derivedList.setId(listIdDerived);
        derivedList.setIdParent(listIdBase);
        derivedList.setDerivationSource("LISTINO");
        derivedList.setDerivationType("PERCENTAGE");
        derivedList.setDerivationValue(BigDecimal.valueOf(10.0));
        
        when(listiniDao.getById(listIdBase)).thenReturn(baseList);
        when(listiniDao.getById(listIdDerived)).thenReturn(derivedList);
        
        // Prezzo sul base
        List<PrezzoProdottoDto> baseOverrides = new ArrayList<>();
        PrezzoProdottoDto pBase = new PrezzoProdottoDto();
        pBase.setIdListino(listIdBase.intValue());
        pBase.setPrezzo(100.0);
        baseOverrides.add(pBase);
        
        when(prezziProdottiDao.getByIdProdotto(productId)).thenReturn(baseOverrides);
        
        // 100 + 10% = 110
        Double result = pricingDelegate.calculatePrice(productId, listIdDerived);
        assertEquals(110.0, result);
    }

    @Test
    public void testCalculatePrice_FromPurchasePrice() throws SQLException {
        Long productId = 100L;
        Long listId = 3L;
        
        ListinoDto list = new ListinoDto();
        list.setId(listId);
        list.setDerivationSource("ULTIMO_ACQUISTO");
        list.setDerivationType("PERCENTAGE");
        list.setDerivationValue(BigDecimal.valueOf(50.0)); // +50% ricarico
        list.setRoundingRule(BigDecimal.valueOf(0.05)); // Arrotonda a 0.05
        
        when(listiniDao.getById(listId)).thenReturn(list);
        
        ProdottoDto prodotto = new ProdottoDto();
        prodotto.setId(productId);
        prodotto.setUltimoPrezzoAcquisto(10.0);
        
        when(prodottiDao.getById(productId)).thenReturn(prodotto);
        when(prezziProdottiDao.getByIdProdotto(productId)).thenReturn(new ArrayList<>());
        
        // 10.0 + 50% = 15.00
        Double result = pricingDelegate.calculatePrice(productId, listId);
        assertEquals(15.00, result);
        
        // Test with rounding: 10.33 + 50% = 15.495 -> Round 0.05 -> 15.50
        prodotto.setUltimoPrezzoAcquisto(10.33);
        result = pricingDelegate.calculatePrice(productId, listId);
        assertEquals(15.50, result);
    }

    @Test
    public void testCalculatePrice_FallbackToBase() throws SQLException {
        Long productId = 100L;
        Long listIdMissing = 99L;
        
        // Listino base (1)
        ListinoDto baseList = new ListinoDto();
        baseList.setId(1L);
        baseList.setDerivationType("NONE");
        
        when(listiniDao.getById(1L)).thenReturn(baseList);
        
        // Price on base
        List<PrezzoProdottoDto> baseOverrides = new ArrayList<>();
        PrezzoProdottoDto pBase = new PrezzoProdottoDto();
        pBase.setIdListino(1);
        pBase.setPrezzo(123.45);
        baseOverrides.add(pBase);
        
        when(prezziProdottiDao.getByIdProdotto(productId)).thenReturn(baseOverrides);
        
        // Si aspetta il prezzo del listino 1 come fallback se passiamo 0 o null
        Double result = pricingDelegate.calculatePrice(productId, null);
        assertEquals(123.45, result);
    }
}
