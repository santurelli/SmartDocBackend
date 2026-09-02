package it.tinna.smartdoc.server.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;

import it.tinna.smartdoc.server.delegate.movimentimagazzino.MovimentiMagazzinoDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentiSearchCriteriaDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import java.util.List;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.server.util.MagazzinoUtility;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/api/movimenti")
@Slf4j
public class MovimentiMagazzinoController {

    @Autowired
    private MovimentiMagazzinoDelegate delegate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/carico")
    public GenericResponseDto insertCarico(@RequestBody MovimentoMagazzinoDto dto, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            log.info("--- RECEIVED CARICO REQUEST ---");
            log.info("ID Prodotto: {}", dto.getIdProdotto());
            log.info("Quantita: {}", dto.getQuantita());
            log.debug("Magazzino (Pre-Check): {}", dto.getIdMagazzino());

            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setUserCreated(userDetails.getId().longValue());
            dto.setTipoMovimento("I"); // Assicura che sia Ingresso
            if (dto.getIdMagazzino() == null) {
                Integer magazzinoPredefinito = MagazzinoUtility.getMagazzinoPredefinito(jdbcTemplate);
                log.info("Magazzino IS NULL. Defaulting to magazzino predefinito {}.", magazzinoPredefinito);
                dto.setIdMagazzino(magazzinoPredefinito);
            } else {
                log.info("Magazzino is {}", dto.getIdMagazzino());
            }
            delegate.insertCarico(dto);
            log.info("--- CARICO PROCESSED SUCCESSFULLY ---");
        } catch (Exception e) {
            log.error("--- ERROR PROCESSING CARICO ---", e);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/scarico")
    public GenericResponseDto insertScarico(@RequestBody MovimentoMagazzinoDto dto, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setUserCreated(userDetails.getId().longValue());
            dto.setTipoMovimento("U"); // Uscita
            if (dto.getIdMagazzino() == null) {
                dto.setIdMagazzino(MagazzinoUtility.getMagazzinoPredefinito(jdbcTemplate));
            }
            delegate.insertScarico(dto);
        } catch (Exception e) {
            log.error("Error processing Scarico", e);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/rettifica")
    public GenericResponseDto insertRettifica(@RequestBody MovimentoMagazzinoDto dto, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setUserCreated(userDetails.getId().longValue());
            // TipoMovimento will be decided by business logic
            if (dto.getIdMagazzino() == null) {
                dto.setIdMagazzino(MagazzinoUtility.getMagazzinoPredefinito(jdbcTemplate));
            }
            delegate.insertRettifica(dto);
        } catch (Exception e) {
            log.error("Error processing Scarico/Rettifica", e);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/list")
    public GenericResponseDto<List<MovimentoMagazzinoDto>> list(@RequestBody MovimentiSearchCriteriaDto criteria) {
        GenericResponseDto<List<MovimentoMagazzinoDto>> response = new GenericResponseDto<>();
        try {
            response.setPayload(delegate.list(criteria));
        } catch (Exception e) {
            log.error("Error listing movimenti", e);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    @PostMapping("/export-excel")
    public org.springframework.http.ResponseEntity<byte[]> exportExcel(@RequestBody MovimentiSearchCriteriaDto criteria) {
        try {
            // Unpaged search for export
            criteria.setStart(0);
            criteria.setLength(Integer.MAX_VALUE);
            
            List<MovimentoMagazzinoDto> list = delegate.list(criteria);

            org.jxls.common.Context context = new org.jxls.common.Context();
            context.putVar("movimenti", list);

            org.springframework.core.io.ClassPathResource templateResource = new org.springframework.core.io.ClassPathResource("report/movimenti_magazzino.xls");
            try (java.io.InputStream is = templateResource.getInputStream()) {
                java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();

                org.jxls.util.JxlsHelper.getInstance().processTemplate(is, os, context);

                byte[] content = os.toByteArray();

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"));
                headers.setContentDispositionFormData("attachment", "movimenti_magazzino.xls");
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                return org.springframework.http.ResponseEntity.ok()
                        .headers(headers)
                        .body(content);
            }
        } catch (Exception e) {
            log.error("Error exporting excel", e);
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }
}


