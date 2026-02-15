package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class CausaleTrasportoDto extends BaseDto {
    private String descrizione;
    private Integer predefinita;

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getPredefinita() {
        return predefinita;
    }

    public void setPredefinita(Integer predefinita) {
        this.predefinita = predefinita;
    }
}
