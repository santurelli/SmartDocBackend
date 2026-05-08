package it.tinna.smartdoc.server.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.tinna.smartdoc.server.delegate.statistiche.StatisticheDelegate;
import it.tinna.smartdoc.shared.dto.statistiche.DatiGlobaliDto;
import it.tinna.smartdoc.shared.dto.statistiche.DatoDaMostrare;
import it.tinna.smartdoc.shared.dto.statistiche.StatisticaDto;
import it.tinna.smartdoc.shared.dto.statistiche.StatisticaPagamentoDto;
import it.tinna.smartdoc.shared.dto.statistiche.TipoRaggruppamento;
import it.tinna.smartdoc.shared.dto.response.GenericResponseDto;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/statistiche")
@Slf4j
public class StatisticheController {

    @Autowired
    private StatisticheDelegate statisticheDelegate;

    @GetMapping("/acquisti")
    public ResponseEntity<GenericResponseDto<List<StatisticaDto>>> getAcquisti(@RequestParam(required = false) String dtDal,
                                          @RequestParam(required = false) String dtAl,
                                          @RequestParam TipoRaggruppamento raggruppa,
                                          @RequestParam DatoDaMostrare mostra,
                                          @RequestParam(required = false) Integer fornitore) throws SQLException {
        try {
            List<StatisticaDto> list = statisticheDelegate.getAcquisti(dtDal, dtAl, raggruppa, mostra, fornitore);
            return ResponseEntity.ok(new GenericResponseDto<>(list, null));
        } catch (SQLException e) {
            log.error("Errore nel recupero delle statistiche acquisti", e);
            return ResponseEntity.internalServerError().body(new GenericResponseDto<>(null, e.getMessage()));
        }
    }
 
    @GetMapping("/vendite")
    public ResponseEntity<GenericResponseDto<List<StatisticaDto>>> getVendite(@RequestParam(required = false) String dtDal,
                                         @RequestParam(required = false) String dtAl,
                                         @RequestParam TipoRaggruppamento raggruppa,
                                         @RequestParam DatoDaMostrare mostra,
                                         @RequestParam(required = false) Integer cliente) throws SQLException {
        try {
            List<StatisticaDto> list = statisticheDelegate.getVendite(dtDal, dtAl, raggruppa, mostra, cliente);
            return ResponseEntity.ok(new GenericResponseDto<>(list, null));
        } catch (SQLException e) {
            log.error("Errore nel recupero delle statistiche vendite", e);
            return ResponseEntity.internalServerError().body(new GenericResponseDto<>(null, e.getMessage()));
        }
    }
 
    @GetMapping("/pagamenti")
    public ResponseEntity<GenericResponseDto<List<StatisticaPagamentoDto>>> getPagamenti(@RequestParam(required = false) String dtDal,
                                                    @RequestParam(required = false) String dtAl,
                                                    @RequestParam TipoRaggruppamento raggruppa,
                                                    @RequestParam(required = false) String soggetto) throws SQLException {
        try {
            List<StatisticaPagamentoDto> list = statisticheDelegate.getPagamenti(dtDal, dtAl, raggruppa, soggetto);
            return ResponseEntity.ok(new GenericResponseDto<>(list, null));
        } catch (SQLException e) {
            log.error("Errore nel recupero delle statistiche pagamenti", e);
            return ResponseEntity.internalServerError().body(new GenericResponseDto<>(null, e.getMessage()));
        }
    }
 
    @GetMapping("/globali")
    public ResponseEntity<GenericResponseDto<DatiGlobaliDto>> getDatiGlobali() throws SQLException {
        try {
            DatiGlobaliDto dto = statisticheDelegate.getDatiGlobali();
            return ResponseEntity.ok(new GenericResponseDto<>(dto, null));
        } catch (SQLException e) {
            log.error("Errore nel recupero dei dati globali dashboard", e);
            return ResponseEntity.internalServerError().body(new GenericResponseDto<>(null, e.getMessage()));
        }
    }
}
