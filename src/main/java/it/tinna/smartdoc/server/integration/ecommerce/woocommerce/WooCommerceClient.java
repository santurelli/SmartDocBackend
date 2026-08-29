package it.tinna.smartdoc.server.integration.ecommerce.woocommerce;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceConfigDto;
import it.tinna.smartdoc.shared.dto.ecommerce.woocommerce.WooOrderDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Client REST minimale per l'API WooCommerce (wp-json/wc/v3). Autenticazione via
 * Consumer Key/Secret generate dal cliente nel pannello WooCommerce (Basic Auth su HTTPS).
 */
@Component
@Slf4j
public class WooCommerceClient {

    private static final DateTimeFormatter WOO_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final int PER_PAGE = 50;

    @Autowired
    private RestTemplate restTemplate;

    public List<WooOrderDto> fetchOrdini(EcommerceConfigDto config, LocalDateTime after) {
        String baseUrl = normalizeUrl(config.getStoreUrl()) + "/wp-json/wc/v3/orders";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("status", config.getStatoOrdineWoo())
                .queryParam("per_page", PER_PAGE)
                .queryParam("orderby", "date")
                .queryParam("order", "asc");

        if (after != null) {
            builder.queryParam("after", after.format(WOO_DATE_FORMAT));
        }

        HttpHeaders headers = new HttpHeaders();
        String credenziali = config.getConsumerKey() + ":" + config.getConsumerSecret();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(credenziali.getBytes(StandardCharsets.UTF_8)));

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<WooOrderDto[]> response = restTemplate.exchange(
                builder.toUriString(), HttpMethod.GET, request, WooOrderDto[].class);

        WooOrderDto[] body = response.getBody();
        return body != null ? List.of(body) : List.of();
    }

    private String normalizeUrl(String storeUrl) {
        if (storeUrl == null) {
            return "";
        }
        String url = storeUrl.trim();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
