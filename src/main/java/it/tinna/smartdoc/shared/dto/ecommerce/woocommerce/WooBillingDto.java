package it.tinna.smartdoc.shared.dto.ecommerce.woocommerce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WooBillingDto {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String company;
    private String email;
    private String phone;

    @JsonProperty("address_1")
    private String address1;

    private String city;
    private String postcode;
    private String state;
    private String country;

    public String getNominativo() {
        if (company != null && !company.isBlank()) {
            return company;
        }
        String nome = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        return nome.trim();
    }
}
