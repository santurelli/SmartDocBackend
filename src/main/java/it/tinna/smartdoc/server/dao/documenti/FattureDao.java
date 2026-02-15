package it.tinna.smartdoc.server.dao.documenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;

@Repository
public class FattureDao extends BaseDao {

    @Autowired
    public FattureDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<FatturaDto> getList(String dataInizio, String dataFine, Integer idCliente, Integer idAgente, String statoFatturaElettronica,
                                   String orderColumn, String orderDir, int start, int length,
                                   String tipoFatturaSql, String statoFatturaSql, String numDocumentoSql) throws SQLException {
        String query = FileQueryReader.getQuery("FATTURE_S16");
        query = query.replace("${ORDER_BY}", orderColumn + " " + orderDir);
        query = query.replace("${LIMIT}", " LIMIT " + length + " OFFSET " + start);
        query = query.replace("${TIPO_FATTURA}", tipoFatturaSql);
        query = query.replace("${STATO}", statoFatturaSql);
        query = query.replace("${NUM_DOCUMENTO}", numDocumentoSql);

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(FatturaDto.class),
                dataInizio, dataFine, idCliente, idAgente, statoFatturaElettronica);
    }

    public FatturaDto getById(long id) throws SQLException {
        FatturaDto dto = jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S03"),
                new BeanPropertyRowMapper<>(FatturaDto.class), id);
        if (dto != null) {
            dto.setProdotti(getProdotti(id));
            dto.setListaSpeseIncassoFattura(getSpeseIncasso(id));
            dto.setListaScadenzePagamentiDocumento(getScadenze(id));
        }
        return dto;
    }

    public List<ProdottoDocumentoDto> getProdotti(long idFattura) throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S04"),
                new BeanPropertyRowMapper<>(ProdottoDocumentoDto.class), idFattura);
    }

    public List<SpesaIncassoDocumentoDto> getSpeseIncasso(long idFattura) throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S05"),
                new BeanPropertyRowMapper<>(SpesaIncassoDocumentoDto.class), idFattura);
    }

    public long insert(FatturaDto dto) throws SQLException {
        return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_I01"), Long.class,
                dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(),
                dto.getIdListino(), dto.getIdAgente(), dto.getIdProgetto(),
                dto.getIdCausaleTrasporto(), dto.getDataOraTrasporto(), dto.getTarga(),
                dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(),
                dto.getColli(), dto.getPallet(), dto.getPesoNetto(), dto.getPesoLordo(),
                dto.getIdCliente(), dto.getIdTipoPagamento(), dto.getIdNsBanca(),
                dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(), dto.getAbi(), dto.getCab(),
                dto.getConto(), dto.getBic(),
                dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(),
                dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(),
                dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(),
                dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(),
                dto.getEsigibilitaDifferita(), dto.getIdCausaleEsigibilitaDifferita(),
                dto.getTipoComunicazione(), dto.getCodiceUfficioDestinazione(), dto.getPec(),
                dto.getIdMagazzino(), dto.getSplitPayment(), dto.getFlFatturaElettronica(),
                dto.getCausale(), dto.getNumeroOrdineAcquisto(), dto.getDataOrdineAcquisto(),
                dto.getCig(), dto.getCup(), dto.getDatiCommessa(),
                dto.getNumeroScontrino(), dto.getDataScontrino(),
                dto.getTipoFattura() != null ? dto.getTipoFattura().name() : null,
                dto.getStatoFatturaElettronica() != null ? dto.getStatoFatturaElettronica().name() : null,
                dto.getIdFatturaCollegata() > 0 ? dto.getIdFatturaCollegata() : null, dto.getUserCreated()
        );
    }

    public void update(FatturaDto dto) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_U01"),
                dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(),
                dto.getIdListino(), dto.getIdAgente(), dto.getIdProgetto(),
                dto.getIdCausaleTrasporto(), dto.getDataOraTrasporto(), dto.getTarga(),
                dto.getIdTipoPorto(), dto.getIdVettore(), dto.getIdAspettoBeni(),
                dto.getColli(), dto.getPallet(), dto.getPesoNetto(), dto.getPesoLordo(),
                dto.getIdCliente(), dto.getIdTipoPagamento(), dto.getIdNsBanca(),
                dto.getDescrizioneBanca(), dto.getIban(), dto.getCin(), dto.getAbi(), dto.getCab(),
                dto.getConto(), dto.getBic(),
                dto.getIndirizzoIntestazione(), dto.getCapIntestazione(), dto.getCittaIntestazione(),
                dto.getProvinciaIntestazione(), dto.getNazioneIntestazione(),
                dto.getIndirizzoDestinazione(), dto.getCapDestinazione(), dto.getCittaDestinazione(),
                dto.getProvinciaDestinazione(), dto.getNazioneDestinazione(),
                dto.getEsigibilitaDifferita(), dto.getIdCausaleEsigibilitaDifferita(),
                dto.getTipoComunicazione(), dto.getCodiceUfficioDestinazione(), dto.getPec(),
                dto.getIdMagazzino(), dto.getSplitPayment(), dto.getFlFatturaElettronica(),
                dto.getCausale(), dto.getNumeroOrdineAcquisto(), dto.getDataOrdineAcquisto(),
                dto.getCig(), dto.getCup(), dto.getDatiCommessa(),
                dto.getNumeroScontrino(), dto.getDataScontrino(),
                dto.getTipoFattura() != null ? dto.getTipoFattura().name() : null,
                dto.getStatoFatturaElettronica() != null ? dto.getStatoFatturaElettronica().name() : null,
                dto.getIdFatturaCollegata() > 0 ? dto.getIdFatturaCollegata() : null, dto.getUserLastUpdate(), dto.getId()
        );
    }

    public void delete(long id, Long user) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D04"), user, id);
    }

    public void deleteProdotti(long idFattura) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_D01"), idFattura);
    }

    public void insertProdotto(ProdottoDocumentoDto p, long idFattura) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("FATTURE_I02"),
                idFattura, p.getIdProdotto(), p.getQuantita(), p.getIdUnitaMisura(),
                p.getPrezzo(), p.getSconto(), p.getPrezzoImponibile(), p.getProvvigione(),
                p.getIdAliquotaIva(), p.getScarica(), p.getNota(),
                p.getIdColore(), p.getIdTaglia(), p.getIdScelta(), p.getIdTono(),
                p.getIdConto(), p.getFmCodice(), p.getFmDescrizione(), p.getFmUnitaMisura(),
                p.getFmTono(), p.getFmScelta(), p.getFmTaglia(), p.getFmColore(), p.getIdDivisione()
        );
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenze(long idFattura) throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("FATTURE_S06"),
                new BeanPropertyRowMapper<>(ScadenzaPagamentoDocumentoDto.class), idFattura);
    }

    public Integer getNextNum(String data, int flElettronica, String tipo) throws SQLException {
        return jdbcTemplate.queryForObject(FileQueryReader.getQuery("FATTURE_S07"), Integer.class,
                flElettronica, tipo, data, data, flElettronica, tipo, data);
    }
}
