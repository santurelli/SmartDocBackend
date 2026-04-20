package it.tinna.smartdoc.server.dao.statistiche;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.DateUtility;
import it.tinna.smartdoc.shared.dto.statistiche.DatoDaMostrare;
import it.tinna.smartdoc.shared.dto.statistiche.StatisticaDto;
import it.tinna.smartdoc.shared.dto.statistiche.StatisticaPagamentoDto;
import it.tinna.smartdoc.shared.dto.statistiche.TipoRaggruppamento;

public class StatisticheDao extends BaseDao
{

    public StatisticheDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public List<StatisticaDto> getAcquisti(String dtDal,
                                           String dtAl,
                                           final TipoRaggruppamento raggruppa,
                                           final DatoDaMostrare mostra,
                                           Integer fornitore) throws SQLException
    {
        String query = null;
        ArrayList<Object> params = new ArrayList<>();
        Map<String, String> valuesMap = new HashMap<>();
        
        boolean isProductQuery = (raggruppa == TipoRaggruppamento.PRODOTTO || 
                                raggruppa == TipoRaggruppamento.CATEGORIA_PRODOTTO || 
                                raggruppa == TipoRaggruppamento.SOTTOCATEGORIA_PRODOTTO || 
                                raggruppa == TipoRaggruppamento.DIVISIONE);

        if ( raggruppa == TipoRaggruppamento.MESE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "DATE_TRUNC('month', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('month', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('month', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.GIORNO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "DATE_TRUNC('day', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('day', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('day', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.TRIMESTRE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "DATE_TRUNC('quarter', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('quarter', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('quarter', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.ANNOTEMPORALE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "DATE_TRUNC('year', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('year', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('year', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.FORNITORE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "cliente_fornitore");
            valuesMap.put("GROUP_BY", "cliente_fornitore");
            valuesMap.put("ORDER_BY", "cliente_fornitore");
        }
        else if ( raggruppa == TipoRaggruppamento.AGENTE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "agente");
            valuesMap.put("GROUP_BY", "agente");
            valuesMap.put("ORDER_BY", "agente");
        }
        else if ( raggruppa == TipoRaggruppamento.PRODOTTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S06");
            valuesMap.put("COL_1", "descrizione_prodotto");
            valuesMap.put("GROUP_BY", "descrizione_prodotto");
            valuesMap.put("ORDER_BY", "descrizione_prodotto");
        }
        else if ( raggruppa == TipoRaggruppamento.CATEGORIA_PRODOTTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S06");
            valuesMap.put("COL_1", "descrizione_categoria");
            valuesMap.put("GROUP_BY", "descrizione_categoria");
            valuesMap.put("ORDER_BY", "descrizione_categoria");
        }
        else if ( raggruppa == TipoRaggruppamento.SOTTOCATEGORIA_PRODOTTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S06");
            valuesMap.put("COL_1", "descrizione_sottocategoria");
            valuesMap.put("GROUP_BY", "descrizione_sottocategoria");
            valuesMap.put("ORDER_BY", "descrizione_sottocategoria");
        }
        else if ( raggruppa == TipoRaggruppamento.DIVISIONE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S06");
            valuesMap.put("COL_1", "descrizione_divisione");
            valuesMap.put("GROUP_BY", "descrizione_divisione");
            valuesMap.put("ORDER_BY", "descrizione_divisione");
        }
        else if ( raggruppa == TipoRaggruppamento.CITTA )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "citta");
            valuesMap.put("GROUP_BY", "citta");
            valuesMap.put("ORDER_BY", "citta");
        }
        else if ( raggruppa == TipoRaggruppamento.PROVINCIA )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "provincia");
            valuesMap.put("GROUP_BY", "provincia");
            valuesMap.put("ORDER_BY", "provincia");
        }
        else if ( raggruppa == TipoRaggruppamento.NAZIONE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "nazione");
            valuesMap.put("GROUP_BY", "nazione");
            valuesMap.put("ORDER_BY", "nazione");
        }
        else if ( raggruppa == TipoRaggruppamento.TIPO_DOCUMENTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "tipo_documento");
            valuesMap.put("GROUP_BY", "tipo_documento");
            valuesMap.put("ORDER_BY", "tipo_documento");
        }
        else if ( raggruppa == TipoRaggruppamento.PAGAMENTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S05");
            valuesMap.put("COL_1", "pagamento");
            valuesMap.put("GROUP_BY", "pagamento");
            valuesMap.put("ORDER_BY", "pagamento");
        }

        if ( mostra == DatoDaMostrare.NUMERO_DOCUMENTI )
        {
            valuesMap.put("COL_2", "COUNT(*)");
        }
        else if ( mostra == DatoDaMostrare.IMPONIBILE )
        {
            String col = isProductQuery ? "prezzoimponibile" : "(totale - iva_credito)";
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCF' then -(" + col + ") else (" + col + ") end), 0)");
        }
        else if ( mostra == DatoDaMostrare.IVA )
        {
            String col = isProductQuery ? "(prezzototale - prezzoimponibile)" : "iva_credito";
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCF' then -(" + col + ") else (" + col + ") end), 0)");
        }
        else if ( mostra == DatoDaMostrare.TOTALE_DOCUMENTO )
        {
            String col = isProductQuery ? "prezzototale" : "totale";
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCF' then -(" + col + ") else (" + col + ") end), 0)");
        }
        else if ( mostra == DatoDaMostrare.QUANTITA_PRODOTTI )
        {
            valuesMap.put("COL_2", "SUM(CASE WHEN tipo_documento = 'NCF' THEN -(quantita_movimento) ELSE (quantita_movimento) END)");
        }
        else if ( mostra == DatoDaMostrare.IMPORTO_PRODOTTI )
        {
            valuesMap.put("COL_2", "SUM(CASE WHEN tipo_documento = 'NCF' THEN -(prezzoimponibile) ELSE (prezzoimponibile) END)");
        }
        else if ( mostra == DatoDaMostrare.IMPORTO_PRODOTTI_IVATO )
        {
            valuesMap.put("COL_2", "SUM(CASE WHEN tipo_documento = 'NCF' THEN -(prezzototale) ELSE (prezzototale) END)");
        }
        else if ( mostra == DatoDaMostrare.PREZZO_MEDIO_PRODOTTI )
        {
            valuesMap.put("COL_2", "AVG(prezzoimponibile/quantita_movimento)");
        }
        else if ( mostra == DatoDaMostrare.PREZZO_MASSIMO_PRODOTTI )
        {
            valuesMap.put("COL_2", "MAX(prezzoimponibile/quantita_movimento)");
        }
        else if ( mostra == DatoDaMostrare.PREZZO_MINIMO_PRODOTTI )
        {
            valuesMap.put("COL_2", "MIN(prezzoimponibile/quantita_movimento)");
        }
        params.add(dtDal);
        params.add(dtAl);
        params.add(fornitore);
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            return jdbcTemplate.query(query, new RowMapper<StatisticaDto>()
            {
                @Override
                public StatisticaDto mapRow(ResultSet rs,
                                            int rowNum) throws SQLException
                {
                    StatisticaDto dto = new StatisticaDto();
                    if ( raggruppa == TipoRaggruppamento.MESE || raggruppa == TipoRaggruppamento.GIORNO || raggruppa == TipoRaggruppamento.TRIMESTRE || raggruppa == TipoRaggruppamento.ANNOTEMPORALE )
                    {
                        Timestamp dt = rs.getTimestamp("descrizione");
                        if ( raggruppa == TipoRaggruppamento.GIORNO ) {
                            dto.setDescrizione(DateUtility.format(dt, "dd/MM/yyyy"));
                        } else if ( raggruppa == TipoRaggruppamento.TRIMESTRE ) {
                            try {
                                int month = Integer.parseInt(DateUtility.getMonth(dt));
                                int quarter = (month - 1) / 3 + 1;
                                dto.setDescrizione("Q" + quarter + " " + DateUtility.getYear(dt));
                            } catch (Exception e) {
                                dto.setDescrizione("");
                            }
                        } else if ( raggruppa == TipoRaggruppamento.ANNOTEMPORALE ) {
                            dto.setDescrizione(DateUtility.getYear(dt));
                        } else {
                            String monthText = DateUtility.getMonthText(dt).substring(0, 3);
                            String year = StringUtils.substring(DateUtility.getYear(dt), -2);
                            dto.setDescrizione(new StringBuilder(monthText).append(" ").append(year).toString());
                        }
                    }
                    else
                    {
                        String desc = rs.getString("descrizione");
                        if ( raggruppa == TipoRaggruppamento.TIPO_DOCUMENTO )
                        {
                            desc = formatTipoDocumento(desc);
                        }
                        dto.setDescrizione(desc);
                    }
                    if ( mostra == DatoDaMostrare.NUMERO_DOCUMENTI )
                    {
                        dto.setValore(new Double(rs.getLong("valore")));
                    }
                    else
                    {
                        dto.setValore(rs.getDouble("valore"));
                    }
                    return dto;
                }
            }, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle statistiche di acquisto", e);
            throw new SQLException(e);
        }
    }

    public List<StatisticaPagamentoDto> getPagamenti(String dtDal,
                                                     String dtAl,
                                                     final TipoRaggruppamento raggruppa,
                                                     String soggetto) throws SQLException
    {
        String query = null;
        ArrayList<Object> params = new ArrayList<>();
        Map<String, String> valuesMap = new HashMap<>();
        query = FileQueryReader.getQuery("STATISTICHE_S07");
        if ( raggruppa == TipoRaggruppamento.MESE )
        {
            valuesMap.put("COL_VPRIMANOTA", "DATE_TRUNC('month', data)");
            valuesMap.put("GROUP_BY_VPRIMANOTA", "DATE_TRUNC('month', data)");
            valuesMap.put("COL_FATTURE", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentifatture.dt_pagamento, d_e_scadenzepagamentifatture.data))");
            valuesMap.put("GROUP_BY_FATTURE", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentifatture.dt_pagamento, d_e_scadenzepagamentifatture.data))");
            valuesMap.put("COL_NOTECREDITO", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentinotecredito.dt_pagamento, d_e_scadenzepagamentinotecredito.data))");
            valuesMap.put("GROUP_BY_NOTECREDITO", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentinotecredito.dt_pagamento, d_e_scadenzepagamentinotecredito.data))");
            valuesMap.put("COL_FATTUREFORNITORE", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentifatturefornitore.dt_pagamento, d_e_scadenzepagamentifatturefornitore.data))");
            valuesMap.put("GROUP_BY_FATTUREFORNITORE", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentifatturefornitore.dt_pagamento, d_e_scadenzepagamentifatturefornitore.data))");
            valuesMap.put("COL_NOTECREDITOFORNITORE", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentinotecreditofornitore.dt_pagamento, d_e_scadenzepagamentinotecreditofornitore.data))");
            valuesMap.put("GROUP_BY_NOTECREDITOFORNITORE", "DATE_TRUNC('month', COALESCE(d_e_scadenzepagamentinotecreditofornitore.dt_pagamento, d_e_scadenzepagamentinotecreditofornitore.data))");
        }
        else
        {
            valuesMap.put("COL_VPRIMANOTA", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("GROUP_BY_VPRIMANOTA", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("COL_FATTURE", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("GROUP_BY_FATTURE", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("COL_NOTECREDITO", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("GROUP_BY_NOTECREDITO", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("COL_FATTUREFORNITORE", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("GROUP_BY_FATTUREFORNITORE", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("COL_NOTECREDITOFORNITORE", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
            valuesMap.put("GROUP_BY_NOTECREDITOFORNITORE", "COALESCE(d_e_divisioni.descrizione, '<Vuoto>')");
        }
        params.add(dtDal);
        params.add(dtAl);
        params.add(StringUtils.defaultIfEmpty(soggetto, null));
        params.add(dtDal);
        params.add(dtAl);
        if ( StringUtils.isNotEmpty(soggetto) )
        {
            if ( StringUtils.startsWith(soggetto, "C-") )
            {
                params.add(Long.parseLong(soggetto.replaceAll("C-", "")));
            }
            else
            {
                params.add(-1l);
            }
        }
        else
        {
            params.add(null);
        }
        params.add(dtDal);
        params.add(dtAl);
        if ( StringUtils.isNotEmpty(soggetto) )
        {
            if ( StringUtils.startsWith(soggetto, "C-") )
            {
                params.add(Long.parseLong(soggetto.replaceAll("C-", "")));
            }
            else
            {
                params.add(-1l);
            }
        }
        else
        {
            params.add(null);
        }
        params.add(dtDal);
        params.add(dtAl);
        if ( StringUtils.isNotEmpty(soggetto) )
        {
            if ( StringUtils.startsWith(soggetto, "F-") )
            {
                params.add(Long.parseLong(soggetto.replaceAll("F-", "")));
            }
            else
            {
                params.add(-1l);
            }
        }
        else
        {
            params.add(null);
        }
        params.add(dtDal);
        params.add(dtAl);
        if ( StringUtils.isNotEmpty(soggetto) )
        {
            if ( StringUtils.startsWith(soggetto, "F-") )
            {
                params.add(Long.parseLong(soggetto.replaceAll("F-", "")));
            }
            else
            {
                params.add(-1l);
            }
        }
        else
        {
            params.add(null);
        }

        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            return jdbcTemplate.query(query, new RowMapper<StatisticaPagamentoDto>()
            {
                @Override
                public StatisticaPagamentoDto mapRow(ResultSet rs,
                                                     int rowNum) throws SQLException
                {
                    StatisticaPagamentoDto dto = new StatisticaPagamentoDto();
                    if ( raggruppa == TipoRaggruppamento.MESE )
                    {
                        Timestamp dt = rs.getTimestamp("descrizione");
                        String monthText = DateUtility.getMonthText(dt).substring(0, 3);
                        String year = StringUtils.substring(DateUtility.getYear(dt), -2);
                        dto.setKey(new StringBuilder(monthText).append(" ").append(year).toString());
                    }
                    else
                    {
                        dto.setKey(rs.getString("descrizione"));
                    }
                    dto.setEntrate(rs.getDouble("entrate"));
                    dto.setUscite(rs.getDouble("uscite"));
                    dto.setSaldo(BigDecimal.valueOf(dto.getEntrate()).subtract(BigDecimal.valueOf(dto.getUscite())).doubleValue());
                    return dto;
                }
            }, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle statistiche dei pagamenti", e);
            throw new SQLException(e);
        }
    }

    public List<StatisticaDto> getPagamentiRicevutiAnnoCorrentePerMese() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<StatisticaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(StatisticaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("STATISTICHE_S04"), rowMapper);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dei pagamenti ricevuti nell'anno corrente per mese", e);
            throw new SQLException(e);
        }
    }

    public List<StatisticaDto> getVendite(String dtDal,
                                          String dtAl,
                                          final TipoRaggruppamento raggruppa,
                                          final DatoDaMostrare mostra,
                                          Integer cliente) throws SQLException
    {
        String query = null;
        ArrayList<Object> params = new ArrayList<>();
        Map<String, String> valuesMap = new HashMap<>();

        boolean isProductQuery = (raggruppa == TipoRaggruppamento.PRODOTTO || 
                                raggruppa == TipoRaggruppamento.CATEGORIA_PRODOTTO || 
                                raggruppa == TipoRaggruppamento.SOTTOCATEGORIA_PRODOTTO || 
                                raggruppa == TipoRaggruppamento.DIVISIONE);

        if ( raggruppa == TipoRaggruppamento.MESE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "DATE_TRUNC('month', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('month', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('month', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.GIORNO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "DATE_TRUNC('day', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('day', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('day', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.TRIMESTRE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "DATE_TRUNC('quarter', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('quarter', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('quarter', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.ANNOTEMPORALE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "DATE_TRUNC('year', data_documento)");
            valuesMap.put("GROUP_BY", "DATE_TRUNC('year', data_documento)");
            valuesMap.put("ORDER_BY", "DATE_TRUNC('year', data_documento)");
        }
        else if ( raggruppa == TipoRaggruppamento.CLIENTE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "cliente_fornitore");
            valuesMap.put("GROUP_BY", "cliente_fornitore");
            valuesMap.put("ORDER_BY", "cliente_fornitore");
        }
        else if ( raggruppa == TipoRaggruppamento.AGENTE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "agente");
            valuesMap.put("GROUP_BY", "agente");
            valuesMap.put("ORDER_BY", "agente");
        }
        else if ( raggruppa == TipoRaggruppamento.PRODOTTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S02");
            valuesMap.put("COL_1", "descrizione_prodotto");
            valuesMap.put("GROUP_BY", "descrizione_prodotto");
            valuesMap.put("ORDER_BY", "descrizione_prodotto");
        }
        else if ( raggruppa == TipoRaggruppamento.CATEGORIA_PRODOTTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S02");
            valuesMap.put("COL_1", "descrizione_categoria");
            valuesMap.put("GROUP_BY", "descrizione_categoria");
            valuesMap.put("ORDER_BY", "descrizione_categoria");
        }
        else if ( raggruppa == TipoRaggruppamento.SOTTOCATEGORIA_PRODOTTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S02");
            valuesMap.put("COL_1", "descrizione_sottocategoria");
            valuesMap.put("GROUP_BY", "descrizione_sottocategoria");
            valuesMap.put("ORDER_BY", "descrizione_sottocategoria");
        }
        else if ( raggruppa == TipoRaggruppamento.DIVISIONE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S02");
            valuesMap.put("COL_1", "descrizione_divisione");
            valuesMap.put("GROUP_BY", "descrizione_divisione");
            valuesMap.put("ORDER_BY", "descrizione_divisione");
        }
        else if ( raggruppa == TipoRaggruppamento.CITTA )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "citta");
            valuesMap.put("GROUP_BY", "citta");
            valuesMap.put("ORDER_BY", "citta");
        }
        else if ( raggruppa == TipoRaggruppamento.PROVINCIA )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "provincia");
            valuesMap.put("GROUP_BY", "provincia");
            valuesMap.put("ORDER_BY", "provincia");
        }
        else if ( raggruppa == TipoRaggruppamento.NAZIONE )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "nazione");
            valuesMap.put("GROUP_BY", "nazione");
            valuesMap.put("ORDER_BY", "nazione");
        }
        else if ( raggruppa == TipoRaggruppamento.TIPO_DOCUMENTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "tipo_documento");
            valuesMap.put("GROUP_BY", "tipo_documento");
            valuesMap.put("ORDER_BY", "tipo_documento");
        }
        else if ( raggruppa == TipoRaggruppamento.PAGAMENTO )
        {
            query = FileQueryReader.getQuery("STATISTICHE_S01");
            valuesMap.put("COL_1", "pagamento");
            valuesMap.put("GROUP_BY", "pagamento");
            valuesMap.put("ORDER_BY", "pagamento");
        }

        if ( mostra == DatoDaMostrare.NUMERO_DOCUMENTI )
        {
            valuesMap.put("COL_2", "COUNT(*)");
        }
        else if ( mostra == DatoDaMostrare.IMPONIBILE )
        {
            String col = isProductQuery ? "prezzoimponibile" : "(totale - iva_debito)";
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCC' then -(" + col + ") else (" + col + ") end), 0)");
        }
        else if ( mostra == DatoDaMostrare.IVA )
        {
            String col = isProductQuery ? "(prezzototale - prezzoimponibile)" : "iva_debito";
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCC' then -(" + col + ") else (" + col + ") end), 0)");
        }
        else if ( mostra == DatoDaMostrare.TOTALE_DOCUMENTO )
        {
            String col = isProductQuery ? "prezzototale" : "totale";
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCC' then -(" + col + ") else (" + col + ") end), 0)");
        }
        else if ( mostra == DatoDaMostrare.QUANTITA_PRODOTTI )
        {
            valuesMap.put("COL_2", "SUM(CASE WHEN tipo_documento = 'NCC' THEN -(quantita_movimento) ELSE (quantita_movimento) END)");
        }
        else if ( mostra == DatoDaMostrare.IMPORTO_PRODOTTI )
        {
            valuesMap.put("COL_2", "SUM(CASE WHEN tipo_documento = 'NCC' THEN -(prezzoimponibile) ELSE (prezzoimponibile) END)");
        }
        else if ( mostra == DatoDaMostrare.IMPORTO_PRODOTTI_IVATO )
        {
            valuesMap.put("COL_2", "SUM(CASE WHEN tipo_documento = 'NCC' THEN -(prezzototale) ELSE (prezzototale) END)");
        }
        else if ( mostra == DatoDaMostrare.PREZZO_MEDIO_PRODOTTI )
        {
            valuesMap.put("COL_2", "AVG(prezzoimponibile)");
        }
        else if ( mostra == DatoDaMostrare.PREZZO_MASSIMO_PRODOTTI )
        {
            valuesMap.put("COL_2", "MAX(prezzoimponibile)");
        }
        else if ( mostra == DatoDaMostrare.PREZZO_MINIMO_PRODOTTI )
        {
            valuesMap.put("COL_2", "MIN(prezzoimponibile)");
        }
        else if ( mostra == DatoDaMostrare.MARGINALITA )
        {
            valuesMap.put("COL_2", "coalesce(SUM(case when tipo_documento = 'NCC' then -(prezzoimponibile - coalesce(prezzo_acquisto, 0) * quantita_movimento) else (prezzoimponibile - coalesce(prezzo_acquisto, 0) * quantita_movimento) end), 0)");
        }
        else if ( mostra == DatoDaMostrare.RICARICO )
        {
            valuesMap.put("COL_2", "coalesce((SUM(prezzoimponibile) / NULLIF(SUM(prezzo_acquisto * quantita_movimento), 0) - 1) * 100, 0)");
        }
        params.add(dtDal);
        params.add(dtAl);
        params.add(cliente);
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            return jdbcTemplate.query(query, new RowMapper<StatisticaDto>()
            {
                @Override
                public StatisticaDto mapRow(ResultSet rs,
                                            int rowNum) throws SQLException
                {
                    StatisticaDto dto = new StatisticaDto();
                    if ( raggruppa == TipoRaggruppamento.MESE || raggruppa == TipoRaggruppamento.GIORNO || raggruppa == TipoRaggruppamento.TRIMESTRE || raggruppa == TipoRaggruppamento.ANNOTEMPORALE )
                    {
                        Timestamp dt = rs.getTimestamp("descrizione");
                        if ( raggruppa == TipoRaggruppamento.GIORNO ) {
                            dto.setDescrizione(DateUtility.format(dt, "dd/MM/yyyy"));
                        } else if ( raggruppa == TipoRaggruppamento.TRIMESTRE ) {
                            try {
                                int month = Integer.parseInt(DateUtility.getMonth(dt));
                                int quarter = (month - 1) / 3 + 1;
                                dto.setDescrizione("Q" + quarter + " " + DateUtility.getYear(dt));
                            } catch (Exception e) {
                                dto.setDescrizione("");
                            }
                        } else if ( raggruppa == TipoRaggruppamento.ANNOTEMPORALE ) {
                            dto.setDescrizione(DateUtility.getYear(dt));
                        } else {
                            String monthText = DateUtility.getMonthText(dt).substring(0, 3);
                            String year = StringUtils.substring(DateUtility.getYear(dt), -2);
                            dto.setDescrizione(new StringBuilder(monthText).append(" ").append(year).toString());
                        }
                    }
                    else
                    {
                        String desc = rs.getString("descrizione");
                        if ( raggruppa == TipoRaggruppamento.TIPO_DOCUMENTO )
                        {
                            desc = formatTipoDocumento(desc);
                        }
                        dto.setDescrizione(desc);
                    }
                    if ( mostra == DatoDaMostrare.NUMERO_DOCUMENTI )
                    {
                        dto.setValore(new Double(rs.getLong("valore")));
                    }
                    else
                    {
                        dto.setValore(rs.getDouble("valore"));
                    }
                    return dto;
                }
            }, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle statistiche di vendita", e);
            throw new SQLException(e);
        }
    }

    public List<StatisticaDto> getVenditeAnnoCorrentePerMese() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<StatisticaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(StatisticaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("STATISTICHE_S03"), rowMapper);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle vendite nell'anno corrente per mese", e);
            throw new SQLException(e);
        }
    }

    private String formatTipoDocumento(String code)
    {
        if ( code == null ) return "";
        switch ( code )
        {
            case "F":
                return "Fattura";
            case "S":
                return "Scontrino / Fatt. Acc.";
            case "NCC":
                return "Nota Credito Cliente";
            case "FF":
                return "Fattura Fornitore";
            case "NCF":
                return "Nota Credito Fornitore";
            default:
                return code;
        }
    }

}
