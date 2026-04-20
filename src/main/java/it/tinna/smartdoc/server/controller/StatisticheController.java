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

@RestController
@RequestMapping("/api/statistiche")
public class StatisticheController {

    @Autowired
    private StatisticheDelegate statisticheDelegate;

    @GetMapping("/acquisti")
    public List<StatisticaDto> getAcquisti(@RequestParam(required = false) String dtDal,
                                          @RequestParam(required = false) String dtAl,
                                          @RequestParam TipoRaggruppamento raggruppa,
                                          @RequestParam DatoDaMostrare mostra,
                                          @RequestParam(required = false) Integer fornitore) throws SQLException {
        return statisticheDelegate.getAcquisti(dtDal, dtAl, raggruppa, mostra, fornitore);
    }

    @GetMapping("/vendite")
    public List<StatisticaDto> getVendite(@RequestParam(required = false) String dtDal,
                                         @RequestParam(required = false) String dtAl,
                                         @RequestParam TipoRaggruppamento raggruppa,
                                         @RequestParam DatoDaMostrare mostra,
                                         @RequestParam(required = false) Integer cliente) throws SQLException {
        return statisticheDelegate.getVendite(dtDal, dtAl, raggruppa, mostra, cliente);
    }

    @GetMapping("/pagamenti")
    public List<StatisticaPagamentoDto> getPagamenti(@RequestParam(required = false) String dtDal,
                                                    @RequestParam(required = false) String dtAl,
                                                    @RequestParam TipoRaggruppamento raggruppa,
                                                    @RequestParam(required = false) String soggetto) throws SQLException {
        return statisticheDelegate.getPagamenti(dtDal, dtAl, raggruppa, soggetto);
    }

    @GetMapping("/globali")
    public DatiGlobaliDto getDatiGlobali() throws SQLException {
        return statisticheDelegate.getDatiGlobali();
    }
}
