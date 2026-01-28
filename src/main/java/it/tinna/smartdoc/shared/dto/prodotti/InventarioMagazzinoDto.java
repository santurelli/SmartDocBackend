package it.tinna.smartdoc.shared.dto.prodotti;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class InventarioMagazzinoDto extends BaseDto {
    private String codiceProdotto;
    private String descrizioneProdotto;
    private String descCategoria;
    private String descSottoCategoria;
    private String descrizioneFornitore;
    private String descunitaMisuraBase;
    private Double quantita;
    private Double valoreUnitario;
    
    private String descrFormato;
    private String descrScelta;
    private String descrTono;
    private String descrCalibro;

    public String getDescrizioneProdottoExcel() {
        StringBuilder strB = new StringBuilder(StringUtils.defaultString(getDescrizioneProdotto()));
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object tipoStore = request.getSession().getAttribute(ISharedConstants.CONFIG_KEY_TIPOSTORE);
            if ("CERAMICA".equals(tipoStore)) {
                if (StringUtils.isNotBlank(getDescrFormato()) || StringUtils.isNotBlank(getDescrScelta()) || StringUtils.isNotBlank(getDescrTono()) || StringUtils.isNotBlank(getDescrCalibro())) {
                    if (StringUtils.isNotBlank(getDescrFormato())) {
                        strB.append(" Formato: ").append(getDescrFormato());
                    }
                    if (StringUtils.isNotBlank(getDescrScelta())) {
                        strB.append(" Scelta: ").append(getDescrScelta());
                    }
                    if (StringUtils.isNotBlank(getDescrTono())) {
                        strB.append(" Tono: ").append(getDescrTono());
                    }
                    if (StringUtils.isNotBlank(getDescrCalibro())) {
                        strB.append(" Calibro: ").append(getDescrCalibro());
                    }
                }
            }
        }
        return strB.toString();
    }
}
