package it.tinna.smartdoc.server.delegate.contatti;

import java.util.List;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;

@Service(value = "contattiDelegate")
public class ContattiDelegate extends BaseDelegate {

    public List<ContattoDto> getListByIdRichiedente(String richiedente, Integer idRichiedente) throws Exception {
        ContattiDao dao = new ContattiDao(jdbcTemplate);
        return dao.getListByIdRichiedente(richiedente, idRichiedente);
    }

}

