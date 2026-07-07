package it.tinna.smartdoc.server.dao.prodotti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import it.tinna.smartdoc.server.dao.BaseDao;
import it.tinna.smartdoc.server.database.FileQueryReader;
import it.tinna.smartdoc.shared.dto.prodotti.PrezzoProdottoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.server.util.StringUtility;

@Repository
public class ProdottiDao extends BaseDao {

    public ProdottiDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    public List<ProdottoDto> getList(String categoria, String search, int length, int start, int orderColumn, String orderDir, 
            Double giacenza, String operatoreGiacenza, Integer idFornitore, Integer idTono, Integer idCalibro, Integer idFormato, Integer idScelta) throws SQLException {
        BeanPropertyRowMapper<ProdottoDto> rowMapper = new BeanPropertyRowMapper<>(ProdottoDto.class);
        try {
            String query = FileQueryReader.getQuery("PRODOTTI_S01");
            
            // Handle filters
            List<Object> args = new ArrayList<>();

            // 1. Categoria (Integer or null for all) - Matches: COALESCE(?, d_e_prodotti.k_d_e_categorie, 0)
            Integer idCategoria = null;
            if (StringUtils.hasText(categoria) && !"0".equals(categoria)) {
                try {
                    idCategoria = Integer.parseInt(categoria);
                } catch (NumberFormatException e) { }
            }
            args.add(idCategoria);
            
            // 2, 3, 4. Search (String) - Used 3 times in query
            // Matches:
            // LOWER(codice) LIKE COALESCE(LOWER(?), LOWER(codice))
            // LOWER(descrizione) LIKE COALESCE(LOWER(?), LOWER(descrizione))
            // COALESCE(LOWER(desc_cat), '') LIKE COALESCE(LOWER(?), ...)
            String searchLike = StringUtility.formatForLikeHelper(search);
            args.add(searchLike);
            args.add(searchLike);
            args.add(searchLike);
            
            // Inject Advanced Filters BEFORE "ORDER BY"
            StringBuilder sb = new StringBuilder();
            
            // Giacenza
            if (giacenza != null) {
                // Validate operator
                String op = ">=";
                if (operatoreGiacenza != null) {
                    if ("=".equals(operatoreGiacenza) || ">=".equals(operatoreGiacenza) || "<=".equals(operatoreGiacenza) || ">".equals(operatoreGiacenza) || "<".equals(operatoreGiacenza)) {
                        op = operatoreGiacenza;
                    }
                }
                sb.append(" AND get_totale_disponibile(d_e_prodotti.k_d_e_prodotti, (SELECT k_d_e_magazzini FROM d_e_magazzini WHERE fl_predefinito=1 AND fl_deleted=0 LIMIT 1)) ").append(op).append(" ? ");
                args.add(giacenza);
            }
            
            // Fornitore
            if (idFornitore != null) {
                sb.append(" AND k_d_e_fornitori = ? ");
                args.add(idFornitore);
            }
            
            // Tono
            if (idTono != null) {
                sb.append(" AND k_d_e_toniarticolo = ? ");
                args.add(idTono);
            }
            
            // Calibro
            if (idCalibro != null) {
                sb.append(" AND k_d_e_calibriarticolo = ? ");
                args.add(idCalibro);
            }

            // Formato
            if (idFormato != null) {
                sb.append(" AND k_d_e_formatiarticolo = ? ");
                args.add(idFormato);
            }

            // Scelta
            if (idScelta != null) {
                sb.append(" AND k_d_e_sceltearticolo = ? ");
                args.add(idScelta);
            }
            
            query = query.replace("${EXTRA_FILTERS}", sb.toString());
            
            // Dynamic Ordering
            String orderBy = "descrizione"; // Default
            switch (orderColumn) {
                case 0: orderBy = "codice"; break;
                case 1: orderBy = "descrizione"; break;
                case 2: orderBy = "descCategoria"; break;
                case 4: orderBy = "quantitaEsistente"; break;
                case 5: orderBy = "(quantitaEsistente - quantitaImpegnata)"; break;
            }
            
            // Note: Postgres is case insensitive for unquoted identifiers.
            if (orderColumn == 2) orderBy = "descCategoria";
            if (orderColumn == 3) orderBy = "descSottoCategoria";

            String innerOrderBy = orderBy;
            String outerOrderBy = orderBy;

            if (orderColumn == 4) {
                 // Esistenza
                 innerOrderBy = "get_totale_disponibile(d_e_prodotti.k_d_e_prodotti, (SELECT k_d_e_magazzini FROM d_e_magazzini WHERE fl_predefinito=1 AND fl_deleted=0 LIMIT 1))";
                 outerOrderBy = "CASE x.tipologia WHEN 'S' THEN 0 WHEN 'A' THEN 0 ELSE get_totale_disponibile(x.id, (SELECT k_d_e_magazzini FROM d_e_magazzini WHERE fl_predefinito=1 AND fl_deleted=0 LIMIT 1)) END";
            } else if (orderColumn == 5) {
                 // Disponibile (Esistenza - Impegnato)
                 
                 // Inner Logic (uses d_e_prodotti table)
                 String innerEsistenza = "get_totale_disponibile(d_e_prodotti.k_d_e_prodotti, (SELECT k_d_e_magazzini FROM d_e_magazzini WHERE fl_predefinito=1 AND fl_deleted=0 LIMIT 1))";
                 String innerImpegnato = "COALESCE((SELECT SUM(quantita) " +
                                       "FROM d_e_prodotti_confordine " +
                                       "JOIN d_e_confordine ON d_e_prodotti_confordine.k_d_e_confordine = d_e_confordine.k_d_e_confordine " +
                                       "WHERE d_e_confordine.fl_deleted = 0 " +
                                       "AND d_e_prodotti_confordine.k_d_e_prodotti = d_e_prodotti.k_d_e_prodotti " +
                                       "AND NOT (d_e_confordine.k_d_e_confordine IN (SELECT d_r_doccollegati.id_docpadre FROM d_r_doccollegati WHERE CAST(d_r_doccollegati.tipo_docpadre AS text) = 'CONF_ORDINE'))), 0)";
                 
                 innerOrderBy = "(" + innerEsistenza + " - " + innerImpegnato + ")";

                 // Outer Logic (uses x alias from subquery)
                 // We must replicate the full expression because calculating on aliases (alias1 - alias2) is often not supported in ORDER BY
                 String outerEsistenza = "CASE x.tipologia WHEN 'S' THEN 0 WHEN 'A' THEN 0 ELSE get_totale_disponibile(x.id, (SELECT k_d_e_magazzini FROM d_e_magazzini WHERE fl_predefinito=1 AND fl_deleted=0 LIMIT 1)) END";
                 
                 // Repoint subquery reference to x.id
                 String outerImpegnato = innerImpegnato.replace("d_e_prodotti.k_d_e_prodotti", "x.id");
                 // Wrap in CASE for consistency with SELECT list
                 outerImpegnato = "CASE x.tipologia WHEN 'S' THEN 0 WHEN 'A' THEN 0 ELSE " + outerImpegnato + " END";

                 outerOrderBy = "(" + outerEsistenza + " - " + outerImpegnato + ")";
            }

            query = query.replace("${ORDER_BY}", innerOrderBy + " " + orderDir);
            query = query.replace("${ORDER_BY_2}", outerOrderBy + " " + orderDir); // Replace outer order too

            query = query.replace("${ORDER_BY}", innerOrderBy + " " + orderDir);
            query = query.replace("${ORDER_BY_2}", outerOrderBy + " " + orderDir); // Replace outer order too
            query = query.replace("${LIMIT}", "OFFSET " + start + " LIMIT " + length);

            return jdbcTemplate.query(query, rowMapper, args.toArray());
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public ProdottoDto getById(long id) throws SQLException {
        try {
            BeanPropertyRowMapper<ProdottoDto> rowMapper = new BeanPropertyRowMapper<>(ProdottoDto.class);
            ProdottoDto prodotto = jdbcTemplate.queryForObject(FileQueryReader.getQuery("PRODOTTI_S02"), rowMapper, id);
            
            // Load Prices
            if (prodotto != null) {
                prodotto.setPrezzi(getPrezzi(id));
                // Load Barcodes
                // prodotto.setCodiciBarre(getCodiciBarre(id));
            }
            return prodotto;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public List<PrezzoProdottoDto> getPrezzi(long idProdotto) {
        try {
            BeanPropertyRowMapper<PrezzoProdottoDto> rowMapper = new BeanPropertyRowMapper<>(PrezzoProdottoDto.class);
            return jdbcTemplate.query(FileQueryReader.getQuery("PREZZIPRODOTTO_S01"), rowMapper, idProdotto);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public long insert(ProdottoDto dto) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PRODOTTI_I01"), Long.class,
                dto.getCodice(),
                dto.getDescrizione(),
                dto.getTipologia(),
                dto.getGestMagazzino(),
                dto.getIdDivisione() != 0 ? dto.getIdDivisione() : null,
                dto.getIdConto() != 0 ? dto.getIdConto() : null,
                dto.getIdCategoria() != null && dto.getIdCategoria() != 0 ? dto.getIdCategoria() : null,
                dto.getIdSottoCategoria() != null && dto.getIdSottoCategoria() != 0 ? dto.getIdSottoCategoria() : null,
                dto.getIdFormato() != 0 ? dto.getIdFormato() : null,
                dto.getIdScelta() != 0 ? dto.getIdScelta() : null,
                dto.getIdTono() != 0 ? dto.getIdTono() : null,
                dto.getIdCalibro() != 0 ? dto.getIdCalibro() : null,
                dto.getMqBox(),
                dto.getPezziBox(),
                dto.getIdAliquotaIva() != null && dto.getIdAliquotaIva() != 0 ? dto.getIdAliquotaIva() : null,
                dto.getIdFornitore() != null && dto.getIdFornitore() != 0 ? dto.getIdFornitore() : null,
                dto.getCodicePerFornitore(),
                dto.getPrezzoFornitore(),
                dto.getNote(),
                dto.getIdUnitaMisura1() != null && dto.getIdUnitaMisura1() != 0 ? dto.getIdUnitaMisura1() : null,
                dto.getEquivUnitaMisura(),
                dto.getIdUnitaMisura2() != null && dto.getIdUnitaMisura2() != 0 ? dto.getIdUnitaMisura2() : null,
                dto.getIdUnitaMisuraVendita() != null && dto.getIdUnitaMisuraVendita() != 0 ? dto.getIdUnitaMisuraVendita() : null,
                dto.getEquivUnitaMisuraVendita(),
                dto.getColli(),
                dto.getPallet(),
                dto.getAssemblato(),
                dto.getIdUnitaMisuraDimensioni() != null && dto.getIdUnitaMisuraDimensioni() != 0 ? dto.getIdUnitaMisuraDimensioni() : null,
                dto.getAltezzaNetta(),
                dto.getAltezzaLorda(),
                dto.getLarghezzaNetta(),
                dto.getLarghezzaLorda(),
                dto.getProfonditaNetta(),
                dto.getProfonditaLorda(),
                dto.getIdUnitaMisuraPeso() != null && dto.getIdUnitaMisuraPeso() != 0 ? dto.getIdUnitaMisuraPeso() : null,
                dto.getPesoNetto(),
                dto.getPesoLordo(),
                dto.getObsoleto(),
                dto.getEsclusoDaProvvigione(),
                dto.getIdSottocontoArticolo() != null && dto.getIdSottocontoArticolo() != 0 ? dto.getIdSottocontoArticolo() : null,
                dto.getIdGruppoTaglia(),
                dto.getIdGruppoTono(),
                dto.getScortaMinima(),
                dto.getSezione(),
                dto.getSerie(),
                dto.getDiametro(),
                dto.getScultura(),
                dto.getIndiceVelocita(),
                dto.getDot(),
                dto.getUserCreated() 
            );
        } catch (DataAccessException e) {
             _log.error("Errore durante l'inserimento del prodotto {}", dto.getCodice(), e);
             throw new SQLException(e);
        }
    }

    public void update(ProdottoDto dto) throws SQLException {
         try {
            jdbcTemplate.update(FileQueryReader.getQuery("PRODOTTI_U01"),
                dto.getCodice(),
                dto.getDescrizione(),
                dto.getTipologia(),
                dto.getGestMagazzino(),
                dto.getIdDivisione() != 0 ? dto.getIdDivisione() : null,
                dto.getIdConto() != 0 ? dto.getIdConto() : null,
                dto.getIdCategoria() != null && dto.getIdCategoria() != 0 ? dto.getIdCategoria() : null,
                dto.getIdSottoCategoria() != null && dto.getIdSottoCategoria() != 0 ? dto.getIdSottoCategoria() : null,
                dto.getIdFormato() != 0 ? dto.getIdFormato() : null,
                dto.getIdScelta() != 0 ? dto.getIdScelta() : null,
                dto.getIdTono() != 0 ? dto.getIdTono() : null,
                dto.getIdCalibro() != 0 ? dto.getIdCalibro() : null,
                dto.getMqBox(),
                dto.getPezziBox(),
                dto.getIdAliquotaIva() != null && dto.getIdAliquotaIva() != 0 ? dto.getIdAliquotaIva() : null,
                dto.getIdFornitore() != null && dto.getIdFornitore() != 0 ? dto.getIdFornitore() : null,
                dto.getCodicePerFornitore(),
                dto.getPrezzoFornitore(),
                dto.getNote(),
                dto.getIdUnitaMisura1() != null && dto.getIdUnitaMisura1() != 0 ? dto.getIdUnitaMisura1() : null,
                dto.getEquivUnitaMisura(),
                dto.getIdUnitaMisura2() != null && dto.getIdUnitaMisura2() != 0 ? dto.getIdUnitaMisura2() : null,
                dto.getIdUnitaMisuraVendita() != null && dto.getIdUnitaMisuraVendita() != 0 ? dto.getIdUnitaMisuraVendita() : null,
                dto.getEquivUnitaMisuraVendita(),
                dto.getColli(),
                dto.getPallet(),
                dto.getAssemblato(),
                dto.getIdUnitaMisuraDimensioni() != null && dto.getIdUnitaMisuraDimensioni() != 0 ? dto.getIdUnitaMisuraDimensioni() : null,
                dto.getAltezzaNetta(),
                dto.getAltezzaLorda(),
                dto.getLarghezzaNetta(),
                dto.getLarghezzaLorda(),
                dto.getProfonditaNetta(),
                dto.getProfonditaLorda(),
                dto.getIdUnitaMisuraPeso() != null && dto.getIdUnitaMisuraPeso() != 0 ? dto.getIdUnitaMisuraPeso() : null,
                dto.getPesoNetto(),
                dto.getPesoLordo(),
                dto.getObsoleto(),
                dto.getEsclusoDaProvvigione(),
                dto.getIdSottocontoArticolo() != null && dto.getIdSottocontoArticolo() != 0 ? dto.getIdSottocontoArticolo() : null,
                dto.getIdGruppoTaglia(),
                dto.getIdGruppoTono(),
                dto.getScortaMinima(),
                dto.getSezione(),
                dto.getSerie(),
                dto.getDiametro(),
                dto.getScultura(),
                dto.getIndiceVelocita(),
                dto.getDot(),
                dto.getUserLastUpdate(),
                dto.getId()
            );
        } catch (DataAccessException e) {
             throw new SQLException(e);
        }
    }

    public String getProssimoCodice() throws SQLException {
        try {
            return jdbcTemplate.queryForObject(FileQueryReader.getQuery("PRODOTTI_S05"), String.class);
        } catch (DataAccessException e) {
             throw new SQLException(e);
        }
    }
    
    public Map<String, Object> getCombosMap() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        return map;
    }

    public boolean isExistentCodice(String codice, Integer id) throws SQLException {
         try {
            Long count = jdbcTemplate.queryForObject(FileQueryReader.getQuery("PRODOTTI_S03"), Long.class, codice, id);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }

    public void delete(long id, Object user) throws SQLException {
        try {
            // Soft delete article
            jdbcTemplate.update(FileQueryReader.getQuery("PRODOTTI_D01"), user, id);
            // Delete associated prices
            jdbcTemplate.update(FileQueryReader.getQuery("PRODOTTI_D02"), id);
        } catch (DataAccessException e) {
            throw new SQLException(e);
        }
    }
}

