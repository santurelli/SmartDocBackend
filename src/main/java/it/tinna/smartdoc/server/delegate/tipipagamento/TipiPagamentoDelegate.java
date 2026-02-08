package it.tinna.smartdoc.server.delegate.tipipagamento;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Service
@CacheConfig(cacheNames = "tipipagamento", cacheResolver = "companyCacheResolver")
public class TipiPagamentoDelegate extends BaseDelegate {

    @Autowired
    private TipiPagamentoDao tipiPagamentoDao;

    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Long idUser, List<Long> ids) throws SQLException {
        for (Long id : ids) {
            tipiPagamentoDao.deleteScadenze(id);
            tipiPagamentoDao.delete(idUser, id);
        }
    }

    public TipoPagamentoDto getById(long id) throws SQLException {
        TipoPagamentoDto dto = tipiPagamentoDao.getById(id);
        if (dto != null) {
            dto.setScadenze(tipiPagamentoDao.getScadenze(id));
        }
        return dto;
    }

    public List<TipoPagamentoDto> getList(String strToSearch, Integer length, Integer start, Integer orderColumn, String orderDir) throws SQLException {
        return tipiPagamentoDao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    @Cacheable
    public List<TipoPagamentoDto> getListForCombo() throws SQLException {
        return tipiPagamentoDao.getListForCombo();
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void insert(TipoPagamentoDto dto, Long userId) throws SQLException {
        if (dto.getPredefinito() != null && dto.getPredefinito() == 1) {
            tipiPagamentoDao.resetPredefinite(userId);
        }
        long id = tipiPagamentoDao.insert(dto, userId);
        dto.setId(id);
        if (dto.getScadenze() != null) {
            for (ScadenzaPagamentoDto s : dto.getScadenze()) {
                s.setIdTipoPagamento(id);
                tipiPagamentoDao.insertScadenza(s);
            }
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void update(TipoPagamentoDto dto, Long userId) throws SQLException {
        if (dto.getPredefinito() != null && dto.getPredefinito() == 1) {
            tipiPagamentoDao.resetPredefinite(userId);
        }
        tipiPagamentoDao.update(dto, userId);
        tipiPagamentoDao.deleteScadenze(dto.getId());
        if (dto.getScadenze() != null) {
            for (ScadenzaPagamentoDto s : dto.getScadenze()) {
                s.setIdTipoPagamento(dto.getId());
                tipiPagamentoDao.insertScadenza(s);
            }
        }
    }

    public boolean isExistent(String descrizione, long id) throws SQLException {
        return tipiPagamentoDao.isExistent(descrizione, id);
    }
}
