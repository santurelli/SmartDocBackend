package it.tinna.smartdoc.shared.dto.movimentiprodotti;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class MovimentoProdottoDto extends BaseDto
{

    @Expose
    private String                      causale;

    @Expose
    private String                      clienteFornitore;

    @Expose
    private String                      codiceProdotto;

    @Expose
    private String                      dataMovimento;

    @Expose
    private String                      descrCalibro;

    @Expose
    private String                      descrFormato;

    @Expose
    private String                      descrizioneProdotto;

    @SuppressWarnings("unused")
    private String                      descrizioneProdottoExcel;

    @Expose
    private String                      descrScelta;

    @Expose
    private String                      descrTono;

    private String                      descUnitaMisuraBase;

    private String                      descUnitaMisuraMovimento;

    private List<DocumentoCollegatoDto> docCollegati = new ArrayList<DocumentoCollegatoDto>();

    private String                      idDocumento;

    private String                      idProdotto;

    private String                      idUnitaMisuraBase;

    private String                      idUnitaMisuraMovimento;

    @Expose
    private String                      numeroDocumento;

    @Expose
    private Double                      quantitaBase;

    private Double                      quantitaInArrivo;

    private Double                      quantitaMovimento;

    @Expose
    private String                      tipoMovimento;

    public String getDescrizioneProdottoExcel()
    {
        StringBuilder strB = new StringBuilder(getDescrizioneProdotto());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if ( request.getSession().getAttribute(ISharedConstants.CONFIG_KEY_TIPOSTORE).equals("CERAMICA") )
        {
            if ( StringUtils.isNotBlank(getDescrFormato()) || StringUtils.isNotBlank(getDescrScelta()) || StringUtils.isNotBlank(getDescrTono()) || StringUtils.isNotBlank(getDescrCalibro()) )
            {
                if ( StringUtils.isNotBlank(getDescrFormato()) )
                {
                    strB.append(" Formato: ").append(getDescrFormato());
                }
                if ( StringUtils.isNotBlank(getDescrScelta()) )
                {
                    strB.append(" Scelta: ").append(getDescrScelta());
                }
                if ( StringUtils.isNotBlank(getDescrTono()) )
                {
                    strB.append(" Tono: ").append(getDescrTono());
                }
                if ( StringUtils.isNotBlank(getDescrCalibro()) )
                {
                    strB.append(" Calibro: ").append(getDescrCalibro());
                }
            }
        }
        return strB.toString();
    }

}

