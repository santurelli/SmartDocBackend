package it.tinna.smartdoc.server.delegate.ubicazioni;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.ubicazioni.UbicazioniDao;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneDto;
import it.tinna.smartdoc.shared.dto.ubicazioni.UbicazioneProdottoDto;

@Service
public class UbicazioniDelegate {

    @Autowired
    private UbicazioniDao ubicazioniDao;

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
}
