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
public class EcommerceConfigDto extends BaseDto {

    @Expose
    private String piattaforma = "WOOCOMMERCE";
    @Expose
    private String storeUrl;
    @Expose
    private String consumerKey;
    @Expose
    private String consumerSecret;
    @Expose
    private String statoOrdineWoo = "processing";
    @Expose
    private Integer flAbilitato = 0;
    @Expose
    private Integer flCreaDdt = 1;
    @Expose
    private Integer flCreaFattura = 1;
    @Expose
    private Integer intervalloMinuti = 15;
    @Expose
    private String dtUltimoSync;
    @Expose
    private String ultimoEsito;
}
