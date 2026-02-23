package it.tinna.smartdoc.shared.dto.documenti;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto.TipologiaIndirizzo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class DocumentoAcquistoDto extends DocumentoDto
{

    @Expose
    private String       dataDocumentoFornitore;

    @Expose
    private String       descFornitore;

    @Expose
    @SerializedName("fornitore")
    private FornitoreDto fornitoreDto;

    @Expose
    private long         idFornitore;

    @Expose
    private String       numeroDocumentoFornitore;

    public String getIntestazioneDocumento()
    {
        StringBuilder intestazioneDocumento = new StringBuilder("");
        intestazioneDocumento.append("<b>").append(fornitoreDto.getDenominazione()).append("</b><br>");
        if ( fornitoreDto.getElencoIndirizzi() != null && !fornitoreDto.getElencoIndirizzi().isEmpty() )
        {
            IndirizzoDto indirizzo = fornitoreDto.getElencoIndirizzi().get(0);
            for ( IndirizzoDto indirizzoDto : fornitoreDto.getElencoIndirizzi() )
            {
                if ( indirizzoDto.getTipologia().contentEquals(TipologiaIndirizzo.SEDE_OPERATIVA.getValore()) )
                {
                    indirizzo = indirizzoDto;
                    break;
                }
            }
            if ( StringUtils.isNotBlank(indirizzo.getIndirizzo()) )
            {
                intestazioneDocumento.append(indirizzo.getIndirizzo()).append("<br>");
            }
            if ( StringUtils.isNotBlank(indirizzo.getCap()) )
            {
                intestazioneDocumento.append(indirizzo.getCap());
                if ( StringUtils.isNotBlank(indirizzo.getCitta()) )
                {
                    intestazioneDocumento.append(" - ");
                }
                else if ( StringUtils.isNotBlank(indirizzo.getProvincia()) )
                {
                    intestazioneDocumento.append(" ");
                }
                else
                {
                    intestazioneDocumento.append("<br>");
                }
            }
            if ( StringUtils.isNotBlank(indirizzo.getCitta()) )
            {
                intestazioneDocumento.append(indirizzo.getCitta());
                if ( StringUtils.isNotBlank(indirizzo.getProvincia()) )
                {
                    intestazioneDocumento.append(" ");
                }
                else
                {
                    intestazioneDocumento.append("<br>");
                }
            }
            if ( StringUtils.isNotBlank(indirizzo.getProvincia()) )
            {
                intestazioneDocumento.append("(").append(indirizzo.getProvincia()).append(")").append("<br>");
            }
            if ( StringUtils.isNotBlank(indirizzo.getNazione()) )
            {
                intestazioneDocumento.append(indirizzo.getNazione());
            }
        }
        return intestazioneDocumento.toString();
    }

}

