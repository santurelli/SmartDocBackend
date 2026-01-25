package it.tinna.smartdoc.server.delegate.citta;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.citta.CittaDao;
import it.tinna.smartdoc.shared.dto.citta.CittaDto;

@Service
public class CittaDelegate {

    @Autowired
    private CittaDao cittaDao;

    public List<CittaDto> getSuggestion(String query) throws SQLException {
        return cittaDao.getSuggestion(query);
    }
}
