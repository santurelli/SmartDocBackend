package it.tinna.smartdoc.server.dao.documenti;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import it.tinna.smartdoc.server.database.FileQueryReader;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;

@Repository
public class NoteCreditoDao extends BaseDao {

    @Autowired
    public NoteCreditoDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<NotaCreditoDto> getList(String dataInizio, String dataFine, Integer idCliente, Integer idAgente,
                                      String orderColumn, String orderDir, int start, int length,
                                      String statoFatturaSql, String numDocumentoSql) throws SQLException {
        String query = FileQueryReader.getQuery("NOTECREDITO_S10");
        query = query.replace("${ORDER_BY}", orderColumn + " " + orderDir);
        query = query.replace("${LIMIT}", " LIMIT " + length + " OFFSET " + start);
        query = query.replace("${STATO}", statoFatturaSql);
        query = query.replace("${NUM_DOCUMENTO}", numDocumentoSql);

        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(NotaCreditoDto.class),
                dataInizio, dataFine, idCliente, idAgente);
    }

    public NotaCreditoDto getById(long id) throws SQLException {
        NotaCreditoDto dto = jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S01"),
                new BeanPropertyRowMapper<>(NotaCreditoDto.class), id);
        if (dto != null) {
            dto.setProdotti(getProdotti(id));
            dto.setListaSpeseIncassoFattura(getSpeseIncasso(id));
            dto.setListaScadenzePagamentiDocumento(getScadenze(id));
        }
        return dto;
    }

    public List<ProdottoDocumentoDto> getProdotti(long idNotaCredito) throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITO_S02"),
                new BeanPropertyRowMapper<>(ProdottoDocumentoDto.class), idNotaCredito);
    }

    public long insert(NotaCreditoDto dto) throws SQLException {
        return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_I01"), Long.class,
                (dto.getIdFattura() != null && dto.getIdFattura() > 0) ? dto.getIdFattura() : null, dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(),
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
                dto.getDtLiquidazioneProvvigione(), dto.getCodiceUfficioDestinazione(), dto.getPec(),
                dto.getIdMagazzino(), dto.getTipoComunicazione(), dto.getSplitPayment(),
                dto.getFlFatturaElettronica(),
                dto.getStatoFatturaElettronica() != null ? dto.getStatoFatturaElettronica().name() : null,
                dto.getUserCreated()
        );
    }

    public void update(NotaCreditoDto dto) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_U01"),
                (dto.getIdFattura() != null && dto.getIdFattura() > 0) ? dto.getIdFattura() : null, dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(),
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
                dto.getDtLiquidazioneProvvigione(), dto.getCodiceUfficioDestinazione(), dto.getPec(),
                dto.getIdMagazzino(), dto.getTipoComunicazione(), dto.getSplitPayment(),
                dto.getFlFatturaElettronica(),
                dto.getStatoFatturaElettronica() != null ? dto.getStatoFatturaElettronica().name() : null,
                dto.getUserLastUpdate(), dto.getId()
        );
    }

    public void delete(long id, Long user) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_D03"), user, id);
    }

    public void deleteProdotti(long idNotaCredito) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_D01"), idNotaCredito);
    }

    public void insertProdotto(ProdottoDocumentoDto p, long idNotaCredito) throws SQLException {
        jdbcTemplate.update(FileQueryReader.getQuery("NOTECREDITO_I02"),
                idNotaCredito, p.getIdProdotto(), p.getQuantita(), p.getIdUnitaMisura(),
                p.getPrezzo(), p.getSconto(), p.getPrezzoImponibile(), p.getProvvigione(),
                p.getIdAliquotaIva(), p.getScarica(), p.getNota(),
                p.getIdColore(), p.getIdTaglia(), p.getIdScelta(), p.getIdTono(),
                p.getIdConto(), p.getFmCodice(), p.getFmDescrizione(), p.getFmUnitaMisura(),
                p.getFmTono(), p.getFmScelta(), p.getFmTaglia(), p.getFmColore(), p.getIdDivisione()
        );
    }

    public List<SpesaIncassoDocumentoDto> getSpeseIncasso(long idNotaCredito) throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITO_S03"),
                new BeanPropertyRowMapper<>(SpesaIncassoDocumentoDto.class), idNotaCredito);
    }

    public List<ScadenzaPagamentoDocumentoDto> getScadenze(long idNotaCredito) throws SQLException {
        return jdbcTemplate.query(FileQueryReader.getQuery("NOTECREDITO_S06"),
                new BeanPropertyRowMapper<>(ScadenzaPagamentoDocumentoDto.class), idNotaCredito);
    }

    public Integer getNextNum(String data, int flElettronica) throws SQLException {
        return jdbcTemplate.queryForObject(FileQueryReader.getQuery("NOTECREDITO_S05"), Integer.class,
                flElettronica, data, data, flElettronica, data);
    }
}
