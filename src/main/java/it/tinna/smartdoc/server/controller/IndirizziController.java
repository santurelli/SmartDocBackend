package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.dao.indirizzi.IndirizziDao;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;

@RestController
@RequestMapping("/api/indirizzi")
public class IndirizziController {

    @Autowired
    private IndirizziDao indirizziDao;

    @GetMapping("/clienti/{idCliente}")
    public ResponseEntity<List<IndirizzoDto>> getIndirizziCliente(@PathVariable Long idCliente) {
        try {
            return ResponseEntity.ok(indirizziDao.getListByIdRichiedente("clienti", idCliente));
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

