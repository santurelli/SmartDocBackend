package it.tinna.smartdoc.server.dao.speseincasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.StringUtility;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;

public class SpeseIncassoDao extends BaseDao {

	public SpeseIncassoDao(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	public void delete(Integer idUser, Integer idSpesaIncasso) throws SQLException {
		try {
			jdbcTemplate.update(FileQueryReader.getQuery("SPESEINCASSO_D01"), idUser, idSpesaIncasso);
		}
		catch (DataAccessException e) {
			_log.error("Errore nella cancellazione della spesa di incasso {}", idSpesaIncasso, e);
			throw new SQLException(e);
		}
	}

	public SpesaIncassoDto getById(Integer id) throws SQLException {
		try {
			BeanPropertyRowMapper<SpesaIncassoDto> rowMapper = new BeanPropertyRowMapper<>();
			rowMapper.setMappedClass(SpesaIncassoDto.class);
			return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SPESEINCASSO_S02"), rowMapper, id);
		}
		catch (EmptyResultDataAccessException e) {
			_log.error("Nessuna spesa di incasso trovata con id {}", id);
			return null;
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero della spesa di incasso con id {}", id, e);
			throw new SQLException(e);
		}
	}

	public List<SpesaIncassoDto> getList(String descrizione) throws SQLException {
		try {
			BeanPropertyRowMapper<SpesaIncassoDto> rowMapper = new BeanPropertyRowMapper<>();
			rowMapper.setMappedClass(SpesaIncassoDto.class);
			return jdbcTemplate.query(	FileQueryReader.getQuery("SPESEINCASSO_S01"),
										rowMapper,
										StringUtils.isEmpty(descrizione) ? null
												: StringUtility.formatForLike(descrizione));
		}
		catch (EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
		catch (DataAccessException e) {
			_log.error("Errore nel recupero dell'elenco spese di incasso", e);
			throw new SQLException(e);
		}
	}

	public void insert(SpesaIncassoDto dto) throws SQLException {
		try {
			jdbcTemplate.update(FileQueryReader.getQuery("SPESEINCASSO_I01"),
								dto.getDescrizione(),
								dto.getIdAliquotaIva(),
								dto.getImporto(),
								dto.getTrasporto(),
								dto.getUserCreated());
		}
		catch (DataAccessException e) {
			_log.error("Errore nell'inserimento della spesa di incasso", e);
			throw new SQLException(e);
		}
	}

	public boolean isExistent(String descrizione, Integer id) throws SQLException {
		try {
			long l = jdbcTemplate.queryForObject(	FileQueryReader.getQuery("SPESEINCASSO_S03"),
													Long.class,
													descrizione,
													id);
			return l > 0;
		}
		catch (DataAccessException e) {
			_log.error(	"Errore nella verifica dell'esistenza della spesa di incasso con descrizione {}",
						descrizione,
						e);
			throw new SQLException(e);
		}
	}

	public void update(SpesaIncassoDto dto) throws SQLException {
		try {
			jdbcTemplate.update(FileQueryReader.getQuery("SPESEINCASSO_U01"),
								dto.getDescrizione(),
								dto.getIdAliquotaIva(),
								dto.getImporto(),
								dto.getTrasporto(),
								dto.getUserLastUpdate(),
								dto.getId());
		}
		catch (DataAccessException e) {
			_log.error("Errore nell'aggiornamento della spesa di incasso con id {}", dto.getId(), e);
			throw new SQLException(e);
		}
	}

}

