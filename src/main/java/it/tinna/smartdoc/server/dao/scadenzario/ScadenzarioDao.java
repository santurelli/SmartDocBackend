package it.tinna.smartdoc.server.dao.scadenzario;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzaDto;

public class ScadenzarioDao extends BaseDao {

	public ScadenzarioDao(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	public void delete(Integer id, Integer idUtente) throws SQLException {
		try {
			jdbcTemplate.update(FileQueryReader.getQuery("SCADENZE_D01"), idUtente, id);
		}
		catch (DataAccessException e) {
			_log.error("Errore nella cancellazione della scadenza {}", id, e);
			throw new SQLException(e);
		}
	}

	public List<ScadenzaDto> getList(Integer giorni, String soggetto, String tipoPagamento) throws SQLException {
		String query = FileQueryReader.getQuery("SCADENZE_S01");
		ArrayList<Object> params = new ArrayList<>();
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("RICERCA", "");
		if (giorni != null) {
			if (giorni == -1) {
				// tutte le scadute
				valuesMap.put("RICERCA", "AND data - CURRENT_DATE < 0  AND dt_esecuzione IS NULL");
			}
			else if (giorni == 0) {
				// in scadenza entro fine mese
				valuesMap.put(	"RICERCA",
								new StringBuilder("AND dt_esecuzione IS NULL AND data <= get_fine_mese(current_date) AND data >= current_date").toString());
			}
			else {
				// in scadenza nei prossimi x giorni
				valuesMap.put(	"RICERCA",
								new StringBuilder("AND dt_esecuzione IS NULL AND (data - CURRENT_DATE <= ? AND data - CURRENT_DATE >= 0)").toString());
				params.add(giorni);
				params.add(giorni);
			}
		}
		if (tipoPagamento != null) {
			// in questo modo viene annullata la query su
			valuesMap.put("FL_TIPO_PAGAMENTO", "AND 1=0");
		}
		else {
			valuesMap.put("FL_TIPO_PAGAMENTO", "");
		}
		params.add(StringUtils.isEmpty(soggetto) ? null : soggetto);
		params.add(StringUtils.isEmpty(tipoPagamento) ? null : tipoPagamento);
		query = StrSubstitutor.replace(query, valuesMap);
		try {
			BeanPropertyRowMapper<ScadenzaDto> rowMapper = new BeanPropertyRowMapper<>();
			rowMapper.setMappedClass(ScadenzaDto.class);
			return jdbcTemplate.query(query, rowMapper, params.toArray());
		}
		catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero dell'elenco scadenze", e);
			throw new SQLException(e);
		}
	}

	public List<ScadenzaDto> getListScadenzeClienti(Integer idCliente, String tipoDocumento, Integer idScadenza)
			throws SQLException {
		try {
			BeanPropertyRowMapper<ScadenzaDto> rowMapper = new BeanPropertyRowMapper<>();
			rowMapper.setMappedClass(ScadenzaDto.class);
			return jdbcTemplate.query(	FileQueryReader.getQuery("SCADENZE_S04"),
										rowMapper,
										idCliente,
										idScadenza,
										tipoDocumento);
		}
		catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero delle scadenze per il cliente {}", idCliente, e);
			throw new SQLException(e);
		}
	}

	public List<ScadenzaDto> getListScadenzeFornitori(Integer idFornitore, String tipoDocumento, Integer idScadenza)
			throws SQLException {
		try {
			BeanPropertyRowMapper<ScadenzaDto> rowMapper = new BeanPropertyRowMapper<>();
			rowMapper.setMappedClass(ScadenzaDto.class);
			return jdbcTemplate.query(	FileQueryReader.getQuery("SCADENZE_S05"),
										rowMapper,
										idFornitore,
										idScadenza,
										tipoDocumento);
		}
		catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero delle scadenze per il fornitore {}", idFornitore, e);
			throw new SQLException(e);
		}
	}

	public ScadenzaDto getById(Integer id) throws SQLException {
		try {
			BeanPropertyRowMapper<ScadenzaDto> rowMapper = new BeanPropertyRowMapper<>();
			rowMapper.setMappedClass(ScadenzaDto.class);
			return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCADENZE_S02"), rowMapper, id);
		}
		catch (EmptyResultDataAccessException e) {
			_log.error("Nessuna scadenza trovata con id {}", id);
			return null;
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero della scadenza con id {}", id, e);
			throw new SQLException(e);
		}
	}

	// public List<ItemSuggestion> getSoggettoSuggestion(String query) throws
	// Exception {
	// QueryRunner qRunner = new QueryRunner();
	// return qRunner.query( conn,
	// FileQueryReader.getQuery("SCADENZE_S03"),
	// new TrimmedBeanListHandler<ItemSuggestion>(ItemSuggestion.class),
	// new Object[] { StringUtility.formatForLike(query.toLowerCase()),
	// StringUtility.formatForLike(query.toLowerCase()) });
	// }

	public BigDecimal getTotaleDaPagareFineMese() throws SQLException {
		try {
			return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCADENZE_S07"), BigDecimal.class);
		}
		catch (EmptyResultDataAccessException e) {
			return BigDecimal.ZERO;
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero del totale da pagare entro fine mese", e);
			throw new SQLException(e);
		}
	}

	public BigDecimal getTotaleDaRicevereFineMese() throws SQLException {
		try {
			return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCADENZE_S06"), BigDecimal.class);
		}
		catch (EmptyResultDataAccessException e) {
			return BigDecimal.ZERO;
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero del totale da ricevere entro fine mese", e);
			throw new SQLException(e);
		}
	}

	public void insert(ScadenzaDto dto) throws SQLException {
		try {
			jdbcTemplate.update(FileQueryReader.getQuery("SCADENZE_I01"),
								dto.getDataScadenza(),
								dto.getDescrizione().trim(),
								StringUtils.isEmpty(dto.getNote()) ? null : dto.getNote().trim(),
								StringUtils.isEmpty(dto.getDataEsecuzione()) ? null : dto.getDataEsecuzione().trim(),
								dto.getUserCreated());
		}
		catch (DataAccessException e) {
			_log.error("Errore nell'inserimento della scadenza", e);
			throw new SQLException(e);
		}
	}

	public void update(ScadenzaDto dto) throws SQLException {
		try {
			jdbcTemplate.update(FileQueryReader.getQuery("SCADENZE_U01"),
								dto.getDataScadenza(),
								dto.getDescrizione().trim(),
								StringUtils.isEmpty(dto.getNote()) ? null : dto.getNote().trim(),
								StringUtils.isEmpty(dto.getDataEsecuzione()) ? null : dto.getDataEsecuzione().trim(),
								dto.getUserLastUpdate(),
								dto.getId());
		}
		catch (DataAccessException e) {
			_log.error("Errore nell'aggiornamento della scadenza {}", dto.getId(), e);
			throw new SQLException(e);
		}
	}

}

