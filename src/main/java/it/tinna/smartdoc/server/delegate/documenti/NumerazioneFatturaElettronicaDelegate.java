package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.documenti.NumerazioneFatturaElettronicaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;

@Service(value = "numerazioneFatturaElettronicaDelegate")
public class NumerazioneFatturaElettronicaDelegate extends BaseDelegate
{

    // public NumerazioneFatturaElettronicaDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    public String getNumero(int anno) throws SQLException
    {
        NumerazioneFatturaElettronicaDao dao = new NumerazioneFatturaElettronicaDao(jdbcTemplate);
        long id = dao.getNextIdNumero(anno);
        return StringUtils.leftPad("" + id, 10, "0");
    }

}

