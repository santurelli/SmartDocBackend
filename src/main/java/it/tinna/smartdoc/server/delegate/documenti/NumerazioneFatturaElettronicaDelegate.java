package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.NumerazioneFatturaElettronicaDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;

@Transactional(readOnly = true)
@Service(value = "numerazioneFatturaElettronicaDelegate")
public class NumerazioneFatturaElettronicaDelegate extends BaseDelegate
{

    // public NumerazioneFatturaElettronicaDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public String getNumero(int anno) throws SQLException
    {
        NumerazioneFatturaElettronicaDao dao = new NumerazioneFatturaElettronicaDao(jdbcTemplate);
        long id = dao.getNextIdNumero(anno);
        return StringUtils.leftPad("" + id, 10, "0");
    }

}

