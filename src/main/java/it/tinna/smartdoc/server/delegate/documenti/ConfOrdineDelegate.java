package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.ConfOrdineDao;
import it.tinna.smartdoc.server.delegate.agenti.AgentiDelegate;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.server.delegate.risorse.RisorseDelegate;
import it.tinna.smartdoc.server.delegate.tipipagamento.TipiPagamentoDelegate;
import it.tinna.smartdoc.server.delegate.unitamisura.UnitaMisuraDelegate;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.documenti.ConfOrdineDto;
import it.tinna.smartdoc.shared.dto.documenti.MovimentiDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.response.DatatablesResponseDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;
import it.tinna.smartdoc.shared.dto.template.confordine.ConfOrdineTemplate;

@Service
public class ConfOrdineDelegate extends it.tinna.smartdoc.server.delegate.BaseDelegate {

    @Autowired
    private ConfOrdineDao confOrdineDao;

    @Autowired
    private AliquoteIvaDelegate aliquoteIvaDelegate;

    @Autowired
    private UnitaMisuraDelegate unitaMisuraDelegate;

    @Autowired
    private TipiPagamentoDelegate tipiPagamentoDelegate;

    @Autowired
    private ListiniDelegate listiniDelegate;

    @Autowired
    private RisorseDelegate risorseDelegate;

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    @Autowired
    private AgentiDelegate agentiDelegate;

    @Transactional(rollbackFor = Exception.class)
    public void delete(long id, long userId) throws SQLException {
        ConfOrdineDto dto = new ConfOrdineDto();
        dto.setId(id);
        dto.setUserLastUpdate(userId);
        confOrdineDao.delete(dto);
    }

    public ConfOrdineDto getById(long id) throws SQLException {
        return confOrdineDao.getById(id);
    }

    public DatatablesResponseDto<MovimentiDocumentoDto> getList(Integer idCliente, String dtFrom, String dtTo, Integer idAgente,
            Integer length, Integer start, String orderColumn, String orderDir) throws SQLException {
        
        List<MovimentiDocumentoDto> list = confOrdineDao.getList(idCliente, dtFrom, dtTo, idAgente, length, start, orderColumn, orderDir);
        long total = 0;
        if (list.size() > 0) {
            total = list.get(0).getTotal();
        }
        
        DatatablesResponseDto<MovimentiDocumentoDto> response = new DatatablesResponseDto<>();
        response.setList(list);
        response.setTotalCount(total);
        response.setTotalFiltered(total);
        
        return response;
    }

    public String getNextNum(String data) throws SQLException {
        return confOrdineDao.generaCodice(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer save(ConfOrdineDto dto) throws SQLException {
        if ( isExistentNumero(dto.getNumDocumento(), dto.getParticella(), dto.getDataDocumento(), dto.getId()) )
        {
            throw new SQLException("Il numero di conferma ordine " + dto.getNumDocumento() + (StringUtils.isNotBlank(dto.getParticella()) ? "/" + dto.getParticella() : "") + " è già presente per l'anno di riferimento.");
        }
        Integer id;
        if (dto.getId() == 0) {
            id = confOrdineDao.insert(dto);
        } else {
            confOrdineDao.update(dto);
            id = (int) (long) dto.getId();
        }

        // Gestione collegamento documento (es. da Preventivo)
        if (dto.getIdDocAssociato() != null && dto.getIdDocAssociato() > 0 && StringUtils.isNotEmpty(dto.getTipoDocAssociato())) {
            if (!confOrdineDao.existsDocumentoCollegato(dto.getIdDocAssociato().intValue(), dto.getTipoDocAssociato(), id, "CONF_ORDINE")) {
                _log.info("Inserimento collegamento documento {} ({}) -> {} (CONF_ORDINE)", dto.getIdDocAssociato(), dto.getTipoDocAssociato(), id);
                confOrdineDao.insertDocumentoCollegato(dto.getIdDocAssociato().intValue(), dto.getTipoDocAssociato(), id, "CONF_ORDINE");
            }
        }

        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto riga : dto.getProdotti()) {
                riga.setIdDocumento(id);
                confOrdineDao.insertProdotto(riga);
            }
        }
        return id;
    }

    @Autowired
    private it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate datiaziendaDelegate;

    @Autowired
    private it.tinna.smartdoc.server.delegate.clienti.ClientiDelegate clientiDelegate;

