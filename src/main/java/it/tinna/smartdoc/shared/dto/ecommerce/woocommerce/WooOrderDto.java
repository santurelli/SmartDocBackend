package it.tinna.smartdoc.shared.dto.ecommerce.woocommerce;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Mappatura minimale della risposta REST di WooCommerce per un ordine
 * (endpoint GET /wp-json/wc/v3/orders). Solo i campi usati dall'importazione.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WooOrderDto {

    private Long id;
    private String number;
    private String status;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("currency")
    private String currency;

    private String total;

    @JsonProperty("total_tax")
    private String totalTax;

    private WooBillingDto billing;

    @JsonProperty("line_items")
    private List<WooLineItemDto> lineItems;
}
