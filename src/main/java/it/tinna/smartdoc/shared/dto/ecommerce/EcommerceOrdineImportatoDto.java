package it.tinna.smartdoc.shared.dto.ecommerce;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
public class EcommerceOrdineImportatoDto extends BaseDto {

    @Expose
    private String idOrdineEsterno;
    @Expose
    private String numeroOrdineEsterno;
    @Expose
    private Integer idDdt;
    @Expose
    private Integer idFattura;
    @Expose
    private String dtImportazione;
    @Expose
    private String esito;
    @Expose
    private String dettaglioEsito;
}
