package it.tinna.smartdoc.server.delegate.indirizzi;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

@Transactional(readOnly = true)
@Service(value = "indirizziDelegate")
public class IndirizziDelegate extends BaseDelegate {

    public List<IndirizzoDto> getListByIdRichiedente(String richiedente, Integer idRichiedente) throws Exception {
        IndirizziDao dao = new IndirizziDao(jdbcTemplate);
        return dao.getListByIdRichiedente(richiedente, idRichiedente);
    }

}

