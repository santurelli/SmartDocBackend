package it.tinna.smartdoc.shared.dto.clienti;

// import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class ClienteDto extends BaseClienteDto {

    // @Expose
    private String descAgente;

    // @Expose
    private Integer idAgente;

    // @Expose
    private Integer idListino;

    // @Expose
    private String sconto;

    // @Expose
    private Integer idZonaCompetenza;

    private Integer idSottoconto;

    // @Expose
    private Integer idLingua;

    // @Expose
    private String ultimoDocVendita;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClienteDto) {
            return ((ClienteDto) obj).getId() == this.getId();
        }
        return false;
    }

}
