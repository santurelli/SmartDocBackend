package it.tinna.smartdoc.server.dao.documenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.documenti.CausaleEsigibilitaDifferitaDto;

@Repository
public class CausaliEsigibilitaDifferitaDao extends BaseDao {

    @Autowired
    public CausaliEsigibilitaDifferitaDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<CausaleEsigibilitaDifferitaDto> getList(String search, Integer length, Integer start, Integer orderCol, String orderDir) throws SQLException {
        String query = FileQueryReader.getQuery("CAUSALIESIGIBILITA_S02");
        String orderBy = "";
        if (orderCol != null) {
            String colName = "descrizione";
            orderBy = " ORDER BY " + colName + " " + orderDir;
        }
        query = query.replace("${ORDER_BY}", orderBy);
        query = query.replace("${LIMIT}", " LIMIT " + length + " OFFSET " + start);

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(CausaleEsigibilitaDifferitaDto.class), "%" + search + "%");
    }

    public List<CausaleEsigibilitaDifferitaDto> getListForCombo() throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("CAUSALIESIGIBILITA_S01"), new BeanPropertyRowMapper<>(CausaleEsigibilitaDifferitaDto.class));
    }

    public void insert(CausaleEsigibilitaDifferitaDto dto, Integer userId) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("CAUSALIESIGIBILITA_I01"),
                dto.getDescrizione(), userId);
    }

    public void update(CausaleEsigibilitaDifferitaDto dto, Integer userId) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("CAUSALIESIGIBILITA_U01"),
                dto.getDescrizione(), userId, dto.getId());
    }

    public void delete(Integer id, Integer userId) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("CAUSALIESIGIBILITA_D01"), userId, id);
    }
}
