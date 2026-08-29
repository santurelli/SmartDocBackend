package it.tinna.smartdoc.server.integration.ai;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.tinna.smartdoc.shared.dto.ai.EstrazioneFatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.ai.EstrazioneRigaDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Estrazione dati da fattura fornitore (immagine o PDF) via OpenAI vision, con output
 * strutturato (JSON Schema). Funzionalita' riservata al piano Enterprise: il controllo del
 * piano NON e' fatto qui ma nel controller chiamante, questo servizio si limita all'estrazione.
 */
@Service
@Slf4j
public class OpenAiDocumentExtractionService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EstrazioneFatturaFornitoreDto estrai(MultipartFile file) throws IOException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Chiave API OpenAI non configurata (OPENAI_API_KEY)");
        }

        String base64Png = convertiInPngBase64(file);

        Map<String, Object> body = buildRequestBody(base64Png);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String rawResponse;
        try {
            rawResponse = restTemplate.postForObject(OPENAI_URL, request, String.class);
        } catch (Exception e) {
            log.error("Errore nella chiamata a OpenAI per l'estrazione documento", e);
            EstrazioneFatturaFornitoreDto errore = new EstrazioneFatturaFornitoreDto();
            errore.setErroreEstrazione("Errore nella comunicazione con il servizio AI: " + e.getMessage());
            return errore;
        }

        return parseRisposta(rawResponse);
    }

    private String convertiInPngBase64(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        byte[] contenuto = file.getBytes();

        BufferedImage immagine;
        if (contentType != null && contentType.equals("application/pdf")) {
            try (PDDocument document = PDDocument.load(contenuto)) {
                PDFRenderer renderer = new PDFRenderer(document);
                immagine = renderer.renderImageWithDPI(0, 200); // solo la prima pagina
            }
        } else {
            immagine = ImageIO.read(new java.io.ByteArrayInputStream(contenuto));
            if (immagine == null) {
                throw new IOException("Formato file non supportato: usa PDF, JPG o PNG");
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(immagine, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private Map<String, Object> buildRequestBody(String base64Png) {
        Map<String, Object> imageUrl = new LinkedHashMap<>();
        imageUrl.put("url", "data:image/png;base64," + base64Png);

        Map<String, Object> imageContent = new LinkedHashMap<>();
        imageContent.put("type", "image_url");
        imageContent.put("image_url", imageUrl);

        Map<String, Object> textContent = new LinkedHashMap<>();
        textContent.put("type", "text");
        textContent.put("text", "Estrai i dati di questa fattura fornitore (documento italiano). "
                + "Se un campo non e' leggibile o assente, lascialo null. Le date vanno in formato gg/mm/aaaa. "
                + "Gli importi delle righe sono al netto di IVA (imponibile unitario).");

        Map<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", java.util.List.of(textContent, imageContent));

        Map<String, Object> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Sei un assistente esperto di contabilita' italiana. Estrai dati strutturati da fatture fornitore. Rispondi solo con i dati richiesti, nessun commento.");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", java.util.List.of(systemMessage, userMessage));
        body.put("response_format", buildResponseFormatSchema());
        body.put("temperature", 0);

        return body;
    }

    private Map<String, Object> buildResponseFormatSchema() {
        Map<String, Object> rigaProps = new LinkedHashMap<>();
        rigaProps.put("descrizione", Map.of("type", "string"));
        rigaProps.put("quantita", Map.of("type", "number"));
        rigaProps.put("prezzoUnitario", Map.of("type", "number"));
        rigaProps.put("aliquotaIva", Map.of("type", "number"));

        Map<String, Object> rigaSchema = new LinkedHashMap<>();
        rigaSchema.put("type", "object");
        rigaSchema.put("properties", rigaProps);
        rigaSchema.put("required", java.util.List.of("descrizione", "quantita", "prezzoUnitario", "aliquotaIva"));
        rigaSchema.put("additionalProperties", false);

        Map<String, Object> righeArray = new LinkedHashMap<>();
        righeArray.put("type", "array");
        righeArray.put("items", rigaSchema);

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("denominazioneFornitore", Map.of("type", java.util.List.of("string", "null")));
        props.put("partitaIvaFornitore", Map.of("type", java.util.List.of("string", "null")));
        props.put("codiceFiscaleFornitore", Map.of("type", java.util.List.of("string", "null")));
        props.put("numeroDocumento", Map.of("type", java.util.List.of("string", "null")));
        props.put("dataDocumento", Map.of("type", java.util.List.of("string", "null")));
        props.put("totaleImponibile", Map.of("type", java.util.List.of("number", "null")));
        props.put("totaleIva", Map.of("type", java.util.List.of("number", "null")));
        props.put("totaleDocumento", Map.of("type", java.util.List.of("number", "null")));
        props.put("righe", righeArray);

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", props);
        schema.put("required", java.util.List.of("denominazioneFornitore", "partitaIvaFornitore", "codiceFiscaleFornitore",
                "numeroDocumento", "dataDocumento", "totaleImponibile", "totaleIva", "totaleDocumento", "righe"));
        schema.put("additionalProperties", false);

        Map<String, Object> jsonSchema = new LinkedHashMap<>();
        jsonSchema.put("name", "fattura_fornitore");
        jsonSchema.put("schema", schema);
        jsonSchema.put("strict", true);

        Map<String, Object> responseFormat = new LinkedHashMap<>();
        responseFormat.put("type", "json_schema");
        responseFormat.put("json_schema", jsonSchema);
        return responseFormat;
    }

    private EstrazioneFatturaFornitoreDto parseRisposta(String rawResponse) throws IOException {
        JsonNode root = objectMapper.readTree(rawResponse);
        JsonNode errorNode = root.get("error");
        if (errorNode != null) {
            EstrazioneFatturaFornitoreDto errore = new EstrazioneFatturaFornitoreDto();
            errore.setErroreEstrazione("Errore OpenAI: " + errorNode.path("message").asText("sconosciuto"));
            return errore;
        }

        String content = root.path("choices").get(0).path("message").path("content").asText();
        JsonNode dati = objectMapper.readTree(content);

        EstrazioneFatturaFornitoreDto dto = new EstrazioneFatturaFornitoreDto();
        dto.setDenominazioneFornitore(textOrNull(dati, "denominazioneFornitore"));
        dto.setPartitaIvaFornitore(textOrNull(dati, "partitaIvaFornitore"));
        dto.setCodiceFiscaleFornitore(textOrNull(dati, "codiceFiscaleFornitore"));
        dto.setNumeroDocumento(textOrNull(dati, "numeroDocumento"));
        dto.setDataDocumento(textOrNull(dati, "dataDocumento"));
        dto.setTotaleImponibile(numberOrNull(dati, "totaleImponibile"));
        dto.setTotaleIva(numberOrNull(dati, "totaleIva"));
        dto.setTotaleDocumento(numberOrNull(dati, "totaleDocumento"));

        java.util.List<EstrazioneRigaDto> righe = new java.util.ArrayList<>();
        JsonNode righeNode = dati.get("righe");
        if (righeNode != null && righeNode.isArray()) {
            for (JsonNode r : righeNode) {
                EstrazioneRigaDto riga = new EstrazioneRigaDto();
                riga.setDescrizione(textOrNull(r, "descrizione"));
                riga.setQuantita(numberOrNull(r, "quantita"));
                riga.setPrezzoUnitario(numberOrNull(r, "prezzoUnitario"));
                riga.setAliquotaIva(numberOrNull(r, "aliquotaIva"));
                righe.add(riga);
            }
        }
        dto.setRighe(righe);
        return dto;
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asText();
    }

    private Double numberOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asDouble();
    }
}
