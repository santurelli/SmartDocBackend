package it.tinna.smartdoc.server.dao.documenti;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;

public class NumerazioneFatturaElettronicaDao extends BaseDao
{

    public NumerazioneFatturaElettronicaDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public long getNextIdNumero(final int anno) throws SQLException
    {
        try
        {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator()
            {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException
                {
                    PreparedStatement ps = con.prepareStatement(FileQueryReader.getQuery("NUMERAZIONEFATTURAELETTRONICA_I01"), Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, anno);
                    return ps;
                }
            }, holder);
            // return holder.getKey().longValue();
            return new Long(holder.getKeyList().get(0).get("numero").toString()).longValue();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del prossimo id nella tabella numerazionefatturaelettronica", e);
            throw new SQLException(e);
        }
    }

}

