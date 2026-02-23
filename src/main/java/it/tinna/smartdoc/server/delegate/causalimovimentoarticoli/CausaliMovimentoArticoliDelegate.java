package it.tinna.smartdoc.server.delegate.causalimovimentoarticoli;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.causalimovimentoarticoli.CausaliMovimentoArticoliDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.causalimovimenti.CausaleMovimentoDto;

@Service
public class CausaliMovimentoArticoliDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void delete(long idUser, List<Long> list) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        for (Long id : list) {
            dao.delete(idUser, id);
        }
    }

    public CausaleMovimentoDto getById(long id) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<CausaleMovimentoDto> getList(String strToSearch, int length, int start, int orderColumn, String orderDir) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        return dao.getList(strToSearch, length, start, orderColumn, orderDir);
    }

    public List<CausaleMovimentoDto> getSuggestion(String q) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        return dao.getSuggestion(q);
    }

    public void insert(CausaleMovimentoDto dto) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        dao.insert(dto);
    }

    public boolean isExistent(String descrizione, long id) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public void update(CausaleMovimentoDto dto) throws SQLException {
        CausaliMovimentoArticoliDao dao = new CausaliMovimentoArticoliDao(jdbcTemplate);
        dao.update(dto);
    }
}

