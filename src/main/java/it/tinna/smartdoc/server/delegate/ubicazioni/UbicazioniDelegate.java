package it.tinna.smartdoc.server.delegate.ubicazioni;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
    public void scaricaUbicazioniEGiacenza(long idProdotto, List<Map<String, Object>> itemsList, ProdottoDto prodotto, Long currentUid) throws SQLException {
        if (itemsList == null || itemsList.isEmpty()) {
            return;
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
                            mov.setIdUnitaMisura(prodotto.getIdUnitaMisura1());
                            mov.setPrezzoUnitario(prodotto.getPrezzo() != null ? prodotto.getPrezzo() : 0.0);
                            mov.setIdAliquotaIva(prodotto.getIdAliquotaIva());
                        }
                        mov.setDataMovimento(dataMovimento);
                        mov.setIdMagazzino(idMagazzino);

                        String codUbi = up.getCodUbicazione() != null ? up.getCodUbicazione() : (item.containsKey("codUbicazione") && item.get("codUbicazione") != null ? item.get("codUbicazione").toString() : "");
                        String causale = "Prelievo da ubicazione " + codUbi;
                        if (!dot.isEmpty()) {
                            causale += " (DOT " + dot + ")";
                        }
                        mov.setDescrCausale(causale);
                        mov.setUserCreated(currentUid);

                        movimentiMagazzinoDelegate.insertScarico(mov);
                        log.info("Scalata giacenza generale per prodotto {} di qta {} da ubicazione {}", idProdotto, actualDeduct, codUbi);
                    }
                    break;
                }
            }
        }
    }
}
