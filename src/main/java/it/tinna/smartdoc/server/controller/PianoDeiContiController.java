package it.tinna.smartdoc.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.pianoconti.PianoDeiContiDelegate;
import it.tinna.smartdoc.server.security.UserDetailsImpl;
import it.tinna.smartdoc.shared.dto.pianoconti.PianoContoDto;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;

@RestController
@RequestMapping("/api/piano-conti")
public class PianoDeiContiController {

    @Autowired
    private PianoDeiContiDelegate delegate;

    private long currentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId().longValue();
    }

    @GetMapping("/list")
    public GenericResponseDto list(@RequestParam(defaultValue = "") String search) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            List<PianoContoDto> list = delegate.getList(search);
            response.setPayload(list);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @GetMapping("/{id}")
    public GenericResponseDto getById(@PathVariable long id) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            response.setPayload(delegate.getById(id));
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping
    public GenericResponseDto insert(@RequestBody PianoContoDto dto) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            dto.setUserCreated(currentUserId());
            long id = delegate.insert(dto);
            dto.setId(id);
            response.setPayload(dto);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PutMapping("/{id}")
    public GenericResponseDto update(@PathVariable long id, @RequestBody PianoContoDto dto) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            dto.setId(id);
            dto.setUserLastUpdate(currentUserId());
            delegate.update(dto);
            response.setPayload(dto);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public GenericResponseDto delete(@PathVariable long id) {
        GenericResponseDto response = new GenericResponseDto();
        try {
            delegate.delete(currentUserId(), id);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @PostMapping("/importa-standard")
    public GenericResponseDto importaStandard() {
        GenericResponseDto response = new GenericResponseDto();
        try {
            int count = delegate.importaStandard(currentUserId());
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("count", count);
            payload.put("settore", delegate.getSettoreCorrente());
            response.setPayload(payload);
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @GetMapping("/settore-corrente")
    public GenericResponseDto settoreCorrente() {
        GenericResponseDto response = new GenericResponseDto();
        try {
            response.setPayload(delegate.getSettoreCorrente());
        } catch (Exception e) {
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
