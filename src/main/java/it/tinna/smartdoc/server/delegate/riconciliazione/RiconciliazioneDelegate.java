package it.tinna.smartdoc.server.delegate.riconciliazione;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.riconciliazione.RiconciliazioneDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.service.riconciliazione.RiconciliazioneMatchingService;
import it.tinna.smartdoc.server.service.riconciliazione.parser.CsvParser;
import it.tinna.smartdoc.server.service.riconciliazione.parser.EstrattoContoParser;
import it.tinna.smartdoc.server.service.riconciliazione.parser.Mt940Parser;
import it.tinna.smartdoc.shared.dto.riconciliazione.MatchCandidatoDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.MovimentoEstrattoContoDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneCsvMappingDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneImportDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneMovimentoDto;

@Transactional(readOnly = true)
@Service("riconciliazioneDelegate")
public class RiconciliazioneDelegate extends BaseDelegate
{

    private static final DateTimeFormatter DATA_IT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RiconciliazioneMatchingService matchingService = new RiconciliazioneMatchingService();

    public List<RiconciliazioneImportDto> getListImport() throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        return dao.getListImport();
    }

    public List<RiconciliazioneMovimentoDto> getMovimentiByImport(long idImport) throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        List<RiconciliazioneMovimentoDto> movimenti = dao.getMovimentiByImport(idImport);
        // Per i movimenti non abbinati, ricalcola i candidati "al volo" per proporli in review
        // (non persistiti: cambiano se nel frattempo si salda un'altra scadenza)
        for ( RiconciliazioneMovimentoDto m : movimenti )
        {
            if ( "NON_ABBINATO".equals(m.getStato()) )
            {
                MovimentoEstrattoContoDto movimentoDto = toMovimentoDto(m);
                List<MatchCandidatoDto> pool = getPoolCandidati(movimentoDto);
                m.setCandidati(matchingService.calcolaCandidati(movimentoDto, pool));
            }
        }
        return movimenti;
    }

    @Transactional(rollbackFor = Exception.class)
    public RiconciliazioneImportDto importFile(byte[] fileContent,
                                               String nomeFile,
                                               String formato,
                                               Integer idRisorsa,
                                               long userId) throws SQLException, IOException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);

        EstrattoContoParser parser;
        if ( "MT940".equalsIgnoreCase(formato) )
        {
            parser = new Mt940Parser();
        }
        else
        {
            RiconciliazioneCsvMappingDto mapping = idRisorsa != null ? dao.getCsvMapping(idRisorsa) : null;
            if ( mapping == null )
            {
                RiconciliazioneImportDto errore = new RiconciliazioneImportDto();
                errore.setNomeFile(nomeFile);
                errore.setFormato(formato);
                errore.setIdRisorsa(idRisorsa);
                errore.setStato("ERRORE");
                errore.setDettaglioErrore("Nessun mapping colonne configurato per questa risorsa/banca. Configuralo prima di importare un CSV.");
                errore.setNumMovimenti(0);
                errore.setNumAbbinatiAuto(0);
                long id = dao.insertImport(errore, userId);
                errore.setId(id);
                return errore;
            }
            parser = new CsvParser(mapping);
        }

        RiconciliazioneImportDto importDto = new RiconciliazioneImportDto();
        importDto.setNomeFile(nomeFile);
        importDto.setFormato(formato);
        importDto.setIdRisorsa(idRisorsa);

        List<MovimentoEstrattoContoDto> movimenti;
        try
        {
            movimenti = parser.parse(fileContent);
        }
        catch ( Exception e )
        {
            _log.error("Errore nel parsing del file di estratto conto {}", nomeFile, e);
            importDto.setStato("ERRORE");
            importDto.setDettaglioErrore("Errore nella lettura del file: " + e.getMessage());
            importDto.setNumMovimenti(0);
            importDto.setNumAbbinatiAuto(0);
            long id = dao.insertImport(importDto, userId);
            importDto.setId(id);
            return importDto;
        }

        importDto.setStato("COMPLETATO");
        importDto.setNumMovimenti(movimenti.size());
        int abbinatiAuto = 0;
        long idImport = dao.insertImport(importDto, userId);
        importDto.setId(idImport);

        for ( MovimentoEstrattoContoDto movimento : movimenti )
        {
            List<MatchCandidatoDto> pool = getPoolCandidati(movimento);
            List<MatchCandidatoDto> candidati = matchingService.calcolaCandidati(movimento, pool);

            if ( matchingService.isAutoMatchAffidabile(candidati) )
            {
                MatchCandidatoDto scelto = candidati.get(0);
                dao.insertMovimento(idImport, movimento, "ABBINATO_AUTO", scelto.getTipo(), scelto.getIdScadenza(), scelto.getScore());
                chiudiScadenza(dao, scelto.getTipo(), scelto.getIdScadenza(), movimento.getDataValuta().format(DATA_IT));
                abbinatiAuto++;
            }
            else
            {
                dao.insertMovimento(idImport, movimento, "NON_ABBINATO", null, null, null);
            }
        }

        importDto.setNumAbbinatiAuto(abbinatiAuto);
        return importDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void confermaAbbinamento(long idMovimento,
                                    String tipoScadenza,
                                    long idScadenza,
                                    String dataValuta,
                                    long userId) throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        dao.updateMovimentoAbbinamento(idMovimento, "ABBINATO_MANUALE", tipoScadenza, (int) idScadenza, BigDecimal.valueOf(100), userId);
        chiudiScadenza(dao, tipoScadenza, (int) idScadenza, dataValuta);
    }

    @Transactional(rollbackFor = Exception.class)
    public void ignoraMovimento(long idMovimento,
                                long userId) throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        dao.updateMovimentoAbbinamento(idMovimento, "IGNORATO", null, null, null, userId);
    }

    public RiconciliazioneCsvMappingDto getCsvMapping(long idRisorsa) throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        return dao.getCsvMapping(idRisorsa);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void saveCsvMapping(RiconciliazioneCsvMappingDto dto,
                               long userId) throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        dao.saveCsvMapping(dto, userId);
    }

    private void chiudiScadenza(RiconciliazioneDao dao,
                                String tipo,
                                Integer idScadenza,
                                String dataValuta) throws SQLException
    {
        if ( "INCASSO".equals(tipo) )
        {
            dao.saldaScadenzaIncasso(idScadenza, dataValuta);
        }
        else if ( "PAGAMENTO".equals(tipo) )
        {
            dao.saldaScadenzaPagamento(idScadenza, dataValuta);
        }
    }

    private List<MatchCandidatoDto> getPoolCandidati(MovimentoEstrattoContoDto movimento) throws SQLException
    {
        RiconciliazioneDao dao = new RiconciliazioneDao(jdbcTemplate);
        String dataCentro = movimento.getDataValuta().format(DATA_IT);
        List<MatchCandidatoDto> pool = new ArrayList<>();
        // Un'entrata puo' abbinarsi solo a scadenze di incasso, un'uscita solo a scadenze di pagamento:
        // il segno del movimento bancario e' un vincolo, non un'euristica.
        if ( movimento.getImporto().signum() > 0 )
        {
            pool.addAll(dao.getScadenzeAperteIncasso(dataCentro));
        }
        else
        {
            pool.addAll(dao.getScadenzeApertePagamento(dataCentro));
        }
        return pool;
    }

    private List<MatchCandidatoDto> getPoolCandidati(RiconciliazioneMovimentoDto m) throws SQLException
    {
        return getPoolCandidati(toMovimentoDto(m));
    }

    private MovimentoEstrattoContoDto toMovimentoDto(RiconciliazioneMovimentoDto m)
    {
        MovimentoEstrattoContoDto dto = new MovimentoEstrattoContoDto();
        dto.setImporto(m.getImporto());
        dto.setCausaleBanca(m.getCausaleBanca());
        dto.setControparte(m.getControparte());
        dto.setDataValuta(java.time.LocalDate.parse(m.getDataValuta(), DATA_IT));
        return dto;
    }

}
