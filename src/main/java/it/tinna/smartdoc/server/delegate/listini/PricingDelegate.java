package it.tinna.smartdoc.server.delegate.listini;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.listini.ListiniDao;
import it.tinna.smartdoc.server.dao.prodotti.PrezziProdottiDao;
import it.tinna.smartdoc.server.dao.prodotti.ProdottiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.listini.ListinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;

@Service
public class PricingDelegate extends BaseDelegate {

    @Autowired
    private ListiniDao listiniDao;

    @Autowired
    private ProdottiDao prodottiDao;

    @Autowired
    private PrezziProdottiDao prezziProdottiDao;

    private static final int MAX_RECURSION_DEPTH = 10;

    /**
     * Calcola il prezzo di un prodotto per uno specifico listino seguendo la gerarchia.
     */
    public Double calculatePrice(Long idProdotto, Long idListino) throws SQLException {
        if (idProdotto == null) return 0.0;
        BigDecimal finalPrice = calculatePriceRecursive(idProdotto, idListino, 0);
        return finalPrice != null ? finalPrice.doubleValue() : 0.0;
    }

    private BigDecimal calculatePriceRecursive(Long idProdotto, Long idListino, int depth) throws SQLException {
        if (depth > MAX_RECURSION_DEPTH) {
            _log.error("Rilevato loop nella gerarchia dei listini per il prodotto " + idProdotto + " e listino " + idListino);
            return BigDecimal.ZERO;
        }

        if (idListino == null || idListino == 0) {
            // Se non specificato, proviamo il listino predefinito (assunto ID 1)
            idListino = 1L;
        }

        // 1. Verifica se esiste un PREZZO FISSO (OVERRIDE) per questo specifico (Prodotto, Listino)
        List<PrezzoProdottoDto> overrides = prezziProdottiDao.getByIdProdotto(idProdotto);
        for (PrezzoProdottoDto p : overrides) {
            if (p.getIdListino() != null && p.getIdListino().longValue() == idListino.longValue()) {
                if (p.getPrezzo() != null) {
                    return BigDecimal.valueOf(p.getPrezzo());
                }
            }
        }

        // 2. Se non c'è override, recuperiamo la definizione del listino
        ListinoDto listino = listiniDao.getById(idListino);
        if (listino == null) {
            _log.warn("Listino " + idListino + " non trovato. Ritorno 0.");
            return BigDecimal.ZERO;
        }

        // 3. Determiniamo il valore base (SORGENTE)
        BigDecimal sourceValue = BigDecimal.ZERO;
        String source = listino.getDerivationSource() != null ? listino.getDerivationSource() : "LISTINO";

        if ("LISTINO".equals(source)) {
            if (listino.getIdParent() != null && listino.getIdParent() != 0) {
                sourceValue = calculatePriceRecursive(idProdotto, listino.getIdParent(), depth + 1);
            } else if (idListino != 1L) {
                // Se è un listino "LISTINO" ma senza padre, ripieghiamo sul listino base (1)
                sourceValue = calculatePriceRecursive(idProdotto, 1L, depth + 1);
            }
        } else if ("ULTIMO_ACQUISTO".equals(source) || "MEDIO_ACQUISTO".equals(source)) {
            ProdottoDto prodotto = prodottiDao.getById(idProdotto);
            if (prodotto != null) {
                if ("ULTIMO_ACQUISTO".equals(source)) {
                    sourceValue = prodotto.getUltimoPrezzoAcquisto() != null ? BigDecimal.valueOf(prodotto.getUltimoPrezzoAcquisto()) : BigDecimal.ZERO;
                } else {
                    sourceValue = prodotto.getPrezzoMedioAcquisto() != null ? BigDecimal.valueOf(prodotto.getPrezzoMedioAcquisto()) : BigDecimal.ZERO;
                }
            }
        }

        // 4. Applichiamo la derivazione (Ricarico + Arrotondamento)
        return applyDerivation(sourceValue, listino);
    }

    private BigDecimal applyDerivation(BigDecimal base, ListinoDto listino) {
        if (base == null || base.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal result = base;
        String type = listino.getDerivationType() != null ? listino.getDerivationType() : "NONE";
        BigDecimal value = listino.getDerivationValue() != null ? listino.getDerivationValue() : BigDecimal.ZERO;

        if ("PERCENTAGE".equals(type)) {
            // result = base * (1 + value/100)
            BigDecimal multiplier = BigDecimal.ONE.add(value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            result = base.multiply(multiplier);
        } else if ("FIXED_MARKUP".equals(type)) {
            result = base.add(value);
        }

        // 5. Arrotondamento
        if (listino.getRoundingRule() != null && listino.getRoundingRule().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rule = listino.getRoundingRule();
            // Esempio: rule = 0.05. result = (round(result / 0.05)) * 0.05
            result = result.divide(rule, 0, RoundingMode.HALF_UP).multiply(rule);
        } else {
            // Default 2 decimali
            result = result.setScale(2, RoundingMode.HALF_UP);
        }

        return result;
    }
}
