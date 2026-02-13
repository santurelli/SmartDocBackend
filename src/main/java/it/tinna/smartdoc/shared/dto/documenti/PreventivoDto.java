package it.tinna.smartdoc.shared.dto.documenti;

import java.math.BigDecimal;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.agenti.AgenteDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.progetti.ProgettoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class PreventivoDto extends DocumentoDto {

    @Expose
    private String descTipoPagamento;

}
