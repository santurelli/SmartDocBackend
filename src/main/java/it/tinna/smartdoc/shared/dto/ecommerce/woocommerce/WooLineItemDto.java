package it.tinna.smartdoc.shared.dto.ecommerce.woocommerce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WooLineItemDto {

    private String sku;
    private String name;
    private Double quantity;
    private String total;

    @JsonProperty("total_tax")
    private String totalTax;
}
