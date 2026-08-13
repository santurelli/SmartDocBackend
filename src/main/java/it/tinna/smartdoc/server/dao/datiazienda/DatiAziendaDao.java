package it.tinna.smartdoc.server.dao.datiazienda;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;

public class DatiAziendaDao extends BaseDao
{

    public DatiAziendaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public DatiAziendaDto getDatiAzienda() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DatiAziendaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DatiAziendaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DATIAZIENDA_S01"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei dati azienda", e);
            throw new SQLException(e);
        }
    }

    public byte[] getLogo() throws SQLException
    {
        return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DATIAZIENDA_S03"), new RowMapper<byte[]>()
        {
            @Override
            public byte[] mapRow(ResultSet rs,
                                 int rowNum) throws SQLException
            {
                return rs.getBytes(1);
            }

        });
    }

    public long getRowCount() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DATIAZIENDA_S02"), Long.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del numero di righe dei dati azienda", e);
            throw new SQLException(e);
        }
    }

    public void insert(DatiAziendaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DATIAZIENDA_I01"), dto.getDenominazione(), dto.getIndirizzo(), dto.getCap(), dto.getCitta(), dto.getProvincia(), dto.getTelefono(), dto.getEmail(), dto.getPec(), dto.getFax(), dto.getPartitaIva(), dto.getCodiceFiscale(), dto.getSitoWeb(), dto.getIndirizzoServerMail(), dto.getPortaServerMail(), dto.getUsernameServerMail(), dto.getPasswordServerMailCriptata(), dto.isSslMail(), dto.isTslMail(), dto.isServerProprietario(), dto.getIndirizzoServerPec(), dto.getPortaServerPec(), dto.getUsernameServerPec(), dto.getPasswordServerPecCriptata(), dto.isSslPec(), dto.isTslPec(), dto.getIdRegimeFiscale(), dto.getByteLogo(), dto.getSettoreMerceologico(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dei dati azienda", e);
            throw new SQLException(e);
        }
    }

    public void update(DatiAziendaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DATIAZIENDA_U01"), dto.getDenominazione(), dto.getIndirizzo(), dto.getCap(), dto.getCitta(), dto.getProvincia(), dto.getTelefono(), dto.getEmail(), dto.getPec(), dto.getFax(), dto.getPartitaIva(), dto.getCodiceFiscale(), dto.getSitoWeb(), dto.getIndirizzoServerMail(), dto.getPortaServerMail(), dto.getUsernameServerMail(), dto.getPasswordServerMailCriptata(), dto.isSslMail(), dto.isTslMail(), dto.isServerProprietario(), dto.getIndirizzoServerPec(), dto.getPortaServerPec(), dto.getUsernameServerPec(), dto.getPasswordServerPecCriptata(), dto.isSslPec(), dto.isTslPec(), dto.getIdRegimeFiscale(), dto.getByteLogo(), dto.getSettoreMerceologico(), dto.getUserLastUpdate());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dei dati azienda", e);
            throw new SQLException(e);
        }
    }

}

