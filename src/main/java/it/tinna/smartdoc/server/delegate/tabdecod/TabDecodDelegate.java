package it.tinna.smartdoc.server.delegate.tabdecod;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.constants.CategorieTabDecod;
import it.tinna.smartdoc.server.dao.tabdecod.TabDecodDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.tabdecod.TabDecodDto;

@Service(value = "tabDecodDelegate")
public class TabDecodDelegate extends BaseDelegate
{

    // public TabDecodDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    public List<TabDecodDto> getListByCategoria(CategorieTabDecod categoria) throws SQLException
    {
        TabDecodDao dao = new TabDecodDao(jdbcTemplate);
        return dao.getListByCategoria(categoria);
    }

}

