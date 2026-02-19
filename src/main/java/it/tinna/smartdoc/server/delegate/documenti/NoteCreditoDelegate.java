package it.tinna.smartdoc.server.delegate.documenti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.documenti.NoteCreditoDao;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.server.delegate.clienti.ClientiDelegate;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.util.NumberUtils;
import it.tinna.smartdoc.server.delegate.agenti.AgentiDelegate;
import it.tinna.smartdoc.server.delegate.listini.ListiniDelegate;
import it.tinna.smartdoc.server.delegate.progetti.ProgettiDelegate;
import it.tinna.smartdoc.server.delegate.risorse.RisorseDelegate;
import it.tinna.smartdoc.server.delegate.tipipagamento.TipiPagamentoDelegate;
import it.tinna.smartdoc.server.delegate.unitamisura.UnitaMisuraDelegate;
import it.tinna.smartdoc.server.util.ReportLoader;
import it.tinna.smartdoc.shared.constants.ISharedConstants;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.SpesaIncassoDocumentoDto;
import it.tinna.smartdoc.shared.dto.template.RiepilogoIvaDto;
import it.tinna.smartdoc.shared.dto.template.notecredito.NotaCreditoTemplate;
import it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class NoteCreditoDelegate {

    @Autowired
    private NoteCreditoDao noteCreditoDao;

    @Autowired
    private DatiAziendaDelegate datiAziendaDelegate;

    @Autowired
    private AliquoteIvaDelegate aliquoteIvaDelegate;

    @Autowired
    private ConfigurazioneDelegate configurazioneDelegate;

    @Autowired
    private ClientiDelegate clientiDelegate;

    @Autowired
    private UnitaMisuraDelegate unitaMisuraDelegate;

    @Autowired
    private ListiniDelegate listiniDelegate;

    @Autowired
    private TipiPagamentoDelegate tipiPagamentoDelegate;

    @Autowired
    private RisorseDelegate risorseDelegate;

    @Autowired
    private AgentiDelegate agentiDelegate;

    @Autowired
    private ProgettiDelegate progettiDelegate;

    @Autowired
    private CausaliEsigibilitaDifferitaDelegate causaliEsigibilitaDifferitaDelegate;

    public List<NotaCreditoDto> getList(String dataInizio, String dataFine, Integer idCliente, Integer idAgente,
                                      String orderColumn, String orderDir, int start, int length,
                                      String stato, String numDocumento) throws SQLException {
        
        if (StringUtils.isEmpty(dataInizio)) dataInizio = null;
        if (StringUtils.isEmpty(dataFine)) dataFine = null;

        String statoSql = "";
        if (stato != null && !stato.isEmpty()) {
            statoSql = " AND stato_fattura_elettronica = '" + stato + "' ";
        }
        
        String numDocumentoSql = "";
        if (numDocumento != null && !numDocumento.isEmpty()) {
            numDocumentoSql = " AND num_notacredito = '" + numDocumento + "' ";
        }

        return noteCreditoDao.getList(dataInizio, dataFine, idCliente, idAgente, orderColumn, orderDir, start, length, statoSql, numDocumentoSql);
    }

    public NotaCreditoDto getById(long id) throws SQLException {
        return noteCreditoDao.getById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public long save(NotaCreditoDto dto) throws SQLException {
        long id;
        if (dto.getId() == 0) {
            id = noteCreditoDao.insert(dto);
        } else {
            id = dto.getId();
            noteCreditoDao.update(dto);
            noteCreditoDao.deleteProdotti(id);
        }

        if (dto.getProdotti() != null) {
            for (ProdottoDocumentoDto p : dto.getProdotti()) {
                noteCreditoDao.insertProdotto(p, id);
            }
        }

        return id;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(NotaCreditoDto dto) throws SQLException {
        noteCreditoDao.delete(dto.getId(), dto.getUserLastUpdate());
    }

    public Integer getNextNum(String data, int flElettronica) throws SQLException {
        return noteCreditoDao.getNextNum(data, flElettronica);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCombosMap() throws SQLException {
        Map<String, Object> map = new HashMap<>();
        
        map.put("listini", listiniDelegate.getListForCombo());
        map.put("tipiPagamento", tipiPagamentoDelegate.getListForCombo());
        map.put("risorse", risorseDelegate.getListForCombo("BA")); // Banche
        map.put("aliquoteIva", aliquoteIvaDelegate.getListForCombo());
        map.put("unitaMisura", unitaMisuraDelegate.getListForCombo());
        map.put("agenti", agentiDelegate.getListForCombo());
        map.put("progetti", progettiDelegate.getListForCombo());
        map.put("particelle", configurazioneDelegate.getAsArray(ISharedConstants.CONFIG_DOMAIN_DOCUMENTI, ISharedConstants.CONFIG_KEY_PARTICELLE));
        map.put("causaliEsigibilitaDifferita", causaliEsigibilitaDifferitaDelegate.getListForCombo());
        
        return map;
    }

    public DocumentoWrapperDto esportaNotaCreditoPdf(long id) {
        try {
            NotaCreditoDto dto = this.getById(id);
            if (dto == null) return null;

            if (dto.getClienteDto() == null && dto.getIdCliente() != null) {
                dto.setClienteDto(clientiDelegate.getById(dto.getIdCliente()));
            }

            DatiAziendaDto daDto = datiAziendaDelegate.getDatiAzienda();
            
            Map<String, Object> params = new HashMap<>();
            params.put("documento", dto);
            params.put("datiazienda", daDto);
            
            if (daDto != null && daDto.getByteLogo() != null) {
                params.put("logopath", new java.io.ByteArrayInputStream(daDto.getByteLogo()));
            }

            if (dto.getIdTipoPagamento() != null) {
                it.tinna.smartdoc.shared.dto.tipipagamento.TipoPagamentoDto tp = tipiPagamentoDelegate.getById(dto.getIdTipoPagamento());
                if (tp != null) dto.setDescTipoPagamento(tp.getDescrizione());
            }

            if (dto.getDataDocumento() != null) {
                String dStr = dto.getDataDocumento();
                if (dStr.contains("/")) {
                    String[] parts = dStr.split("/");
                    if (parts.length >= 3) params.put("anno", parts[2]);
                } else if (dStr.contains("-")) {
                    String[] parts = dStr.split("-");
                    if (parts.length >= 1) params.put("anno", parts[0]);
                }
            }
            if (!params.containsKey("anno")) params.put("anno", "");

            String stampaAgente = StringUtils.defaultIfEmpty(configurazioneDelegate.getByKey(ISharedConstants.CONFIGURAZIONE_DOMINIO_STAMPA, ISharedConstants.CONFIG_KEY_STAMPA_AGENTE), "0");
            params.put("print_codagente", "1".equals(stampaAgente) || "true".equalsIgnoreCase(stampaAgente));

            double totaleMerce = 0;
            if (dto.getProdotti() != null) {
                for (ProdottoDocumentoDto pdDto : dto.getProdotti()) {
                    pdDto.setQuantitaFormattata(NumberUtils.formatAsQuantity(pdDto.getQuantita()));
                    pdDto.setPercentualeIvaFormattata(NumberUtils.formatAsPercentage(pdDto.getPercentualeIva()));
                    pdDto.setPrezzoFormattato(NumberUtils.formatAsCurrency(pdDto.getPrezzo()));
                    pdDto.setTotaleFormattato(NumberUtils.formatAsCurrency(pdDto.getTotaleSenzaIva()));
                    
                    if (pdDto.isFuoriMagazzino()) {
                        pdDto.setCodiceProdotto(StringUtils.defaultString(pdDto.getFmCodice()));
                        pdDto.setDescrizione(StringUtils.defaultString(pdDto.getFmDescrizione()));
                        pdDto.setDescrScelta(StringUtils.defaultString(pdDto.getFmScelta()));
                        pdDto.setDescrTono(StringUtils.defaultString(pdDto.getFmTono()));
                        pdDto.setDescrCalibro(StringUtils.defaultString(pdDto.getFmTaglia()));
                    } else {
                        pdDto.setDescrizione(StringUtils.defaultString(pdDto.getDescProdotto()));
                        if (pdDto.getCodiceProdotto() == null) pdDto.setCodiceProdotto("");
                    }
                    if (pdDto.isProdotto() || pdDto.isFuoriMagazzino()) {
                        totaleMerce += pdDto.getTotaleSenzaIva();
                    }
                }
            }

            List<RiepilogoIvaDto> riepilogoIva = new ArrayList<>();
            if (dto.getProdotti() != null) {
                for (ProdottoDocumentoDto pdDto : dto.getProdotti()) {
                    if (pdDto.isProdotto() || pdDto.isFuoriMagazzino()) {
                        RiepilogoIvaDto riDto = new RiepilogoIvaDto();
                        riDto.setIdAliquotaIva(pdDto.getIdAliquotaIva());
                        AliquotaIvaDto aiDto = aliquoteIvaDelegate.getById(pdDto.getIdAliquotaIva());
                        if (riepilogoIva.contains(riDto)) {
                            int idx = riepilogoIva.indexOf(riDto);
                            RiepilogoIvaDto existing = riepilogoIva.get(idx);
                            existing.setImponibileMerce(existing.getImponibileMerce() + pdDto.getTotaleSenzaIva());
                            existing.setImponibileMerceFormattato(NumberUtils.formatAsCurrency(existing.getImponibileMerce()));
                            existing.setTotaleImponibile(existing.getTotaleImponibile() + pdDto.getTotaleSenzaIva());
                            existing.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(existing.getTotaleImponibile()));
                            existing.setImportoIva(existing.getImportoIva() + (pdDto.getTotaleSenzaIva() * aiDto.getImposta() / 100));
                            existing.setImportoIvaFormattato(NumberUtils.formatAsCurrency(existing.getImportoIva()));
                        } else {
                            riepilogoIva.add(riDto);
                            riDto.setAliquotaIva(aiDto.getImposta());
                            riDto.setAliquotaIvaFormattata(aiDto.getCodice() + " " + aiDto.getDescrizione());
                            riDto.setImponibileMerce(pdDto.getTotaleSenzaIva());
                            riDto.setImponibileMerceFormattato(NumberUtils.formatAsCurrency(riDto.getImponibileMerce()));
                            riDto.setTotaleImponibile(pdDto.getTotaleSenzaIva());
                            riDto.setTotaleImponibileFormattato(NumberUtils.formatAsCurrency(riDto.getTotaleImponibile()));
                            riDto.setImportoIva(pdDto.getTotaleSenzaIva() * aiDto.getImposta() / 100);
                            riDto.setImportoIvaFormattato(NumberUtils.formatAsCurrency(riDto.getImportoIva()));
                        }
                    }
                }
            }

            double totSpeseArt15 = 0;
            double totTrasporto = 0;
            double totAltreSpese = 0;
            if (dto.getListaSpeseIncassoFattura() != null) {
                for (SpesaIncassoDocumentoDto spesa : dto.getListaSpeseIncassoFattura()) {
                    if ("T".equals(spesa.getTipo())) totTrasporto += spesa.getImporto();
                    else if ("A".equals(spesa.getTipo())) totAltreSpese += spesa.getImporto();
                    else if ("15".equals(spesa.getTipo())) totSpeseArt15 += spesa.getImporto();
                }
            }

            java.math.BigDecimal totaleImponibile = java.math.BigDecimal.valueOf(totaleMerce + totTrasporto + totAltreSpese);
            java.math.BigDecimal totaleIva = java.math.BigDecimal.ZERO;
            for (RiepilogoIvaDto riDto : riepilogoIva) {
                totaleIva = totaleIva.add(java.math.BigDecimal.valueOf(riDto.getImportoIva()));
            }

            totaleIva = totaleIva.setScale(2, java.math.RoundingMode.HALF_UP);
            totaleImponibile = totaleImponibile.setScale(2, java.math.RoundingMode.HALF_UP);
            
            params.put("totalemerce", NumberUtils.formatAsCurrency(totaleMerce));
            params.put("totaleimponibile", NumberUtils.formatAsCurrency(totaleImponibile.doubleValue()));
            params.put("totaleiva", NumberUtils.formatAsCurrency(totaleIva.doubleValue()));
            double totAcconto = dto.getAcconto() != null ? dto.getAcconto() : 0;
            params.put("acconto", NumberUtils.formatAsCurrency(totAcconto));

            params.put("speseart15", NumberUtils.formatAsCurrency(totSpeseArt15));
            params.put("spesetrasporto", NumberUtils.formatAsCurrency(totTrasporto));
            params.put("spesealtre", NumberUtils.formatAsCurrency(totAltreSpese));
            
            double totFattura = totaleImponibile.doubleValue() + totaleIva.doubleValue() + totSpeseArt15;
            params.put("totalefattura", NumberUtils.formatAsCurrency(totFattura));
            params.put("totalenetto", NumberUtils.formatAsCurrency(totFattura - totAcconto));

            String coordinate = "";
            if (!StringUtils.isEmpty(dto.getDescrizioneNsBanca())) {
                coordinate = dto.getDescrizioneNsBanca() + " - ";
            }
            if (!StringUtils.isEmpty(dto.getIbanNsBanca())) {
                coordinate = coordinate + "IBAN " + dto.getIbanNsBanca();
            }
            params.put("coordinate", coordinate);

            NotaCreditoTemplate nt = new NotaCreditoTemplate();
            nt.setProdotti(dto.getProdotti());
            nt.setRiepilogoIva(riepilogoIva);
            
            // Handle Scadenze
            List<ScadenzaPagamentoDocumentoDto> scadenze = noteCreditoDao.getScadenze(id);
            if (scadenze != null) {
                for (ScadenzaPagamentoDocumentoDto s : scadenze) {
                    s.setImportoFormattato(NumberUtils.formatAsCurrency(s.getImporto()));
                }
            }
            nt.setScadenze(scadenze);
            
            params.put("SUBREPORT_SCADENZE", ReportLoader.getReport("fattura_scadenze.jrxml"));

            JasperReport jasperReport = ReportLoader.getReport("nota_credito.jrxml");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(Arrays.asList(nt)));
            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);

            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(bytes);
            result.setNome("NotaCredito_" + dto.getNumDocumento() + ".pdf");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
