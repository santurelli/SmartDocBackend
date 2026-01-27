package it.tinna.smartdoc.server.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import it.tinna.smartdoc.server.delegate.movimentimagazzino.MovimentiMagazzinoDelegate;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentoMagazzinoDto;
import it.tinna.smartdoc.shared.dto.prodotti.MovimentiSearchCriteriaDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import java.util.List;
import it.tinna.smartdoc.server.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/movimenti")
public class MovimentiMagazzinoController {

    @Autowired
    private MovimentiMagazzinoDelegate delegate;

    @PostMapping("/carico")
    public GenericResponseDto insertCarico(@RequestBody MovimentoMagazzinoDto dto, HttpServletRequest request) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            System.out.println("--- RECEIVED CARICO REQUEST ---");
            System.out.println("ID Prodotto: " + dto.getIdProdotto());
            System.out.println("Quantita: " + dto.getQuantita());
            System.out.println("Magazzino (Pre-Check): " + dto.getIdMagazzino());

            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            dto.setUserCreated(userDetails.getId().longValue());
            dto.setTipoMovimento("I"); // Assicura che sia Ingresso
            if (dto.getIdMagazzino() == null) {
                System.out.println("Magazzino IS NULL. Defaulting to 1.");
                dto.setIdMagazzino(1); // Default Magazzino Sede
            } else {
                System.out.println("Magazzino is " + dto.getIdMagazzino());
            }
            delegate.insertCarico(dto);
            System.out.println("--- CARICO PROCESSED SUCCESSFULLY ---");
        } catch (Exception e) {
            System.out.println("--- ERROR PROCESSING CARICO ---");
            e.printStackTrace();
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
                dto.setIdMagazzino(1); // Default Magazzino Sede
            }
            delegate.insertScarico(dto);
        } catch (Exception e) {
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
                dto.setIdMagazzino(1); // Default Magazzino Sede
            }
            delegate.insertRettifica(dto);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        }
        return response;
    }

    @PostMapping("/list")
    public GenericResponseDto<List<MovimentoMagazzinoDto>> list(@RequestBody MovimentiSearchCriteriaDto criteria) {
        GenericResponseDto<List<MovimentoMagazzinoDto>> response = new GenericResponseDto<>();
        try {
            response.setPayload(delegate.list(criteria));
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}