    public it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto esportaConfOrdinePdf(String dbKey, long id) {
        try {
            ConfOrdineTemplate pt = new ConfOrdineTemplate();
            Map<String, Object> params = new HashMap<>();
            
            ConfOrdineDto dto = confOrdineDao.getById(id);
            String outputName = new StringBuilder(it.tinna.smartdoc.server.util.IOUtility.getValidFilename(new StringBuilder("conferma_ordine_")
                    .append(dto.getNumDocumento())
                    .append(StringUtils.isEmpty(dto.getParticella()) ? "" : "/" + dto.getParticella()).toString()))
                    .append(".pdf").toString();

            if (dto.getClienteDto() == null && dto.getIdCliente() != null) {
                dto.setClienteDto(clientiDelegate.getById(dto.getIdCliente()));
            }

            StringBuilder fatturareA = new StringBuilder();
            if (dto.getClienteDto() != null) {
                fatturareA.append(dto.getClienteDto().getDenominazione()).append("\n");
            } else {
                fatturareA.append(StringUtils.defaultString(dto.getDenominazioneCliente())).append("\n");
            }
            fatturareA.append(StringUtils.defaultString(dto.getIndirizzoIntestazione())).append("\n");
            fatturareA.append(StringUtils.defaultString(dto.getCapIntestazione())).append(" ");
            fatturareA.append(StringUtils.defaultString(dto.getCittaIntestazione())).append(" ");
            if (StringUtils.isNotEmpty(dto.getProvinciaIntestazione())) {
                fatturareA.append("(").append(dto.getProvinciaIntestazione()).append(")");
            }
            fatturareA.append("\n").append(StringUtils.defaultString(dto.getNazioneIntestazione()));
            dto.setFatturareA(fatturareA.toString());
            pt.setAnnotazioneEstesa(dto.getAnnotazioneEstesa());

            // dati azienda
            it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto daDto = datiaziendaDelegate.getDatiAzienda();
            if (daDto != null) {
                if (daDto.getByteLogo() != null) {
                    params.put("logopath", new java.io.ByteArrayInputStream(daDto.getByteLogo()));
                }
            }
            params.put("datiazienda", daDto);
            params.put("documento", dto);
            if (!StringUtils.isEmpty(dto.getDataDocumento())) {
                String[] str = dto.getDataDocumento().split("/");
                if (str.length >= 3) {
                    params.put("anno", str[2]);
                }
            }

            String tipoStore = "";
            try {
                tipoStore = configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_GLOBAL, ISharedConstants.CONFIG_KEY_TIPOSTORE);
            } catch (SQLException e) {
                _log.error("Errore nel recupero della configurazione TIPO_STORE", e);
            }
            boolean isCeramica = "ceramica".equalsIgnoreCase(tipoStore);

            double totaleMerce = 0;
            if (dto.getProdotti() != null) {
                for (ProdottoDocumentoDto pdDto : dto.getProdotti()) {
                    if (pdDto.isProdotto() || pdDto.isFuoriMagazzino()) {
                        pdDto.setQuantitaFormattata(it.tinna.smartdoc.server.util.NumberUtils.formatAsQuantity(pdDto.getQuantita()));
                        pdDto.setPercentualeIvaFormattata(it.tinna.smartdoc.server.util.NumberUtils.formatAsPercentage(pdDto.getPercentualeIva()));
                        pdDto.setPrezzoFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(pdDto.getPrezzo()));
                        pdDto.setTotaleFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(pdDto.getTotaleSenzaIva()));
                        totaleMerce += pdDto.getTotaleSenzaIva();
                        
                        if (pdDto.isFuoriMagazzino()) {
                            if (pdDto.getProdottoDto() == null) pdDto.setProdottoDto(new it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto());
                            pdDto.getProdottoDto().setCodice(StringUtils.isEmpty(pdDto.getFmCodice()) ? "" : pdDto.getFmCodice());
                            pdDto.getProdottoDto().setDescrizione(pdDto.getFmDescrizione());
                            pdDto.setDescrizione(pdDto.getFmDescrizione());
                            pdDto.setDescrScelta(pdDto.getFmScelta());
                            pdDto.setDescrTono(pdDto.getFmTono());
                            pdDto.setDescrCalibro(pdDto.getFmTaglia());
                        } else {
                            String desc = pdDto.getDescProdotto();
                            if (isCeramica && StringUtils.isNotEmpty(pdDto.getDescrFormato())) {
                                desc += "\n" + pdDto.getDescrFormato();
                            }
                            pdDto.setDescrizione(desc);
                        }
                    } else {
                        // It's a NOTE or generic text row
                        pdDto.setDescrizione(pdDto.getNota());
                    }
                }
                pt.setProdotti(dto.getProdotti());
            }

            List<RiepilogoIvaDto> riepilogoIva = new ArrayList<>();
            if (dto.getProdotti() != null) {
                for (ProdottoDocumentoDto pdDto : dto.getProdotti()) {
                    if ((pdDto.isProdotto() || pdDto.isFuoriMagazzino()) && pdDto.getIdAliquotaIva() != null) {
                        RiepilogoIvaDto riDto = new RiepilogoIvaDto();
                        riDto.setIdAliquotaIva(pdDto.getIdAliquotaIva());
                        AliquotaIvaDto aiDto = aliquoteIvaDelegate.getById(pdDto.getIdAliquotaIva());
                        
                        boolean found = false;
                        for (RiepilogoIvaDto existing : riepilogoIva) {
                            if (existing.getIdAliquotaIva().equals(riDto.getIdAliquotaIva())) {
                                existing.setTotaleImponibile(existing.getTotaleImponibile() + pdDto.getTotaleSenzaIva());
                                existing.setTotaleImponibileFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(existing.getTotaleImponibile()));
                                existing.setImportoIva(existing.getImportoIva() + (pdDto.getTotaleSenzaIva() * aiDto.getImposta() / 100));
                                existing.setImportoIvaFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(existing.getImportoIva()));
                                found = true;
                                break;
                            }
                        }
                        
                        if (!found) {
                            riepilogoIva.add(riDto);
                            riDto.setAliquotaIva(aiDto.getImposta());
                            riDto.setAliquotaIvaFormattata(new StringBuilder(aiDto.getCodice()).append(" ").append(aiDto.getDescrizione()).toString());
                            riDto.setImponibileMerce(pdDto.getTotaleSenzaIva());
                            riDto.setImponibileMerceFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(riDto.getImponibileMerce()));
                            riDto.setTotaleImponibile(pdDto.getTotaleSenzaIva());
                            riDto.setTotaleImponibileFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(riDto.getTotaleImponibile()));
                            riDto.setImportoIva(pdDto.getTotaleSenzaIva() * aiDto.getImposta() / 100);
                            riDto.setImportoIvaFormattato(it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                        }
                    }
                }
            }
            pt.setRiepilogoIva(riepilogoIva);

            double totSpeseArt15 = 0;
            double totTrasporto = 0;
            double totAltreSpese = 0;
            
            java.math.BigDecimal totaleImponibile = java.math.BigDecimal.valueOf(totaleMerce);
            java.math.BigDecimal totaleIva = java.math.BigDecimal.ZERO;
            for (RiepilogoIvaDto riDto : riepilogoIva) {
                totaleIva = totaleIva.add(java.math.BigDecimal.valueOf(riDto.getImportoIva()));
            }

            totaleIva = totaleIva.setScale(2, java.math.RoundingMode.HALF_UP);
            totaleImponibile = totaleImponibile.setScale(2, java.math.RoundingMode.HALF_UP);
            
            params.put("totalemerce", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totaleMerce));
            params.put("totaleimponibile", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totaleImponibile.doubleValue()));
            params.put("totaleiva", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totaleIva.doubleValue()));
            double totAcconto = dto.getAcconto() != null ? dto.getAcconto() : 0;
            params.put("acconto", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totAcconto));

            params.put("speseart15", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totSpeseArt15));
            params.put("spesetrasporto", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totTrasporto));
            params.put("spesealtre", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totAltreSpese));
            
            params.put("totalefattura", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() + totaleIva.doubleValue() + totSpeseArt15));
            params.put("totalenetto", it.tinna.smartdoc.server.util.NumberUtils.formatAsCurrency(totaleImponibile.doubleValue() + totaleIva.doubleValue() + totSpeseArt15 - totAcconto));
            
            String coordinate = "";
            if (!StringUtils.isEmpty(dto.getDescrizioneNsBanca())) {
                coordinate = dto.getDescrizioneNsBanca() + " - ";
            }
            if (!StringUtils.isEmpty(dto.getIbanNsBanca())) {
                coordinate = coordinate + "IBAN " + dto.getIbanNsBanca();
            }
            params.put("coordinate", coordinate);

            String stampaAgente = StringUtils.defaultIfEmpty(configurazioneDelegate.getByKey(ISharedConstants.CONFIGURAZIONE_DOMINIO_STAMPA, ISharedConstants.CONFIG_KEY_STAMPA_AGENTE), "0");
            params.put("print_codagente", "1".equals(stampaAgente));

            net.sf.jasperreports.engine.JasperReport jasperReport = it.tinna.smartdoc.server.util.ReportLoader.getReport("conferma_ordine.jrxml");
            net.sf.jasperreports.engine.data.JRBeanCollectionDataSource dataSource = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(java.util.Arrays.asList(pt));
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, params, dataSource);
            byte[] bytes = net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(jasperPrint);

            it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto res = new it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto();
            res.setFlusso(bytes);
            res.setNome(outputName);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> getCombosMap() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put(ISharedConstants.COMBOSMAP_KEY_ALIQUOTEIVA, aliquoteIvaDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_UNITAMISURA, unitaMisuraDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_TIPIPAGAMENTO, tipiPagamentoDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_LISTINI, listiniDelegate.getListForCombo());
        map.put(ISharedConstants.COMBOSMAP_KEY_RISORSE, risorseDelegate.getListForCombo("BA"));
        map.put(ISharedConstants.COMBOSMAP_KEY_AGENTI, agentiDelegate.getList(null, null, null, 0, "asc"));
        
        String particelleAsString = configurazioneDelegate.getByKey(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_PARTICELLE);
        if (StringUtils.isNotEmpty(particelleAsString)) {
            map.put(ISharedConstants.COMBOSMAP_KEY_PARTICELLE, StringUtils.split(particelleAsString, "\r\n"));
        }
        
        return map;
    }

    public boolean isExistentNumero(Integer numero,
                                    String particella,
                                    String data,
                                    Long id) throws SQLException
    {
        return confOrdineDao.isExistentNumero(numero, particella, data, id);
    }
}

