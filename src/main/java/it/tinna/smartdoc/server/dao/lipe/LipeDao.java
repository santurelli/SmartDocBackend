package it.tinna.smartdoc.server.dao.lipe;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.constants.Periodi;
import it.tinna.smartdoc.shared.dto.lipe.LipeAggregateDto;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LipeDao extends BaseDao {

    private static final Logger log = LoggerFactory.getLogger(LipeDao.class);

    public LipeDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public LipeAggregateDto getAggregatoTrimestrale(int anno, Periodi trimestre) throws SQLException {
        String query = FileQueryReader.getQuery("LIPE_S01");
        Map<String, String> valuesMap = new HashMap<>();
        List<Object> params = new ArrayList<>();

        // Parametro anno
        params.add(anno);

        // Filtro periodo (solo trimestrali per LIPE)
        String dataDa;
        String dataA;
        switch (trimestre) {
            case PRIMO_TIMESTRE  -> { dataDa = "01/" + anno; dataA = "03/" + anno; }
            case SECONDO_TIMESTRE -> { dataDa = "04/" + anno; dataA = "06/" + anno; }
            case TERZO_TRIMESTRE -> { dataDa = "07/" + anno; dataA = "09/" + anno; }
            case QUARTO_TRIMESTRE -> { dataDa = "10/" + anno; dataA = "12/" + anno; }
            default -> { dataDa = null; dataA = null; }
        }

        if (dataDa != null) {
            params.add(dataDa);
            params.add(dataA);
            valuesMap.put("WHERE_PERIODO",
                "AND date_trunc('month', data_iva) >= to_date(?, 'MM/YYYY') " +
                "AND date_trunc('month', data_iva) <= to_date(?, 'MM/YYYY') ");
        } else {
            valuesMap.put("WHERE_PERIODO", "");
        }

        query = StrSubstitutor.replace(query, valuesMap);

        try {
            return jdbcTemplate.query(query, rs -> {
                LipeAggregateDto dto = new LipeAggregateDto();
                if (rs.next()) {
                    dto.setImponibileVendite(rs.getBigDecimal("imponibile_vendite"));
                    dto.setIvaEsigibile(rs.getBigDecimal("iva_esigibile"));
                    dto.setImponibileAcquisti(rs.getBigDecimal("imponibile_acquisti"));
                    dto.setIvaDetratta(rs.getBigDecimal("iva_detratta"));
                }
                return dto;
            }, params.toArray());
        } catch (DataAccessException e) {
            log.error("Errore nel calcolo aggregato LIPE per anno={} trimestre={}", anno, trimestre, e);
            throw new SQLException(e);
        }
    }
}
