package it.tinna.smartdoc.server.delegate.ubicazioni;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.ubicazioni.UbicazioniDao;
import it.tinna.smartdoc.server.delegate.movimentimagazzino.MovimentiMagazzinoDelegate;
import it.tinna.smartdoc.server.util.MagazzinoUtility;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneProdottoDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UbicazioniDelegate {

    @Autowired
    private UbicazioniDao ubicazioniDao;

    @Autowired
    private MovimentiMagazzinoDelegate movimentiMagazzinoDelegate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UbicazioneDto> getList(String search, int length, int start) throws SQLException {
        return ubicazioniDao.getList(search, length, start);
    }

    public UbicazioneDto getById(long id) throws SQLException {
        return ubicazioniDao.getById(id);
    }

    public UbicazioneDto getByCodice(String codUbicazione) throws SQLException {
        return ubicazioniDao.getByCodice(codUbicazione);
    }

    @Transactional(rollbackFor = Exception.class)
    public long save(UbicazioneDto dto) throws SQLException {
        if (dto.getId() == 0) {
            return ubicazioniDao.insert(dto);
        } else {
            ubicazioniDao.update(dto);
            return dto.getId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, Long user) throws SQLException {
        ubicazioniDao.delete(id, user);
    }

    public List<UbicazioneProdottoDto> getUbicazioniByProdotto(long idProdotto) throws SQLException {
        return ubicazioniDao.getUbicazioniByProdotto(idProdotto);
    }

    public List<UbicazioneProdottoDto> getProdottiByUbicazione(long idUbicazione) throws SQLException {
        return ubicazioniDao.getProdottiByUbicazione(idUbicazione);
    }

    @Transactional(rollbackFor = Exception.class)
    public long saveProdottoUbicazione(UbicazioneProdottoDto dto) throws SQLException {
        if (dto.getId() == 0) {
            return ubicazioniDao.insertProdottoUbicazione(dto);
        } else {
            ubicazioniDao.updateProdottoUbicazione(dto);
            return dto.getId();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProdottoUbicazione(long id, Long user) throws SQLException {
        ubicazioniDao.deleteProdottoUbicazione(id, user);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> scaricaUbicazioniEGiacenza(long idProdotto, List<Map<String, Object>> itemsList, ProdottoDto prodotto, Long currentUid) throws SQLException {
        List<Map<String, Object>> createdMovs = new ArrayList<>();
        if (itemsList == null || itemsList.isEmpty()) {
            return createdMovs;
        }

        Integer idMagazzino = MagazzinoUtility.getMagazzinoPredefinito(jdbcTemplate);
        String dataMovimento = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        for (Map<String, Object> item : itemsList) {
            long idUbi = item.containsKey("idUbicazione") && item.get("idUbicazione") != null ? Long.parseLong(item.get("idUbicazione").toString()) : 0;
            double qta = item.containsKey("quantita") && item.get("quantita") != null ? Double.parseDouble(item.get("quantita").toString()) : 0.0;
            if (qta <= 0) continue;
            String dot = item.containsKey("dot") && item.get("dot") != null ? item.get("dot").toString().trim() : "";

            List<UbicazioneProdottoDto> prods = getUbicazioniByProdotto(idProdotto);
            for (UbicazioneProdottoDto up : prods) {
                if (up.getIdUbicazione() == idUbi && (up.getDot() != null ? up.getDot().trim() : "").equalsIgnoreCase(dot)) {
                    double actualDeduct = Math.min(up.getQuantita(), qta);
                    double newQta = Math.max(0.0, up.getQuantita() - qta);
                    if (newQta <= 0) {
                        deleteProdottoUbicazione(up.getId(), currentUid);
                    } else {
                        up.setQuantita(newQta);
                        up.setUserLastUpdate(currentUid);
                        saveProdottoUbicazione(up);
                    }

                    // Scalare anche la giacenza generale scrivendo il movimento di uscita in magazzino
                    if (actualDeduct > 0) {
                        MovimentoMagazzinoDto mov = new MovimentoMagazzinoDto();
                        mov.setIdProdotto(idProdotto);
                        mov.setTipoMovimento("U");
                        mov.setQuantita(actualDeduct);
                        if (prodotto != null) {
                            mov.setIdUnitaMisura(prodotto.getIdUnitaMisura1() != null && prodotto.getIdUnitaMisura1() != 0 ? prodotto.getIdUnitaMisura1() : null);
                            mov.setPrezzoUnitario(prodotto.getPrezzo() != null ? prodotto.getPrezzo() : 0.0);
                            mov.setIdAliquotaIva(prodotto.getIdAliquotaIva() != null && prodotto.getIdAliquotaIva() != 0 ? prodotto.getIdAliquotaIva() : null);
                        }
                        mov.setDataMovimento(dataMovimento);
                        mov.setIdMagazzino(idMagazzino);

                        String codUbi = up.getCodUbicazione() != null ? up.getCodUbicazione() : (item.containsKey("codUbicazione") && item.get("codUbicazione") != null ? item.get("codUbicazione").toString() : "");
                        String causale = "Prelievo da ubicazione " + codUbi + (!dot.isEmpty() ? " (DOT " + dot + ")" : "") + " [UBI:" + idUbi + (!dot.isEmpty() ? ":DOT:" + dot : "") + "]";
                        mov.setDescrCausale(causale);
                        mov.setUserCreated(currentUid);

                        Integer idMov = movimentiMagazzinoDelegate.insertScarico(mov);
                        log.info("Scalata giacenza generale per prodotto {} di qta {} da ubicazione {} (mov ID {})", idProdotto, actualDeduct, codUbi, idMov);

                        Map<String, Object> mInfo = new HashMap<>();
                        mInfo.put("idMovimento", idMov);
                        mInfo.put("idUbicazione", idUbi);
                        mInfo.put("codUbicazione", codUbi);
                        mInfo.put("dot", dot);
                        mInfo.put("quantita", actualDeduct);
                        createdMovs.add(mInfo);
                    }
                    break;
                }
            }
        }
        return createdMovs;
    }

    public List<Map<String, Object>> getPrelieviRecentiByProdotto(long idProdotto) {
        String sql = "SELECT m.k_d_e_movimenti_magazzino AS id, " +
                     "       m.quantita, " +
                     "       m.desc_causale AS causale, " +
                     "       to_char(m.data, 'DD/MM/YYYY') AS data, " +
                     "       to_char(COALESCE(m.dt_created, m.data), 'DD/MM/YYYY HH24:MI') AS dataOra, " +
                     "       COALESCE(u.username, '') AS operatore " +
                     "  FROM d_e_movimenti_magazzino m " +
                     "  LEFT JOIN d_e_utenti u ON u.k_d_e_utenti = m.user_created " +
                     " WHERE m.k_d_e_prodotti = ? " +
                     "   AND m.tipo_movimento = 'U' " +
                     "   AND m.desc_causale LIKE 'Prelievo da ubicazione%' " +
                     "   AND m.fl_deleted = 0 " +
                     " ORDER BY COALESCE(m.dt_created, m.data) DESC, m.k_d_e_movimenti_magazzino DESC " +
                     " LIMIT 20";
        return jdbcTemplate.queryForList(sql, idProdotto);
    }

    @Transactional(rollbackFor = Exception.class)
    public void annullaPrelievo(long idMovimento, Long currentUid) throws SQLException {
        String sqlMov = "SELECT k_d_e_movimenti_magazzino AS id, k_d_e_prodotti AS idProdotto, quantita, desc_causale AS descCausale, fl_deleted FROM d_e_movimenti_magazzino WHERE k_d_e_movimenti_magazzino = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlMov, idMovimento);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Movimento di prelievo non trovato con ID " + idMovimento);
        }
        Map<String, Object> mov = rows.get(0);
        int flDeleted = ((Number) mov.get("fl_deleted")).intValue();
        if (flDeleted == 1) {
            throw new IllegalStateException("Questo prelievo è già stato annullato in precedenza.");
        }
        long idProdotto = ((Number) mov.get("idprodotto")).longValue();
        double qta = ((Number) mov.get("quantita")).doubleValue();
        String descCausale = (String) mov.get("desccausale");

        long idUbi = 0;
        String dot = "";
        if (descCausale != null && descCausale.contains("[UBI:")) {
            try {
                int start = descCausale.indexOf("[UBI:") + 5;
                int end = descCausale.indexOf("]", start);
                String tag = descCausale.substring(start, end);
                if (tag.contains(":DOT:")) {
                    String[] parts = tag.split(":DOT:");
                    idUbi = Long.parseLong(parts[0]);
                    dot = parts.length > 1 ? parts[1].trim() : "";
                } else {
                    idUbi = Long.parseLong(tag);
                }
            } catch (Exception ex) {
                log.warn("Errore parsing tag ubicazione da causale: {}", descCausale, ex);
            }
        }

        if (idUbi == 0 && descCausale != null) {
            Matcher m = Pattern.compile("Prelievo da ubicazione\\s+([^\\s(]+)(?:\\s+\\(DOT\\s+([^)]+)\\))?").matcher(descCausale);
            if (m.find()) {
                String cod = m.group(1).trim();
                if (m.group(2) != null) {
                    dot = m.group(2).trim();
                }
                UbicazioneDto u = getByCodice(cod);
                if (u != null) {
                    idUbi = u.getId();
                }
            }
        }

        // 1. Soft-delete del movimento di scarico (la giacenza generale calcolata da get_totale_disponibile si ripristina automaticamente)
        String sqlSoftDelete = "UPDATE d_e_movimenti_magazzino SET fl_deleted = 1, user_last_update = ?, dt_last_update = CURRENT_TIMESTAMP WHERE k_d_e_movimenti_magazzino = ?";
        jdbcTemplate.update(sqlSoftDelete, currentUid != null ? currentUid.intValue() : 1, idMovimento);

        // 2. Ripristino quantità nell'ubicazione specifica (d_r_ubicazioni_prodotti)
        if (idUbi > 0) {
            String sqlCheckActive = "SELECT k_d_r_ubicazioni_prodotti, quantita FROM d_r_ubicazioni_prodotti WHERE k_d_e_prodotti = ? AND k_d_e_ubicazioni = ? AND COALESCE(TRIM(dot), '') = COALESCE(TRIM(?), '') AND fl_deleted = 0 LIMIT 1";
            List<Map<String, Object>> activeRows = jdbcTemplate.queryForList(sqlCheckActive, idProdotto, idUbi, dot);
            if (!activeRows.isEmpty()) {
                long idUp = ((Number) activeRows.get(0).get("k_d_r_ubicazioni_prodotti")).longValue();
                double currentUpQta = ((Number) activeRows.get(0).get("quantita")).doubleValue();
                String sqlUpdate = "UPDATE d_r_ubicazioni_prodotti SET quantita = ?, user_last_update = ?, dt_last_update = CURRENT_TIMESTAMP WHERE k_d_r_ubicazioni_prodotti = ?";
                jdbcTemplate.update(sqlUpdate, currentUpQta + qta, currentUid != null ? currentUid.toString() : "1", idUp);
            } else {
                String sqlCheckDeleted = "SELECT k_d_r_ubicazioni_prodotti FROM d_r_ubicazioni_prodotti WHERE k_d_e_prodotti = ? AND k_d_e_ubicazioni = ? AND COALESCE(TRIM(dot), '') = COALESCE(TRIM(?), '') AND fl_deleted = 1 ORDER BY dt_last_update DESC LIMIT 1";
                List<Map<String, Object>> deletedRows = jdbcTemplate.queryForList(sqlCheckDeleted, idProdotto, idUbi, dot);
                if (!deletedRows.isEmpty()) {
                    long idUp = ((Number) deletedRows.get(0).get("k_d_r_ubicazioni_prodotti")).longValue();
                    String sqlRestore = "UPDATE d_r_ubicazioni_prodotti SET fl_deleted = 0, quantita = ?, user_last_update = ?, dt_last_update = CURRENT_TIMESTAMP WHERE k_d_r_ubicazioni_prodotti = ?";
                    jdbcTemplate.update(sqlRestore, qta, currentUid != null ? currentUid.toString() : "1", idUp);
                } else {
                    UbicazioneProdottoDto newUp = new UbicazioneProdottoDto();
                    newUp.setIdProdotto(idProdotto);
                    newUp.setIdUbicazione(idUbi);
                    newUp.setDot(dot);
                    newUp.setQuantita(qta);
                    newUp.setUserCreated(currentUid != null ? currentUid : 1L);
                    saveProdottoUbicazione(newUp);
                }
            }
        }
        log.info("Prelievo movimento {} annullato con successo per prodotto {}: ripristinati {} pz", idMovimento, idProdotto, qta);
    }
}
