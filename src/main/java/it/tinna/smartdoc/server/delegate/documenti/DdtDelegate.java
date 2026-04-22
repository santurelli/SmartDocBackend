package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.DdtDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.agenti.AgentiDelegate;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.server.delegate.clienti.ClientiDelegate;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.server.delegate.progetti.ProgettiDelegate;
import it.tinna.smartdoc.server.delegate.risorse.RisorseDelegate;
import it.tinna.smartdoc.server.delegate.tipipagamento.TipiPagamentoDelegate;
import it.tinna.smartdoc.server.delegate.tipiporto.TipiPortoDelegate;
import it.tinna.smartdoc.server.delegate.unitamisura.UnitaMisuraDelegate;
import it.tinna.smartdoc.server.delegate.vettori.VettoriDelegate;
import it.tinna.smartdoc.server.delegate.aspettobeni.AspettoBeniDelegate;
import it.tinna.smartdoc.server.delegate.causalitrasporto.CausaliTrasportoDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;

@Service
public class DdtDelegate extends BaseDelegate {

    @Autowired
    private DdtDao ddtDao;

    @Autowired
    private ClientiDelegate clientiDelegate;
    
    @Autowired
    private AgentiDelegate agentiDelegate;
    
    @Autowired
    private ProgettiDelegate progettiDelegate;

    @Autowired
    private AliquoteIvaDelegate aliquoteIvaDelegate;

    @Autowired
    private UnitaMisuraDelegate unitaMisuraDelegate;

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    @Autowired
    private TipiPagamentoDelegate tipiPagamentoDelegate;

    @Autowired
    private ListiniDelegate listiniDelegate;

    @Autowired
    private RisorseDelegate risorseDelegate;

    @Autowired
    private DatiAziendaDelegate datiaziendaDelegate;

    @Autowired
    private VettoriDelegate vettoriDelegate;

    @Autowired
    private TipiPortoDelegate tipiPortoDelegate;

    @Autowired
    private AspettoBeniDelegate aspettoBeniDelegate;

