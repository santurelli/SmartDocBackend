package it.tinna.smartdoc.shared.dto.documenti;

import it.tinna.smartdoc.shared.dto.BaseDto;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class SpesaIncassoDocumentoDto extends BaseDto {

    private AliquotaIvaDto aliquotaIvaDto;
    private boolean checked;
    private String descrizione;
    private long idFattura;
    private Integer idSpesaIncasso;
    private Integer idAliquotaIva;
    private Double importo;
    private String importoFormattato;
    private Double percIva;
    private Integer trasporto;
    private String tipo;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpesaIncassoDocumentoDto) {
            return ((SpesaIncassoDocumentoDto) obj).getId() == this.getId();
        }
        return false;
    }
}
