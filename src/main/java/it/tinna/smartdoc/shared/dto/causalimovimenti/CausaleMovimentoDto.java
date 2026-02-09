package it.tinna.smartdoc.shared.dto.causalimovimenti;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class CausaleMovimentoDto extends BaseDto
{

    @Expose
    private String descrizione;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
