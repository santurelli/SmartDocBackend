package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import it.tinna.smartdoc.server.security.UserDetailsImpl;

import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.shared.dto.listini.ListinoDto;

@RestController
@RequestMapping("/api/listini")
public class ListiniController {

    @Autowired
    private ListiniDelegate listiniDelegate;

    private Integer getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("/list")
    public List<ListinoDto> getAll() throws SQLException {
        return listiniDelegate.getAll();
    }

    @GetMapping("/combo")
    public List<ListinoDto> getListForCombo() throws SQLException {
        return listiniDelegate.getListForCombo();
    }

    @GetMapping("/{id}")
    public ListinoDto getById(@PathVariable Long id) throws SQLException {
        return listiniDelegate.getById(id);
    }

    @PostMapping
    public Long insert(@RequestBody ListinoDto dto) throws SQLException {
        return listiniDelegate.insert(dto, getCurrentUser());
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody ListinoDto dto) throws SQLException {
        dto.setId(id);
        listiniDelegate.update(dto, getCurrentUser());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws SQLException {
        listiniDelegate.delete(id, getCurrentUser());
    }
}
