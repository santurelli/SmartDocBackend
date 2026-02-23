package it.tinna.smartdoc.server.delegate.tipipagamento;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.dao.aliquoteiva.AliquoteIvaDao;
import it.tinna.smartdoc.server.dao.speseincasso.SpeseIncassoDao;
import it.tinna.smartdoc.server.dao.tipipagamento.TipiPagamentoDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.util.DateUtility;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.speseincasso.SpesaIncassoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto;

@Service(value = "tipiPagamentoDelegate")
public class TipiPagamentoDelegate extends BaseDelegate
{

    // public TipiPagamentoDelegate(JdbcTemplate jdbcTemplate)
    // {
    // super(jdbcTemplate);
    // }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(final long idUser,
                       List<Long> ids) throws SQLException
    {
        final TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        for (long arg0 : ids)
        {
            try
            {
                dao.delete(idUser, arg0);
            }
            catch ( SQLException e )
            {
                break;
            }
        }
    }

    public boolean isExistent(String descrizione,
                              Integer id) throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        return dao.isExistent(descrizione, id);
    }

    public TipoPagamentoDto getById(Integer id) throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        TipoPagamentoDto dto = dao.getById(id);
        dto.setScadenze(dao.getScadenze(id));
        return dto;
    }

    public List<TipoPagamentoDto> getList(String strToSearch,
                                          Integer length,
                                          Integer start,
                                          Integer orderColumn,
                                          String orderDir) throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        List<TipoPagamentoDto> list = dao.getList(strToSearch, length, start, orderColumn, orderDir);
        if ( list != null )
        {
            for ( TipoPagamentoDto dto : list )
            {
                dto.setScadenze(dao.getScadenze(dto.getId()));
            }
        }
        return list;
    }

    public List<TipoPagamentoDto> getListForCombo() throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        return dao.getListForCombo();
    }

    public List<ScadenzaPagamentoDto> getScadenze(int idTipoPagamento) throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        return dao.getScadenze(idTipoPagamento);
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenzeDocumento(String dtDocumento,
                                                                    int idTipoPagamento,
                                                                    BigDecimal totDocumento) throws SQLException, ParseException
    {
        TipiPagamentoDao tipiPagamentoDao = new TipiPagamentoDao(jdbcTemplate);
        AtomicInteger atomicInt = new AtomicInteger(1);
        TipoPagamentoDto tpDto = tipiPagamentoDao.getById(idTipoPagamento);
        SpeseIncassoDao siDao = new SpeseIncassoDao(jdbcTemplate);
        List<ScadenzaPagamentoDocumentoDto> result = new ArrayList<>();
        if ( tpDto.getSaldaSubito() == 1 )
        {
            ScadenzaPagamentoDocumentoDto spdDto = new ScadenzaPagamentoDocumentoDto();
            spdDto.setModalitaPagamento(tpDto.getModalita());
            spdDto.setId(atomicInt.incrementAndGet());
            spdDto.setDtScadenza(dtDocumento);
            spdDto.setImporto(totDocumento.doubleValue());
            spdDto.setSaldato(1);
            spdDto.setDtPagamento(dtDocumento);
            spdDto.setImportoSpeseIncasso(0d);
            spdDto.setIvaSpeseIncasso(0d);
            result.add(spdDto);
        }
        else
        {
            double spesaIncasso = 0;
            double ivaSpesaIncasso = 0;
            SpesaIncassoDto siDto = siDao.getById(tpDto.getIdSpeseIncasso());
            if ( siDto != null )
            {
                spesaIncasso = siDto.getImporto();
                AliquoteIvaDao aiDao = new AliquoteIvaDao(jdbcTemplate);
                AliquotaIvaDto aiDto = aiDao.getById(siDto.getIdAliquotaIva());
                if ( aiDto != null )
                {
                    ivaSpesaIncasso = spesaIncasso * aiDto.getImposta() / 100;
                    spesaIncasso += ivaSpesaIncasso;
                }
            }
            List<ScadenzaPagamentoDto> scadenze = tipiPagamentoDao.getScadenze(tpDto.getId());
            if ( scadenze != null && !scadenze.isEmpty() )
            {
                BigDecimal totParziale = BigDecimal.ZERO;
                totParziale = totParziale.setScale(2, BigDecimal.ROUND_HALF_UP);
                for ( int i = 0; i < scadenze.size(); i++ )
                {
                    ScadenzaPagamentoDto spDto = scadenze.get(i);
                    String dtPagamento = DateUtility.add(DateUtility.parse(dtDocumento), spDto.getGiorni());
                    BigDecimal importo = BigDecimal.ZERO;
                    if ( i < scadenze.size() - 1 )
                    {
                        importo = totDocumento.multiply(BigDecimal.valueOf(spDto.getPercTotale()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                        importo = importo.setScale(2, BigDecimal.ROUND_HALF_UP);
                        totParziale = totParziale.add(importo);
                    }
                    else
                    {
                        importo = totDocumento.subtract(totParziale);
                        importo = importo.setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                    if ( spDto.getFineMese() == 1 )
                    {
                        dtPagamento = DateUtility.getEndDayOfMonth(dtPagamento.split("/")[1], dtPagamento.split("/")[2]);
                    }
                    if ( tpDto.getGiornoPagamentoMeseSuccessivo() != null )
                    {
                        String dtFineMese = DateUtility.getEndDayOfMonth(dtPagamento.split("/")[1], dtPagamento.split("/")[2]);
                        if ( dtPagamento.equals(dtFineMese) )
                        {
                            dtPagamento = DateUtility.add(DateUtility.parse(dtPagamento), tpDto.getGiornoPagamentoMeseSuccessivo());
                        }
                    }
                    if ( tpDto.getSpostaScadenze() == 1 )
                    {
                        if ( DateUtility.getMonth(dtPagamento).equals("08") && DateUtility.getDay(dtPagamento).equals("31") || DateUtility.getMonth(dtPagamento).equals("12") && DateUtility.getDay(dtPagamento).equals("31") )
                        {
                            dtPagamento = DateUtility.add(DateUtility.parse(dtPagamento), 10);
                        }
                    }
                    ScadenzaPagamentoDocumentoDto spdDto = new ScadenzaPagamentoDocumentoDto();
                    // spdDto.setId(new Long(new
                    // Date().getTime()).intValue());
                    spdDto.setModalitaPagamento(tpDto.getModalita());
                    spdDto.setId(atomicInt.incrementAndGet());
                    spdDto.setDtScadenza(dtPagamento);
                    spdDto.setImporto(importo.doubleValue());
                    spdDto.setImportoSpeseIncasso(spesaIncasso);
                    spdDto.setIvaSpeseIncasso(ivaSpesaIncasso);
                    result.add(spdDto);
                }
            }
        }
        return result;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void insert(TipoPagamentoDto dto) throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        if ( dto.getPredefinito().intValue() == 1 )
        {
            dao.resetPredefinite(dto.getUserCreated());
        }
        Integer idTipoPagamento = dao.insert(dto);
        dao.deleteScadenze(idTipoPagamento);
        for ( ScadenzaPagamentoDto scadenzaDto : dto.getScadenze() )
        {
            scadenzaDto.setIdTipoPagamento(idTipoPagamento.longValue());
            dao.insertScadenza(scadenzaDto);
        }
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(TipoPagamentoDto dto) throws SQLException
    {
        TipiPagamentoDao dao = new TipiPagamentoDao(jdbcTemplate);
        if ( dto.getPredefinito().intValue() == 1 )
        {
            dao.resetPredefinite(dto.getUserCreated());
        }
        dao.update(dto);
        dao.deleteScadenze(dto.getId());
        for ( ScadenzaPagamentoDto scadenzaDto : dto.getScadenze() )
        {
            scadenzaDto.setIdTipoPagamento(new Long(dto.getId()));
            dao.insertScadenza(scadenzaDto);
        }
    }

}

