package it.tinna.smartdoc.server.delegate.statistiche;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.clienti.ClientiDao;
import it.tinna.smartdoc.server.dao.documenti.DocumentiDao;
import it.tinna.smartdoc.server.dao.documenti.FattureDao;
import it.tinna.smartdoc.server.dao.documenti.FattureFornitoreDao;
import it.tinna.smartdoc.server.dao.documenti.NoteCreditoDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.scadenzario.ScadenzarioDao;
import it.tinna.smartdoc.server.dao.statistiche.StatisticheDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.statistiche.DatiGlobaliDto;
import it.tinna.smartdoc.shared.dto.statistiche.DatoDaMostrare;
import it.tinna.smartdoc.shared.dto.statistiche.StatisticaDto;
import it.tinna.smartdoc.shared.dto.statistiche.StatisticaPagamentoDto;
import it.tinna.smartdoc.shared.dto.statistiche.TipoRaggruppamento;

@Transactional(readOnly = true)
@Service(value = "statisticheDelegate")
public class StatisticheDelegate extends BaseDelegate
{

    public List<StatisticaDto> getAcquisti(String dtDal,
                                           String dtAl,
                                           TipoRaggruppamento raggruppa,
                                           DatoDaMostrare mostra,
                                           Integer fornitore) throws SQLException
    {
        StatisticheDao dao = new StatisticheDao(jdbcTemplate);
        return dao.getAcquisti(dtDal, dtAl, raggruppa, mostra, fornitore);

    }

    /**
     * Restituisce gli indicatori visualizzati nei box in home page
     * 
     * @return
     * @throws Exception
     */
    public DatiGlobaliDto getDatiGlobali() throws SQLException
    {
        DatiGlobaliDto dto = new DatiGlobaliDto();
        ScadenzarioDao scadenzarioDao = new ScadenzarioDao(jdbcTemplate);
        DocumentiDao documentiDao = new DocumentiDao(jdbcTemplate);
        FattureDao fattureDao = new FattureDao(jdbcTemplate);
        NoteCreditoDao noteCreditoDao = new NoteCreditoDao(jdbcTemplate);
        FattureFornitoreDao fattureFornitoreDao = new FattureFornitoreDao(jdbcTemplate);
        ClientiDao clientiDao = new ClientiDao(jdbcTemplate);
        FornitoriDao fornitoriDao = new FornitoriDao(jdbcTemplate);
        StatisticheDao statisticheDao = new StatisticheDao(jdbcTemplate);
        BigDecimal totDaPagare = scadenzarioDao.getTotaleDaPagareFineMese();
        if ( totDaPagare == null )
        {
            totDaPagare = new BigDecimal(0);
        }
        BigDecimal totDaRicevere = scadenzarioDao.getTotaleDaRicevereFineMese();
        if ( totDaRicevere == null )
        {
            totDaRicevere = new BigDecimal(0);
        }
        Long totClienti = clientiDao.getTotClienti();
        Long totFornitori = fornitoriDao.getTotFornitori();
        Long totFatture = documentiDao.getNumTotFattureMese();
        BigDecimal totFattureFornitore = fattureFornitoreDao.getTotaleMeseCorrente();
        BigDecimal totNoteCredito = noteCreditoDao.getTotaleMeseCorrente();
        dto.setTotClienti(totClienti);
        dto.setTotFornitori(totFornitori);
        dto.setNumFattureMese(totFatture);
        dto.setTotDaPagare(totDaPagare);
        dto.setTotDaRicevere(totDaRicevere);
        dto.setTotFattureFornitore(totFattureFornitore);
        dto.setTotNoteCredito(totNoteCredito);
        dto.setVendutoPerMese(statisticheDao.getVenditeAnnoCorrentePerMese());
        dto.setPagamentiRicevutiPerMese(statisticheDao.getPagamentiRicevutiAnnoCorrentePerMese());
        return dto;

    }

    public List<StatisticaPagamentoDto> getPagamenti(String dtDal,
                                                     String dtAl,
                                                     TipoRaggruppamento raggruppa,
                                                     String soggetto) throws SQLException
    {
        StatisticheDao dao = new StatisticheDao(jdbcTemplate);
        return dao.getPagamenti(dtDal, dtAl, raggruppa, soggetto);

    }

    public List<StatisticaDto> getVendite(String dtDal,
                                          String dtAl,
                                          TipoRaggruppamento raggruppa,
                                          DatoDaMostrare mostra,
                                          Integer cliente) throws SQLException
    {
        StatisticheDao dao = new StatisticheDao(jdbcTemplate);
        return dao.getVendite(dtDal, dtAl, raggruppa, mostra, cliente);

    }

}

