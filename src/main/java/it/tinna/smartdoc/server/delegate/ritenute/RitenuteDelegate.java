package it.tinna.smartdoc.server.delegate.ritenute;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.ritenute.RitenuteDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.ritenute.RitenutaFornitoreDto;

@Transactional(readOnly = true)
@Service("ritenuteDelegate")
public class RitenuteDelegate extends BaseDelegate {

    public List<RitenutaFornitoreDto> get770(int anno, Integer idFornitore) throws SQLException {
        return new RitenuteDao(getJdbcTemplate()).get770(anno, idFornitore);
    }

    public Map<String, Object> getCombos() {
        Map<String, String> tipiRitenuta = Map.of(
            "PERSONE_FISICHE",  "Ritenuta di acconto persone fisiche",
            "PERSONE_GIURIDICHE", "Ritenuta di acconto persone giuridiche"
        );

        Map<String, String> causaliPagamento = java.util.Arrays.stream(
            it.tinna.smartdoc.server.constants.CausalePagamentoEnum.values()
        ).collect(Collectors.toMap(
            Enum::name,
            it.tinna.smartdoc.server.constants.CausalePagamentoEnum::getDescrizione
        ));

        return Map.of(
            "tipiRitenuta", tipiRitenuta,
            "causaliPagamento", causaliPagamento
        );
    }
}
