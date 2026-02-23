package it.tinna.smartdoc.server.dao.documenti;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
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

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.server.util.DateUtility;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.documenti.ConfOrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.DettaglioIvaDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.IvaDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ScontrinoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoCassaDto;
import it.tinna.smartdoc.shared.dto.movimentiprodotti.DocumentoCollegatoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;

public class DocumentiDao extends BaseDao
{

    public DocumentiDao(JdbcTemplate jdbcTemplate)
    {
        super(jdbcTemplate);
    }

    public void aggiornaProgrUnivocFileFatturaElettronica(Integer idFattura,
                                                          String progUnivoco) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U04"), progUnivoco, idFattura);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del progressivo univoco fattura elettronica per la fattura {}", idFattura);
            throw new SQLException(e);
        }
    }

    public void associaDoc(long idDocFiglio,
                           String tipoDocFiglio,
                           Integer idDocPadre,
                           String tipoDocPadre) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DOCUMENTI_I01"), idDocPadre, tipoDocPadre, idDocFiglio, tipoDocFiglio);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'associazione del documento {} al documento {}", idDocFiglio, idDocPadre);
            throw new SQLException(e);
        }
    }

    public void associaFatturaADdt(Integer idFattura,
                                   Integer idDdt) throws SQLException
    {
        associaDoc(idFattura, ISharedConstants.TIPODOCASSOCIATO_FATTURA, idDdt, ISharedConstants.TIPODOCASSOCIATO_DDT);
        // QueryRunner qRunner = new QueryRunner();
        // qRunner.update(conn, FileQueryReader.getQuery("DOCUMENTI_I01"), new
        // Object[]{idDdt, ISharedConstants.TIPODOCASSOCIATO_DDT, idFattura,
        // ISharedConstants.TIPODOCASSOCIATO_FATTURA});
    }

    public void deleteConfOrdine(ConfOrdineDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("BOLLECARICO_D04"), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione della conferma d'ordine {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void deleteProdottiScontrino(Integer idScontrino) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("SCONTRINI_D01"), idScontrino);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella cancellazione degli articoli dallo scontrino {}", idScontrino, e);
            throw new SQLException(e);
        }
    }

    public Long getCountDocumenti(String tipoDoc,
                                  Integer ivaCassa,
                                  String dataFatturaFrom,
                                  String dataFatturaTo,
                                  String soggetto) throws SQLException
    {
        String query = FileQueryReader.getQuery("DOCUMENTI_S02");
        List<Object> params = new ArrayList<>();
        Map<String, String> valuesMap = new HashMap<>();
        if ( tipoDoc == null )
        {
            valuesMap.put("TIPO_DOCUMENTO", " = tipo_documento::character varying");
        }
        else if ( tipoDoc.equals(DocumentoDto.TipoDoc.DOC_VENDITA.getValore()) )
        {
            valuesMap.put("TIPO_DOCUMENTO", " IN (?,?,?)");
            params.add(DocumentoDto.TipoDoc.DDT.getValore());
            params.add(DocumentoDto.TipoDoc.FATTURE.getValore());
            params.add(DocumentoDto.TipoDoc.CONF_ORDINE.getValore());
        }
        else if ( tipoDoc.equals(DocumentoDto.TipoDoc.DOC_ACQUISTO.getValore()) )
        {
            valuesMap.put("TIPO_DOCUMENTO", " IN (?,?)");
            params.add(DocumentoDto.TipoDoc.BOLLE_CARICO.getValore());
            params.add(DocumentoDto.TipoDoc.ORDINI_FORNITORE.getValore());
            params.add(DocumentoDto.TipoDoc.NOTA_CREDITO_CLIENTE.getValore());
            params.add(DocumentoDto.TipoDoc.FATTURE_FORNITORE.getValore());
        }
        else
        {
            valuesMap.put("TIPO_DOCUMENTO", " = ?");
            params.add(tipoDoc);
        }
        if ( ivaCassa == null )
        {
            valuesMap.put("IVA_CASSA", " 1=1");
        }
        else
        {
            valuesMap.put("IVA_CASSA", " fl_esigibilitadifferita = 1");
        }
        params.add(dataFatturaFrom);
        params.add(dataFatturaTo);
        params.add(StringUtils.defaultIfEmpty(soggetto, null));
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            return jdbcTemplate.queryForObject(query, Long.class, params.toArray());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del totale documenti di tipo {}", StringUtils.defaultIfBlank(tipoDoc, "<vuoto>"), e);
            throw new SQLException(e);
        }
    }

    /**
     * Restituisce l'ultimo progressivo univoco di invio file fattura elettronica
     * 
     * @return
     * @throws Exception
     */
    public String getCurrentProgressivoInvioFatturaElettronica() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S13"), String.class, ISharedConstants.CONFIGURAZIONE_FATTURAELETTRONICA_PROGRESSIVOUNIVOCOINVIO_CHIAVE);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'ultimo progressivo invio fattura elettronica", e);
            throw new SQLException(e);
        }
    }

    public FatturaDto getFatturaByProgressivoInvio(Integer progressivoInvio) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<FatturaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(FatturaDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S14"), rowMapper, progressivoInvio);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna fattura trovata per il progressivo invio {}", progressivoInvio);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della fattura con progressivo invio {}", progressivoInvio, e);
            throw new SQLException(e);
        }
    }

    public List<DocumentoCollegatoDto> getFattureByCliente(Integer idCliente) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DocumentoCollegatoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DocumentoCollegatoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S10"), rowMapper, idCliente);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco fatture per il cliente {}", idCliente, e);
            throw new SQLException(e);
        }
    }

    public List<DocumentoCollegatoDto> getFattureFornitoreByFornitore(Integer idFornitore) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DocumentoCollegatoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DocumentoCollegatoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTUREFORNITORE_S09"), rowMapper, idFornitore);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco fatture fornitore per il fornitore {}", idFornitore, e);
            throw new SQLException(e);
        }
    }

    public List<DocumentoCollegatoDto> getFattureByDdt(Integer idDdt) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DocumentoCollegatoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DocumentoCollegatoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S08"), rowMapper, idDdt);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco fatture collegate al ddt {}", idDdt, e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getList(String tipoDoc,
                                               Integer ivaCassa,
                                               String dataFatturaFrom,
                                               String dataFatturaTo,
                                               Integer soggetto,
                                               String stato,
                                               String dtStatoFrom,
                                               String dtStatoTo,
                                               String soloDocCompletamentePagati) throws SQLException
    {
        try
        {
            String query = FileQueryReader.getQuery("DOCUMENTI_S01");
            List<Object> params = new ArrayList<>();
            Map<String, String> valuesMap = new HashMap<>();
            if ( tipoDoc == null )
            {
                valuesMap.put("TIPO_DOCUMENTO", " = tipo_documento::character varying");
            }
            else if ( tipoDoc.equals(DocumentoDto.TipoDoc.DOC_VENDITA.getValore()) )
            {
                valuesMap.put("TIPO_DOCUMENTO", " IN (?,?,?)");
                params.add(DocumentoDto.TipoDoc.DDT.getValore());
                params.add(DocumentoDto.TipoDoc.FATTURE.getValore());
                params.add(DocumentoDto.TipoDoc.CONF_ORDINE.getValore());
            }
            else if ( tipoDoc.equals(DocumentoDto.TipoDoc.DOC_ACQUISTO.getValore()) )
            {
                valuesMap.put("TIPO_DOCUMENTO", " IN (?,?)");
                params.add(DocumentoDto.TipoDoc.BOLLE_CARICO.getValore());
                params.add(DocumentoDto.TipoDoc.ORDINI_FORNITORE.getValore());
                params.add(DocumentoDto.TipoDoc.NOTA_CREDITO_CLIENTE.getValore());
                params.add(DocumentoDto.TipoDoc.FATTURE_FORNITORE.getValore());
            }
            else
            {
                valuesMap.put("TIPO_DOCUMENTO", " = ?");
                params.add(tipoDoc);
            }
            params.add(dataFatturaFrom);
            params.add(dataFatturaTo);
            params.add(soggetto);
            if ( ivaCassa == null )
            {
                valuesMap.put("IVA_CASSA", "");
            }
            else
            {
                valuesMap.put("IVA_CASSA", " AND fl_esigibilitadifferita = 1");
            }
            if ( stato == null )
            {
                valuesMap.put("STATO", "");
            }
            else
            {
                if ( stato.equals("P") )
                {
                    if ( tipoDoc.equals(DocumentoDto.TipoDoc.FATTURE.getValore()) )
                    {
                        if ( soloDocCompletamentePagati.equals("1") )
                        {
                            valuesMap.put("STATO", " and totale = get_totalepagato_fattura(id_documento,?) AND totale > get_totalepagato_fattura(id_documento,?)");
                            params.add(dtStatoTo);
                            params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                        }
                        else
                        {
                            valuesMap.put("STATO", " and get_totalepagato_fattura(id_documento,?) > get_totalepagato_fattura(id_documento,?)");
                            params.add(dtStatoTo);
                            params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                        }
                    }
                    else if ( tipoDoc.equals(DocumentoDto.TipoDoc.FATTURE_FORNITORE.getValore()) )
                    {
                        if ( soloDocCompletamentePagati.equals("1") )
                        {
                            valuesMap.put("STATO", " and totale = get_totalepagato_fatturafornitore(id_documento,?) AND totale > get_totalepagato_fatturafornitore(id_documento,?) ");
                            params.add(dtStatoTo);
                            params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                        }
                        else
                        {
                            valuesMap.put("STATO", " and get_totalepagato_fatturafornitore(id_documento,?) > get_totalepagato_fatturafornitore(id_documento,?) ");
                            params.add(dtStatoTo);
                            params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                        }
                    }
                    else if ( tipoDoc.equals(DocumentoDto.TipoDoc.NOTA_CREDITO_CLIENTE.getValore()) )
                    {
                        if ( soloDocCompletamentePagati.equals("1") )
                        {
                            valuesMap.put("STATO", " and totale = get_totalepagato_notacredito(id_documento,?) AND totale > get_totalepagato_notacredito(id_documento,?)");
                            params.add(dtStatoTo);
                            params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                        }
                        else
                        {
                            valuesMap.put("STATO", " and get_totalepagato_notacredito(id_documento,?) > get_totalepagato_notacredito(id_documento,?)");
                            params.add(dtStatoTo);
                            params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                        }
                    }
                    else if ( tipoDoc.equals(DocumentoDto.TipoDoc.NOTA_CREDITO_FORNITORE.getValore()) )
                    {
                        valuesMap.put("STATO", " and totale = get_totalepagato_notacreditofornitore(id_documento,?) AND totale > get_totalepagato_notacreditofornitore(id_documento,?)");
                        params.add(dtStatoTo);
                        params.add(DateUtility.add(DateUtility.parse(dtStatoFrom), -1));
                    }
                }
                else if ( stato.equals("NP") )
                {
                    if ( tipoDoc.equals(DocumentoDto.TipoDoc.FATTURE.getValore()) )
                    {
                        valuesMap.put("STATO", " and totale > get_totalepagato_fattura(id_documento,?)");
                        params.add(dtStatoTo);
                    }
                    else if ( tipoDoc.equals(DocumentoDto.TipoDoc.FATTURE_FORNITORE.getValore()) )
                    {
                        valuesMap.put("STATO", " and totale > get_totalepagato_fatturafornitore(id_documento,?) ");
                        params.add(dtStatoTo);
                    }
                    else if ( tipoDoc.equals(DocumentoDto.TipoDoc.NOTA_CREDITO_CLIENTE.getValore()) )
                    {
                        valuesMap.put("STATO", " and totale > get_totalepagato_notacredito(id_documento,?) ");
                        params.add(dtStatoTo);
                    }
                    else if ( tipoDoc.equals(DocumentoDto.TipoDoc.NOTA_CREDITO_FORNITORE.getValore()) )
                    {
                        valuesMap.put("STATO", " and totale > get_totalepagato_notacreditofornitore(id_documento,?) ");
                        params.add(dtStatoTo);
                    }
                }
                else
                {
                    valuesMap.put("STATO", "");
                }
            }
            query = StrSubstitutor.replace(query, valuesMap);
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException | ParseException e )
        {
            _log.error("Errore nel recupero dei movimenti documento", e);
            throw new SQLException(e);
        }
    }

    public List<DettaglioIvaDocumentoDto> getListDettaglioIvaBollaCarico(Integer idBollaCarico) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DettaglioIvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DettaglioIvaDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("IVADOCUMENTI_S08"), rowMapper, idBollaCarico, idBollaCarico);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del dettaglio iva per la bolla di carico {}", idBollaCarico, e);
            throw new SQLException(e);
        }
    }

    public List<DettaglioIvaDocumentoDto> getListDettaglioIvaFattura(Integer idFattura) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<DettaglioIvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DettaglioIvaDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("IVADOCUMENTI_S05"), rowMapper, idFattura, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del dettaglio iva per la fattura {}", idFattura, e);
            throw new SQLException(e);
        }
    }

    public List<IvaDocumentoDto> getListIvaAcquistiDocumenti(String periodo,
                                                             String anno) throws SQLException
    {
        String query = FileQueryReader.getQuery("IVADOCUMENTI_S04");
        Map<String, String> valuesMap = new HashMap<>();
        if ( periodo.startsWith("T") )
        {
            String dataDa = "";
            String dataA = "";
            String whereCondition = "";
            if ( periodo.endsWith("1") )
            {
                dataDa = "01/" + anno;
                dataA = "03/" + anno;
            }
            else if ( periodo.endsWith("2") )
            {
                dataDa = "04/" + anno;
                dataA = "06/" + anno;
            }
            else if ( periodo.endsWith("3") )
            {
                dataDa = "07/" + anno;
                dataA = "09/" + anno;
            }
            else if ( periodo.endsWith("4") )
            {
                dataDa = "10/" + anno;
                dataA = "12/" + anno;
            }
            whereCondition = " and date_trunc('month', data_documento) >= to_date('" + dataDa + "', 'MM/YYYY') " + " and date_trunc('month', data_documento) <= to_date('" + dataA + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else if ( periodo.startsWith("M") )
        {
            String mese = periodo.substring(1) + "/" + anno;
            String whereCondition = " and date_trunc('month', data_documento) = to_date('" + mese + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else
        {
            valuesMap.put("WHERE_PERIODO", " ");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<IvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(IvaDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, anno);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'iva acquisti documenti per il periodo {} e l'anno {}", periodo, anno, e);
            throw new SQLException(e);
        }
    }

    public List<IvaDocumentoDto> getListIvaVenditeDocumenti(String periodo,
                                                            String anno) throws SQLException
    {
        String query = FileQueryReader.getQuery("IVADOCUMENTI_S03");
        Map<String, String> valuesMap = new HashMap<>();
        if ( periodo.startsWith("T") )
        {
            String dataDa = "";
            String dataA = "";
            String whereCondition = "";
            if ( periodo.endsWith("1") )
            {
                dataDa = "01/" + anno;
                dataA = "03/" + anno;
            }
            else if ( periodo.endsWith("2") )
            {
                dataDa = "04/" + anno;
                dataA = "06/" + anno;
            }
            else if ( periodo.endsWith("3") )
            {
                dataDa = "07/" + anno;
                dataA = "09/" + anno;
            }
            else if ( periodo.endsWith("4") )
            {
                dataDa = "10/" + anno;
                dataA = "12/" + anno;
            }
            whereCondition = " and date_trunc('month', data_iva) >= to_date('" + dataDa + "', 'MM/YYYY') " + " and date_trunc('month', data_iva) <= to_date('" + dataA + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else if ( periodo.startsWith("M") )
        {
            String mese = periodo.substring(1) + "/" + anno;
            String whereCondition = " and date_trunc('month', data_iva) = to_date('" + mese + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else
        {
            valuesMap.put("WHERE_PERIODO", " ");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<IvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(IvaDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, anno);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco vendite documenti", e);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiDdtByIdDdt(Integer idDdt) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("DDT_S02"), rowMapper, idDdt);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco articoli nel ddt {}", idDdt);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiFatturaByIdFattura(Integer idFattura) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S04"), rowMapper, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco degli articoli nella fattura {}", idFattura);
            throw new SQLException(e);
        }
    }

    public List<ProdottoDocumentoDto> getListProdottiScontrinoByIdScontrino(Integer idScontrino) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ProdottoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ProdottoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("SCONTRINI_S02"), rowMapper, idScontrino);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco articoli nello scontrino {}", idScontrino);
            throw new SQLException(e);
        }
    }

    public List<ScadenzaPagamentoDocumentoDto> getListScadenzePagamentoFatturaByIdFattura(Integer idFattura) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S06"), rowMapper, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco scadenze pagamento per la fattura {}", idFattura);
            throw new SQLException(e);
        }
    }

    public Long getNumTotFattureMese() throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S15"), Long.class);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione del numero totale di fatture per mese", e);
            throw new SQLException(e);
        }
    }

    public ScadenzaPagamentoDocumentoDto getScadenzaPagamentoFattura(Integer idScadenzaPagamentoFattura) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScadenzaPagamentoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScadenzaPagamentoDocumentoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S11"), rowMapper, idScadenzaPagamentoFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuna scadenza di pagamento trovata con id {}", idScadenzaPagamentoFattura);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della scadenza di pagamento {}", idScadenzaPagamentoFattura, e);
            throw new SQLException(e);
        }
    }

    public List<SpesaIncassoDocumentoDto> getListSpeseIncassoDdtByIdDdt(Integer idDdt) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<SpesaIncassoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(SpesaIncassoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("DDT_S03"), rowMapper, idDdt);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle spese di incasso per il ddt {}", idDdt);
            throw new SQLException(e);
        }
    }

    public List<TipoCassaDto> getListRitenute() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<TipoCassaDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(TipoCassaDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("TIPOCASSA_S01"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero della lista ritenute", e);
            throw new SQLException(e);
        }
    }

    public List<SpesaIncassoDocumentoDto> getListSpeseIncassoFatturaByIdFattura(Integer idFattura) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<SpesaIncassoDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(SpesaIncassoDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S05"), rowMapper, idFattura);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco spese incasso per la fattura {}", idFattura);
            throw new SQLException(e);
        }
    }

    public List<DettaglioIvaDocumentoDto> getListTotaliIvaAcquistiDocumenti(String periodo,
                                                                            String anno) throws SQLException
    {
        String query = FileQueryReader.getQuery("IVADOCUMENTI_S07");
        Map<String, String> valuesMap = new HashMap<>();
        if ( periodo.startsWith("T") )
        {
            String dataDa = "";
            String dataA = "";
            String whereCondition = "";
            if ( periodo.endsWith("1") )
            {
                dataDa = "01/" + anno;
                dataA = "03/" + anno;
            }
            else if ( periodo.endsWith("2") )
            {
                dataDa = "04/" + anno;
                dataA = "06/" + anno;
            }
            else if ( periodo.endsWith("3") )
            {
                dataDa = "07/" + anno;
                dataA = "09/" + anno;
            }
            else if ( periodo.endsWith("4") )
            {
                dataDa = "10/" + anno;
                dataA = "12/" + anno;
            }
            whereCondition = " and date_trunc('month', d_e_bollecarico.data_bolla) >= to_date('" + dataDa + "', 'MM/YYYY') " + " and date_trunc('month', d_e_bollecarico.data_bolla) <= to_date('" + dataA + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else if ( periodo.startsWith("M") )
        {
            String mese = periodo.substring(1) + "/" + anno;
            String whereCondition = " and date_trunc('month', d_e_bollecarico.data_bolla) = to_date('" + mese + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else
        {
            valuesMap.put("WHERE_PERIODO", " ");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<DettaglioIvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DettaglioIvaDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, anno, anno);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del totale iva acquisti", e);
            throw new SQLException(e);
        }
    }

    public List<DettaglioIvaDocumentoDto> getListTotaliIvaVenditeDocumenti(String periodo,
                                                                           String anno) throws SQLException
    {
        String query = FileQueryReader.getQuery("IVADOCUMENTI_S06");
        Map<String, String> valuesMap = new HashMap<>();
        if ( periodo.startsWith("T") )
        {
            String dataDa = "";
            String dataA = "";
            String whereCondition = "";
            if ( periodo.endsWith("1") )
            {
                dataDa = "01/" + anno;
                dataA = "03/" + anno;
            }
            else if ( periodo.endsWith("2") )
            {
                dataDa = "04/" + anno;
                dataA = "06/" + anno;
            }
            else if ( periodo.endsWith("3") )
            {
                dataDa = "07/" + anno;
                dataA = "09/" + anno;
            }
            else if ( periodo.endsWith("4") )
            {
                dataDa = "10/" + anno;
                dataA = "12/" + anno;
            }
            whereCondition = " and date_trunc('month', d_e_fattura.data_fattura) >= to_date('" + dataDa + "', 'MM/YYYY') " + " and date_trunc('month', d_e_fattura.data_fattura) <= to_date('" + dataA + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else if ( periodo.startsWith("M") )
        {
            String mese = periodo.substring(1) + "/" + anno;
            String whereCondition = " and date_trunc('month', d_e_fattura.data_fattura) = to_date('" + mese + "', 'MM/YYYY') ";
            valuesMap.put("WHERE_PERIODO", whereCondition);
        }
        else
        {
            valuesMap.put("WHERE_PERIODO", " ");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<DettaglioIvaDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(DettaglioIvaDocumentoDto.class);
            return jdbcTemplate.query(query, rowMapper, anno, anno);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero del totale iva vendite", e);
            throw new SQLException(e);
        }
    }

    public List<ScontrinoDto> getListScontrini(String dateFrom,
                                               String dateTo,
                                               Integer length,
                                               Integer start,
                                               Integer orderColumn,
                                               String orderDir) throws SQLException
    {
        String query = FileQueryReader.getQuery("SCONTRINI_S01");
        List<Object> params = new ArrayList<>();
        params.add(StringUtils.isEmpty(dateFrom) ? null : dateFrom);
        params.add(StringUtils.isEmpty(dateTo) ? null : dateTo);
        Map<String, String> valuesMap = new HashMap<>();
        if ( orderColumn == 1 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("d_e_scontrini.data_scontrino ").append(orderDir).toString());
        }
        else if ( orderColumn == 3 )
        {
            valuesMap.put("ORDER_BY", new StringBuilder("get_totale_scontrino(d_e_scontrini.k_d_e_scontrini) ").append(orderDir).toString());
        }
        else
        {
            valuesMap.put("ORDER_BY", new StringBuilder("d_e_scontrini.data_scontrino ").toString());
        }
        if ( length != null && start != null )
        {
            valuesMap.put("LIMIT", "LIMIT ? OFFSET ?");
            params.add(length);
            params.add(start);
        }
        else
        {
            valuesMap.put("LIMIT", "");
        }
        query = StrSubstitutor.replace(query, valuesMap);
        try
        {
            BeanPropertyRowMapper<ScontrinoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScontrinoDto.class);
            return jdbcTemplate.query(query, rowMapper, params.toArray());
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dell'elenco scontrini", e);
            throw new SQLException(e);
        }
    }

    public Integer getNextNumDdt(String data) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DDT_S05"), Integer.class, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return 1;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione del prossimo numero ddt", e);
            throw new SQLException(e);
        }
    }

    public Integer getNextNumFattura(String data,
                                     Integer flFatturaElettronica) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S07"), Integer.class, flFatturaElettronica, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null), flFatturaElettronica, StringUtils.defaultIfEmpty(data, null), StringUtils.defaultIfEmpty(data, null));
        }
        catch ( EmptyResultDataAccessException e )
        {
            return 1;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella determinazione del prossimo numero fattura", e);
            throw new SQLException(e);
        }
    }

    public Integer insertDdt(DdtDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DDT_I01"), Integer.class, dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null), StringUtils.defaultIfEmpty(dto.getDataDocumento(), null), dto.getIdListino(), dto.getIdAgente(), dto.getIdCausaleTrasporto(), StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null), StringUtils.defaultIfEmpty(dto.getTarga(), null), dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(), dto.getColli(), dto.getPallet(), dto.getPesoNetto(), dto.getPesoLordo(), dto.getClienteDto().getId(), dto.getIdTipoPagamento(), dto.getIdNsBanca(), dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(), dto.getAbi(), dto.getCab(), dto.getConto(), dto.getBic(), dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(), dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(), dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(), dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(), dto.getAcconto(), dto.getIdMagazzino(), dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento del ddt", e);
            throw new SQLException(e);
        }
    }

    public Integer insertFattura(FatturaDto dto) throws Exception
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_I01"),
                                               Integer.class,
                                               dto.getNumDocumento(),
                                               StringUtils.defaultIfEmpty(dto.getParticella(), null),
                                               StringUtils.defaultIfEmpty(dto.getDataDocumento(), null),
                                               dto.getIdListino(),
                                               dto.getIdAgente(),
                                               dto.getIdCausaleTrasporto(),
                                               StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null),
                                               StringUtils.defaultIfEmpty(dto.getTarga(), null),
                                               dto.getIdTipoPorto(),
                                               dto.getIdVettore(),
                                               dto.getIdAspettoBeni(),
                                               dto.getColli(),
                                               dto.getPallet(),
                                               dto.getClienteDto().getId(),
                                               dto.getIdTipoPagamento(),
                                               dto.getIdNsBanca(),
                                               dto.getDescrizioneBanca(),
                                               dto.getIban(),
                                               dto.getCin(),
                                               dto.getAbi(),
                                               dto.getCab(),
                                               dto.getConto(),
                                               dto.getBic(),
                                               dto.getIndirizzoIntestazione(),
                                               dto.getCapIntestazione(),
                                               dto.getCittaIntestazione(),
                                               dto.getProvinciaIntestazione(),
                                               dto.getNazioneIntestazione(),
                                               dto.getIndirizzoDestinazione(),
                                               dto.getCapDestinazione(),
                                               dto.getCittaDestinazione(),
                                               dto.getProvinciaDestinazione(),
                                               dto.getNazioneDestinazione(),
                                               StringUtils.isEmpty(dto.getDtLiquidazioneProvvigione()) ? null : DateUtility.toTimestamp(dto.getDtLiquidazioneProvvigione()),
                                               dto.getEsigibilitaDifferita() == null ? 0 : dto.getEsigibilitaDifferita(),
                                               dto.getIdCausaleEsigibilitaDifferita(),
                                               dto.getTipoComunicazione(),
                                               dto.getCodiceUfficioDestinazione(),
                                               dto.getIdMagazzino(),
                                               dto.getSplitPayment(),
                                               dto.getFlFatturaElettronica(),
                                               StringUtils.defaultIfEmpty(dto.getCig(), null),
                                               StringUtils.defaultIfEmpty(dto.getCup(), null),
                                               StringUtils.defaultIfEmpty(dto.getDatiCommessa(), null),
                                               dto.getUserCreated());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della fattura", e);
            throw new SQLException(e);
        }
    }

    public void insertProdottoDdt(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_I02"), dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), dto.getSconto(), dto.getProvvigione(), dto.getIdAliquotaIva(), dto.getScarica(), dto.getNota(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono(), dto.getIdConto(), dto.getFmCodice(), dto.getFmDescrizione(), dto.getFmUnitaMisura(), dto.getFmTono(), dto.getFmScelta(), dto.getFmTaglia(), dto.getFmColore());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'articolo nel ddt {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertProdottoFattura(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_I02"), dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), dto.getSconto(), dto.getProvvigione(), dto.getIdAliquotaIva(), dto.getScarica(), dto.getNota(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono(), dto.getIdConto(), dto.getFmCodice(), dto.getFmDescrizione(), dto.getFmUnitaMisura(), dto.getFmTono(), dto.getFmScelta(), dto.getFmTaglia(), dto.getFmColore());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'articolo nella fattura {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public void insertProdottoScontrino(ProdottoDocumentoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("SCONTRINI_I02"), dto.getIdDocumento(), dto.getIdProdotto(), dto.getQuantita(), dto.getIdUnitaMisura(), dto.getPrezzo(), StringUtils.defaultIfEmpty(dto.getSconto(), null), dto.getIdAliquotaIva(), dto.getIdColore(), dto.getIdTaglia(), dto.getIdScelta(), dto.getIdTono());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dell'articolo nello scontrino {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public Integer insertScadenzaPagamentoFattura(ScadenzaPagamentoDocumentoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_I04"), Integer.class, dto.getIdDocumento(), dto.getDtScadenza(), dto.getImporto(), dto.getIdRisorsa(), dto.getModalitaPagamento(), dto.getImportoSpeseIncasso(), dto.getIvaSpeseIncasso(), dto.getRifPagamento(), dto.getNote(), dto.getSaldato(), dto.getAcconto(), StringUtils.isEmpty(dto.getDtPagamento()) ? null : dto.getDtPagamento());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento della scadenza di pagamento per la fattura {}", dto.getIdDocumento(), e);
            throw new SQLException(e);
        }
    }

    public Integer insertScontrino(ScontrinoDto dto) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCONTRINI_I01"), Integer.class, dto.getUserCreated(), dto.getSconto(), dto.getIdTipoPagamento(), dto.getIdListino(), dto.getIdMagazzino());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'inserimento dello scontrino", e);
            throw new SQLException(e);
        }
    }

    public ScontrinoDto getScontrinoById(Integer id) throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<ScontrinoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(ScontrinoDto.class);
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("SCONTRINI_S01"), rowMapper, id);
        }
        catch ( EmptyResultDataAccessException e )
        {
            _log.error("Nessuno scontrino trovato con id {}", id);
            return null;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero dello scontrino con id {}", id, e);
            throw new SQLException(e);
        }
    }

    /**
     * Calcola il numero totale di fatture e note credito (per verificare che non siamo arrivati al numero massimo)
     * 
     * @return
     * @throws Exception
     */
    public Long getTotDocumentiFatturaElettronica(String dtStart) throws SQLException
    {
        try
        {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("DOCUMENTI_S03"), Long.class, dtStart);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel calcolo del totale delle fatture e note di credito", e);
            throw new SQLException(e);
        }
    }

    public List<MovimentiDocumentoDto> getUltimeFatture() throws SQLException
    {
        try
        {
            BeanPropertyRowMapper<MovimentiDocumentoDto> rowMapper = new BeanPropertyRowMapper<>();
            rowMapper.setMappedClass(MovimentiDocumentoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("DOCUMENTI_S04"), rowMapper);
        }
        catch ( EmptyResultDataAccessException e )
        {
            return new ArrayList<>();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nel recupero delle ultime fatture", e);
            throw new SQLException(e);
        }
    }

    public boolean isFatturaDifferita(Integer idFattura) throws SQLException
    {
        try
        {
            long l = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S09"), Long.class, idFattura);
            return l > 0;
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella decisione su fattura differita per la fattura {}", idFattura, e);
            throw new SQLException(e);
        }
    }

    public void updateDdt(DdtDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("DDT_U01"), dto.getNumDocumento(), StringUtils.defaultIfEmpty(dto.getParticella(), null), StringUtils.defaultIfEmpty(dto.getDataDocumento(), null), dto.getIdListino(), dto.getIdAgente(), dto.getIdCausaleTrasporto(), StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null), StringUtils.defaultIfEmpty(dto.getTarga(), null), dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(), dto.getColli(), dto.getPallet(), dto.getPesoNetto(), dto.getPesoLordo(), dto.getClienteDto().getId(), dto.getIdTipoPagamento(), dto.getIdNsBanca(), dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(), dto.getAbi(), dto.getCab(), dto.getConto(), dto.getBic(), dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(), dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(), dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(), dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(), dto.getAcconto(), dto.getIdMagazzino(), dto.getUserLastUpdate(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del ddt {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public void updateFattura(FatturaDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U01"),
                                dto.getNumDocumento(),
                                StringUtils.defaultIfEmpty(dto.getParticella(), null),
                                StringUtils.defaultIfEmpty(dto.getDataDocumento(), null),
                                dto.getIdListino(),
                                dto.getIdAgente(),
                                dto.getIdCausaleTrasporto(),
                                StringUtils.defaultIfEmpty(dto.getDataOraTrasporto(), null),
                                StringUtils.defaultIfEmpty(dto.getTarga(), null),
                                dto.getIdTipoPorto(),
                                dto.getIdVettore(),
                                dto.getIdAspettoBeni(),
                                dto.getColli(),
                                dto.getPallet(),
                                dto.getClienteDto().getId(),
                                dto.getIdTipoPagamento(),
                                dto.getIdNsBanca(),
                                dto.getDescrizioneBanca(),
                                dto.getIban(),
                                dto.getCin(),
                                dto.getAbi(),
                                dto.getCab(),
                                dto.getConto(),
                                dto.getBic(),
                                dto.getIndirizzoIntestazione(),
                                dto.getCapIntestazione(),
                                dto.getCittaIntestazione(),
                                dto.getProvinciaIntestazione(),
                                dto.getNazioneIntestazione(),
                                dto.getIndirizzoDestinazione(),
                                dto.getCapDestinazione(),
                                dto.getCittaDestinazione(),
                                dto.getProvinciaDestinazione(),
                                dto.getNazioneDestinazione(),
                                StringUtils.isEmpty(dto.getDtLiquidazioneProvvigione()) ? null : DateUtility.toTimestamp(dto.getDtLiquidazioneProvvigione()),
                                dto.getEsigibilitaDifferita() == null ? 0 : dto.getEsigibilitaDifferita(),
                                dto.getIdCausaleEsigibilitaDifferita(),
                                dto.getTipoComunicazione(),
                                dto.getCodiceUfficioDestinazione(),
                                dto.getIdMagazzino(),
                                dto.getSplitPayment(),
                                dto.getFlFatturaElettronica(),
                                StringUtils.defaultIfEmpty(dto.getCig(), null),
                                StringUtils.defaultIfEmpty(dto.getCup(), null),
                                StringUtils.defaultIfEmpty(dto.getDatiCommessa(), null),
                                dto.getUserLastUpdate(),
                                dto.getId());
        }
        catch ( DataAccessException | ParseException e )
        {
            _log.error("Errore nell'aggiornamento della fattura {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    /**
     * Aggiorna il progressivo univoco di invio file fattura elettronica
     * 
     * @param progr
     * @throws Exception
     */
    public void updateProgressivoInvioFatturaElettronica(String progr) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U03"), progr, ISharedConstants.CONFIGURAZIONE_FATTURAELETTRONICA_PROGRESSIVOUNIVOCOINVIO_CHIAVE);
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento del progressivo invio fattura elettronica", e);
            throw new SQLException(e);
        }
    }

    public void updateScontrino(ScontrinoDto dto) throws SQLException
    {
        try
        {
            jdbcTemplate.update(FileQueryReader.getQuery("SCONTRINI_U01"), dto.getSconto(), dto.getIdTipoPagamento(), dto.getId());
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nell'aggiornamento dello scontrino {}", dto.getId(), e);
            throw new SQLException(e);
        }
    }

    public Integer creaProgInvioFatturaElettronica(Integer idFattura,
                                                   String tipoDocumento) throws SQLException
    {
        try
        {
            BigDecimal b = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S12"), BigDecimal.class, idFattura, tipoDocumento);
            return b.intValue();
        }
        catch ( DataAccessException e )
        {
            _log.error("Errore nella creazione del progressivo univoco di invio fattura elettronica", e);
            throw new SQLException(e);
        }
    }

}