    @Autowired
    private CausaliTrasportoDelegate causaliTrasportoDelegate;

    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, long userId) throws SQLException {
        DdtDto dto = new DdtDto();
        dto.setId(id);
        dto.setUserLastUpdate(userId);
        ddtDao.delete(dto);
    }

    public DdtDto getById(long id) throws Exception {
        DdtDto dto = ddtDao.getById(id);
        if (dto != null) {
            dto.setClienteDto(clientiDelegate.getById(dto.getIdCliente()));
            if (dto.getIdProgetto() != null && dto.getIdProgetto() != 0) {
                dto.setProgettoDto(progettiDelegate.getById(dto.getIdProgetto().longValue()));
            }
            if (dto.getIdAgente() != null && dto.getIdAgente() != 0) {
                dto.setAgenteDto(agentiDelegate.getById(dto.getIdAgente()));
            }
            List<ProdottoDocumentoDto> listProdottiDdt = ddtDao.getListProdottiById(dto.getId());
            dto.setProdotti(listProdottiDdt);
            dto.setListaSpeseIncassoFattura(ddtDao.getListSpeseIncassoById(dto.getId()));
        }
        return dto;
    }

    public DatatablesResponseDto<MovimentiDocumentoDto> getList(Integer idCliente,
                                                                 String dtFrom,
                                                                 String dtTo,
                                                                 Integer idAgente,
                                                                 Integer idDocumento,
                                                                 Integer length,
                                                                 Integer start,
                                                                 String orderColumn,
                                                                 String orderDir) throws SQLException {
        List<MovimentiDocumentoDto> list = ddtDao.getList(idCliente, dtFrom, dtTo, idAgente, idDocumento, length, start, orderColumn, orderDir);
        long total = 0;
        if (!list.isEmpty()) {
            total = list.get(0).getTotal();
        }
        DatatablesResponseDto<MovimentiDocumentoDto> response = new DatatablesResponseDto<>();
        response.setList(list);
        response.setTotalCount(total);
        response.setTotalFiltered(total);
        return response;
    }

    public Integer getNextNum(String data) throws SQLException {
        return ddtDao.getNextNum(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public long insert(DdtDto dto) throws SQLException {
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getId()) )
        {
            throw new SQLException("Il numero di DDT " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        long id = ddtDao.insert(dto);
        dto.setId(id);
        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto pDto : dto.getProdotti()) {
                pDto.setIdDocumento(id);
                ddtDao.insertProdotto(pDto);
            }
        }
        if (dto.getListaSpeseIncassoFattura() != null) {
            for (SpesaIncassoDocumentoDto sDto : dto.getListaSpeseIncassoFattura()) {
                sDto.setIdFattura(id);
                ddtDao.insertSpesaIncasso(sDto);
            }
        }
        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(DdtDto dto) throws SQLException {
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getId()) )
        {
            throw new SQLException("Il numero di DDT " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        ddtDao.update(dto);
        ddtDao.deleteProdottiById(dto.getId());
        ddtDao.deleteSpeseIncassoById(dto.getId());
        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto pDto : dto.getProdotti()) {
                pDto.setIdDocumento(dto.getId());
                ddtDao.insertProdotto(pDto);
            }
        }
        if (dto.getListaSpeseIncassoFattura() != null) {
            for (SpesaIncassoDocumentoDto sDto : dto.getListaSpeseIncassoFattura()) {
                sDto.setIdFattura(dto.getId());
                ddtDao.insertSpesaIncasso(sDto);
            }
        }
    }

    public Map<String, Object> getCombosMap() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_LISTINI, listiniDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_RISORSE, risorseDelegate.getListForCombo("BA"));
        map.put(ISharedConstants.COMBOSMAP_KEY_AGENTI, agentiDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_PROGETTI, progettiDelegate.getListForCombo());
        
        // Specific combos for DDT
        map.put("ASPETTIBENI", aspettoBeniDelegate.getListForCombo());
        map.put("CAUSALITRASPORTO", causaliTrasportoDelegate.getListForCombo());
        map.put("TIPIPORTO", tipiPortoDelegate.getListForCombo());
        map.put("VETTORI", vettoriDelegate.getListForCombo());

        String particelleAsString = configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_PARTICELLE);
        if (StringUtils.isNotEmpty(particelleAsString)) {
            map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, StringUtils.split(particelleAsString, "\r\n"));
        } else {
            map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, new String[0]);
        }
        return map;
    }

    public DocumentoWrapperDto esportaDdtPdf(String dbKey, long id) {
        _log.info("Inizio generazione PDF per DDT ID: {}", id);
        try {
            DdtDto dto = getById(id);
            if (dto == null) {
                _log.error("DDT non trovato per ID: {}", id);
                throw new Exception("DDT non trovato: " + id);
            }
            _log.info("Dati DDT recuperati con successo per ID: {}", id);

            it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto daDto = datiaziendaDelegate.getDatiAzienda();
            
            // Popolamento campi per la stampa
            StringBuilder fatturareA = new StringBuilder();
            if (dto.getClienteDto() != null) {
                fatturareA.append(dto.getClienteDto().getDenominazione()).append("\n");
                fatturareA.append(StringUtils.defaultString(dto.getIndirizzoIntestazione())).append("\n");
                fatturareA.append(StringUtils.defaultString(dto.getCapIntestazione())).append(" ");
                fatturareA.append(StringUtils.defaultString(dto.getCittaIntestazione())).append(" ");
                if (StringUtils.isNotEmpty(dto.getProvinciaIntestazione())) {
                    fatturareA.append("(").append(dto.getProvinciaIntestazione()).append(")");
                }
                fatturareA.append("\n").append(StringUtils.defaultString(dto.getNazioneIntestazione()));
            }
            dto.setFatturareA(fatturareA.toString());

            StringBuilder luogoDest = new StringBuilder();
            luogoDest.append(StringUtils.defaultString(dto.getIndirizzoDestinazione())).append("\n");
            luogoDest.append(StringUtils.defaultString(dto.getCapDestinazione())).append(" ");
            luogoDest.append(StringUtils.defaultString(dto.getCittaDestinazione())).append(" ");
            if (StringUtils.isNotEmpty(dto.getProvinciaDestinazione())) {
                luogoDest.append("(").append(dto.getProvinciaDestinazione()).append(")");
            }
            dto.setLuogoDestinazione(luogoDest.toString());

            if (dto.getIdTipoPagamento() != null) {
                it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto tp = tipiPagamentoDelegate.getById(dto.getIdTipoPagamento());
                if (tp != null) dto.setDescTipoPagamento(tp.getDescrizione());
            }

            if (dto.getIdTipoPorto() != null) {
                it.tinna.smartdoc.shared.dto.tipiporto.TipoPortoDto r = tipiPortoDelegate.getById(dto.getIdTipoPorto());
                if (r != null) dto.setDescTipoPorto(r.getDescrizione());
            }
            if (dto.getIdCausaleTrasporto() != null) {
                it.tinna.smartdoc.shared.dto.documenti.CausaleTrasportoDto r = causaliTrasportoDelegate.getById(dto.getIdCausaleTrasporto());
                if (r != null) dto.setDescCausaleTrasporto(r.getDescrizione());
            }
            if (dto.getIdVettore() != null) {
                it.tinna.smartdoc.shared.dto.vettori.VettoreDto r = vettoriDelegate.getById(dto.getIdVettore());
                if (r != null) dto.setDescVettore(r.getDescrizione());
            }
            if (dto.getIdAspettoBeni() != null) {
                it.tinna.smartdoc.shared.dto.aspettobeni.AspettoBeniDto r = aspettoBeniDelegate.getById(dto.getIdAspettoBeni());
                if (r != null) dto.setDescAspettoBeni(r.getDescrizione());
            }
            if (dto.getPesoLordo() != null) {
                dto.setDescPesoLordo(it.tinna.smartdoc.server.util.NumberUtils.formatAsQuantity(dto.getPesoLordo()) + " Kg");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("documento", dto);
            params.put("datiazienda", daDto);
            params.put("annotazioni", dto.getAnnotazioneEstesa());
            
            if (daDto != null && daDto.getByteLogo() != null) {
                params.put("logopath", new java.io.ByteArrayInputStream(daDto.getByteLogo()));
            }

            if (dto.getDataDocumento() != null) {
                String dStr = dto.getDataDocumento();
                if (dStr.contains("/")) {
                    String[] parts = dStr.split("/");
                    if (parts.length >= 3) params.put("anno", parts[2]);
                } else if (dStr.contains("-")) {
                    String[] parts = dStr.split("-");
                    if (parts.length >= 1) params.put("anno", parts[0]); // assuming YYYY-MM-DD
                }
            }
            if (!params.containsKey("anno")) params.put("anno", "");

            String stampaAgente = StringUtils.defaultIfEmpty(configurazioneDelegate.getByKey(ISharedConstants.CONFIGURAZIONE_DOMINIO_STAMPA, ISharedConstants.CONFIG_KEY_STAMPA_AGENTE), "0");
            params.put("print_codagente", "1".equals(stampaAgente) || "true".equalsIgnoreCase(stampaAgente));

            String tipoStore = StringUtils.defaultString(configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_GLOBAL, ISharedConstants.CONFIG_KEY_TIPOSTORE));
            
            if (dto.getProdotti() != null) {
                _log.info("Formattazione di {} prodotti", dto.getProdotti().size());
                for (ProdottoDocumentoDto pdDto : dto.getProdotti()) {
                    pdDto.setQuantitaFormattata(it.tinna.smartdoc.server.util.NumberUtils.formatAsQuantity(pdDto.getQuantita()));
                    
                    if (pdDto.isFuoriMagazzino()) {
                        pdDto.setCodiceProdotto(StringUtils.defaultString(pdDto.getFmCodice()));
                        pdDto.setDescrizione(StringUtils.defaultString(pdDto.getFmDescrizione()));
                        pdDto.setDescrScelta(StringUtils.defaultString(pdDto.getFmScelta()));
                        pdDto.setDescrTono(StringUtils.defaultString(pdDto.getFmTono()));
                        pdDto.setDescrCalibro(StringUtils.defaultString(pdDto.getFmTaglia()));
                    } else {
                        pdDto.setCodiceProdotto(StringUtils.defaultString(pdDto.getCodiceProdotto()));
                        pdDto.setDescrizione(StringUtils.defaultString(pdDto.getDescProdotto()));
                    }

                    // Se è un'azienda di ceramica, aggiungiamo i dettagli alla descrizione se non è un inserimento manuale
                    if ("CERAMICA".equals(tipoStore) && !pdDto.isFuoriMagazzino()) {
                        StringBuilder sb = new StringBuilder(pdDto.getDescrizione());
                        boolean added = false;
                        if (StringUtils.isNotBlank(pdDto.getDescrFormato())) { sb.append("\nFormato: ").append(pdDto.getDescrFormato()); added = true; }
                        if (StringUtils.isNotBlank(pdDto.getDescrScelta())) { sb.append(added ? " - " : "\n").append("Scelta: ").append(pdDto.getDescrScelta()); added = true; }
                        if (StringUtils.isNotBlank(pdDto.getDescrTono())) { sb.append(added ? " - " : "\n").append("Tono: ").append(pdDto.getDescrTono()); added = true; }
                        if (StringUtils.isNotBlank(pdDto.getDescrCalibro())) { sb.append(added ? " - " : "\n").append("Calibro: ").append(pdDto.getDescrCalibro()); added = true; }
                        pdDto.setDescrizione(sb.toString());
                    }
                }
            }

            _log.info("Caricamento jasperReport: ddt.jrxml");
            net.sf.jasperreports.engine.JasperReport jasperReport = it.tinna.smartdoc.server.util.ReportLoader.getReport("ddt.jrxml");
            _log.info("Riempimento report...");
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(java.util.Arrays.asList(dto)));
            _log.info("Esportazione in PDF...");
            byte[] bytes = net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(jasperPrint);
            _log.info("PDF generato con successo, dimensione: {} bytes", bytes.length);

            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(bytes);
            result.setNome("DDT_" + dto.getNumDocumento() + ".pdf");
            return result;
        } catch (Exception e) {
            _log.error("Errore critico durante la generazione del PDF per DDT ID: {}", id, e);
            return null;
        }
    }

    public boolean isExistentNumero(Integer numeroDdt,
                                    String particella,
                                    String data,
                                    Long id) throws SQLException
    {
        return ddtDao.isExistentNumero(numeroDdt, particella, data, id);
    }
}

