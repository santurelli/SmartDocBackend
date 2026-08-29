package it.tinna.smartdoc.server.delegate.ecommerce;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.contatti.ContattiDao;
import it.tinna.smartdoc.server.dao.ecommerce.EcommerceConfigDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.aliquoteiva.AliquoteIvaDelegate;
import it.tinna.smartdoc.server.delegate.clienti.ClientiDelegate;
import it.tinna.smartdoc.server.delegate.documenti.DdtDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FattureDelegate;
import it.tinna.smartdoc.server.delegate.prodotti.ProdottiDelegate;
import it.tinna.smartdoc.server.delegate.unitamisura.UnitaMisuraDelegate;
import it.tinna.smartdoc.server.integration.ecommerce.woocommerce.WooCommerceClient;
import it.tinna.smartdoc.server.service.contabilita.ContoResolverService;
import it.tinna.smartdoc.shared.dto.aliquoteiva.AliquotaIvaDto;
import it.tinna.smartdoc.shared.dto.clienti.ClienteDto;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.documenti.DdtDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceConfigDto;
import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceOrdineImportatoDto;
import it.tinna.smartdoc.shared.dto.ecommerce.woocommerce.WooBillingDto;
import it.tinna.smartdoc.shared.dto.ecommerce.woocommerce.WooLineItemDto;
import it.tinna.smartdoc.shared.dto.ecommerce.woocommerce.WooOrderDto;
import it.tinna.smartdoc.shared.dto.contatti.ContattoDto;
import it.tinna.smartdoc.shared.dto.indirizzi.IndirizzoDto;
import it.tinna.smartdoc.shared.dto.prodotti.ProdottoDto;
import it.tinna.smartdoc.shared.dto.unitamisura.UnitaMisuraDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Importazione ordini WooCommerce -> DDT / Fattura. Ambito volutamente ridotto rispetto al
 * flusso FastOrder: NON invia automaticamente la fattura elettronica allo SdI, la crea in stato
 * BO (bozza) cosi' l'utente puo' rivederla prima dell'invio. Match cliente per email (via
 * d_e_contatti_clienti) quando presente nell'ordine WooCommerce, altrimenti per Partita IVA se
 * disponibile; in assenza di entrambi viene creato un nuovo cliente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EcommerceIntegrationDelegate extends BaseDelegate {

    private static final DateTimeFormatter WOO_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String CODICE_IVA_DEFAULT = "22";

    private final WooCommerceClient wooCommerceClient;
    private final ClientiDelegate clientiDelegate;
    private final ProdottiDelegate prodottiDelegate;
    private final DdtDelegate ddtDelegate;
    private final FattureDelegate fattureDelegate;
    private final AliquoteIvaDelegate aliquoteIvaDelegate;
    private final UnitaMisuraDelegate unitaMisuraDelegate;

    private EcommerceConfigDao dao() {
        return new EcommerceConfigDao(jdbcTemplate);
    }

    public EcommerceConfigDto getConfig() {
        EcommerceConfigDto dto = dao().getForCurrentTenant();
        if (dto != null && StringUtils.isNotBlank(dto.getConsumerSecret())) {
            dto.setConsumerSecret("••••••••");
        }
        return dto;
    }

    public void saveConfig(EcommerceConfigDto dto) {
        // Se il secret arriva mascherato (utente non l'ha ritoccato), non sovrascrivere quello salvato.
        if ("••••••••".equals(dto.getConsumerSecret())) {
            EcommerceConfigDto existing = dao().getForCurrentTenant();
            dto.setConsumerSecret(existing != null ? existing.getConsumerSecret() : null);
        }
        dao().upsert(dto);
    }

    public List<EcommerceOrdineImportatoDto> getLog() {
        return dao().getLog(50);
    }

    /**
     * Esegue la sincronizzazione per il tenant correntemente selezionato in DatabaseContextHolder.
     * Ogni ordine e' processato in una transazione separata: un errore su un ordine non blocca gli altri.
     */
    public void sincronizza() {
        EcommerceConfigDao configDao = dao();
        EcommerceConfigDto config = configDao.getForCurrentTenant();
        if (config == null || config.getFlAbilitato() == null || config.getFlAbilitato() != 1) {
            return;
        }
        if (StringUtils.isBlank(config.getStoreUrl()) || StringUtils.isBlank(config.getConsumerKey()) || StringUtils.isBlank(config.getConsumerSecret())) {
            configDao.aggiornaEsitoSync(config.getId(), "ERRORE: configurazione incompleta (URL o credenziali mancanti)");
            return;
        }

        LocalDateTime ultimoSync = configDao.getUltimoSync(config);
        List<WooOrderDto> ordini;
        try {
            ordini = wooCommerceClient.fetchOrdini(config, ultimoSync);
        } catch (Exception e) {
            log.error("Errore nel recupero ordini WooCommerce per store {}", config.getStoreUrl(), e);
            configDao.aggiornaEsitoSync(config.getId(), "ERRORE connessione: " + e.getMessage());
            return;
        }

        int importati = 0;
        int saltati = 0;
        int errori = 0;
        for (WooOrderDto ordine : ordini) {
            String idEsterno = String.valueOf(ordine.getId());
            if (configDao.isOrdineGiaImportato(idEsterno)) {
                saltati++;
                continue;
            }
            try {
                importaOrdineTransazionale(config, ordine);
                importati++;
            } catch (Exception e) {
                log.error("Errore nell'importazione dell'ordine WooCommerce {}", idEsterno, e);
                configDao.registraOrdineImportato(idEsterno, ordine.getNumber(), null, null, "ERRORE", StringUtils.left(e.getMessage(), 2000));
                errori++;
            }
        }

        String esito = String.format("OK: %d importati, %d gia' presenti, %d in errore (%s)",
                importati, saltati, errori, LocalDateTime.now());
        configDao.aggiornaEsitoSync(config.getId(), esito);
        log.info("Sincronizzazione WooCommerce completata per store {}: {}", config.getStoreUrl(), esito);
    }

    @Transactional(rollbackFor = Exception.class)
    protected void importaOrdineTransazionale(EcommerceConfigDto config, WooOrderDto ordine) throws Exception {
        ClienteDto cliente = findOrCreateCliente(ordine.getBilling());

        List<ProdottoDocumentoDto> righe = new ArrayList<>();
        BigDecimal totaleImponibile = BigDecimal.ZERO;
        BigDecimal totaleIva = BigDecimal.ZERO;

        for (WooLineItemDto item : ordine.getLineItems()) {
            ProdottoDto articolo = findOrCreateArticolo(item);

            ProdottoDocumentoDto riga = new ProdottoDocumentoDto();
            riga.setProdotto(true);
            riga.setIdProdotto((int) articolo.getId());
            riga.setDescProdotto(articolo.getDescrizione());
            double quantita = item.getQuantity() != null ? item.getQuantity() : 1d;
            riga.setQuantita(quantita);
            riga.setIdAliquotaIva(articolo.getIdAliquotaIva());

            BigDecimal totaleRigaSenzaIva = parseImporto(item.getTotal());
            BigDecimal ivaRiga = parseImporto(item.getTotalTax());
            BigDecimal prezzoUnitario = quantita > 0
                    ? totaleRigaSenzaIva.divide(BigDecimal.valueOf(quantita), 2, RoundingMode.HALF_UP)
                    : totaleRigaSenzaIva;

            riga.setPrezzo(prezzoUnitario.doubleValue());
            riga.setTotaleSenzaIva(totaleRigaSenzaIva.doubleValue());
            riga.setPrezzoImponibile(totaleRigaSenzaIva.doubleValue());

            totaleImponibile = totaleImponibile.add(totaleRigaSenzaIva);
            totaleIva = totaleIva.add(ivaRiga);

            righe.add(riga);
        }

        String dataOrdine = formatDataItaliana(ordine.getDateCreated());

        Integer idDdt = null;
        if (config.getFlCreaDdt() != null && config.getFlCreaDdt() == 1) {
            idDdt = creaDdt(cliente, ordine.getBilling(), righe, dataOrdine);
        }

        Integer idFattura = null;
        if (config.getFlCreaFattura() != null && config.getFlCreaFattura() == 1) {
            idFattura = creaFattura(cliente, ordine.getBilling(), righe, dataOrdine, totaleImponibile.add(totaleIva), totaleIva);
        }

        dao().registraOrdineImportato(String.valueOf(ordine.getId()), ordine.getNumber(), idDdt, idFattura, "OK", null);
    }

    private Integer creaDdt(ClienteDto cliente, WooBillingDto billing, List<ProdottoDocumentoDto> righe, String dataDocumento) throws Exception {
        DdtDto dto = new DdtDto();
        dto.setIdCliente((int) cliente.getId());
        dto.setClienteDto(cliente);
        dto.setDataDocumento(dataDocumento);
        dto.setNumDocumento(ddtDelegate.getNextNum(dataDocumento));
        dto.setIndirizzoIntestazione(billing.getAddress1());
        dto.setCittaIntestazione(billing.getCity());
        dto.setCapIntestazione(billing.getPostcode());
        dto.setProvinciaIntestazione(billing.getState());
        dto.setNazioneIntestazione(StringUtils.defaultIfBlank(billing.getCountry(), "Italia"));
        // Le righe vanno clonate: DDT e Fattura non possono condividere le stesse istanze (id riga distinti a insert).
        dto.setProdotti(clonaRighe(righe));
        long id = ddtDelegate.insert(dto);
        return (int) id;
    }

    private Integer creaFattura(ClienteDto cliente, WooBillingDto billing, List<ProdottoDocumentoDto> righe, String dataDocumento, BigDecimal totale, BigDecimal totaleIva) throws Exception {
        FatturaDto dto = new FatturaDto();
        dto.setTipoFattura(TipoFattura.FATTURA);
        dto.setFlFatturaElettronica(1);
        dto.setStatoFatturaElettronica(StatoFatturaElettronica.BO);

        dto.setIdCliente((int) cliente.getId());
        dto.setClienteDto(cliente);
        dto.setIndirizzoIntestazione(StringUtils.defaultIfBlank(billing.getAddress1(), "NON SPECIFICATO"));
        dto.setCittaIntestazione(StringUtils.defaultIfBlank(billing.getCity(), "NON SPECIFICATO"));
        dto.setCapIntestazione(StringUtils.defaultIfBlank(billing.getPostcode(), "00000"));
        dto.setProvinciaIntestazione(StringUtils.defaultIfBlank(billing.getState(), "EE"));
        dto.setNazioneIntestazione(StringUtils.defaultIfBlank(billing.getCountry(), "Italia"));
        dto.setPartitaIva(cliente.getPartitaIva());
        dto.setCodiceFiscale(cliente.getCodiceFiscale());

        dto.setDataDocumento(dataDocumento);
        dto.setNumDocumento(fattureDelegate.getNextNumFattura(dataDocumento, 1, TipoFattura.FATTURA));
        dto.setSplitPayment(0);
        dto.setFlRitenutaAcconto(0);
        dto.setEsigibilitaDifferita(0);

        dto.setProdotti(clonaRighe(righe));
        BigDecimal totaleArrotondato = totale.setScale(2, RoundingMode.HALF_UP);
        dto.setTotale(totaleArrotondato.doubleValue());
        dto.setTotaleIva(totaleIva.setScale(2, RoundingMode.HALF_UP).doubleValue());
        dto.setTotaleDaPagare(dto.getTotale());

        try {
            Integer idTipoPagamento = jdbcTemplate.queryForObject(
                "SELECT k_d_e_tipipagamento FROM d_e_tipipagamento WHERE descrizione = 'Pagato' AND fl_deleted = 0 LIMIT 1",
                Integer.class);
            dto.setIdTipoPagamento(idTipoPagamento);
        } catch (Exception e) {
            log.warn("Nessun tipo pagamento 'Pagato' trovato: la fattura verra' creata senza scadenza collegata");
        }

        if (dto.getIdTipoPagamento() != null) {
            it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto scadenza =
                    new it.tinna.smartdoc.shared.dto.tipipagamento.ScadenzaPagamentoDocumentoDto();
            scadenza.setDtScadenza(dataDocumento);
            scadenza.setImporto(dto.getTotale());
            scadenza.setSaldato(1);
            dto.setListaScadenzePagamentiDocumento(List.of(scadenza));
        }

        long id = fattureDelegate.insert(dto);
        return (int) id;
    }

    private List<ProdottoDocumentoDto> clonaRighe(List<ProdottoDocumentoDto> righe) {
        List<ProdottoDocumentoDto> copia = new ArrayList<>();
        for (ProdottoDocumentoDto r : righe) {
            ProdottoDocumentoDto c = new ProdottoDocumentoDto();
            c.setProdotto(r.isProdotto());
            c.setIdProdotto(r.getIdProdotto());
            c.setDescProdotto(r.getDescProdotto());
            c.setQuantita(r.getQuantita());
            c.setIdAliquotaIva(r.getIdAliquotaIva());
            c.setPrezzo(r.getPrezzo());
            c.setTotaleSenzaIva(r.getTotaleSenzaIva());
            c.setPrezzoImponibile(r.getPrezzoImponibile());
            copia.add(c);
        }
        return copia;
    }

    private ClienteDto findOrCreateCliente(WooBillingDto billing) throws Exception {
        if (billing == null) {
            throw new IllegalStateException("Ordine WooCommerce privo di dati di fatturazione (billing)");
        }

        // Match per email tramite d_e_contatti_clienti (l'anagrafica cliente non ha un campo email
        // proprio: citta'/nazione sono in testata, il resto degli indirizzi vive in IndirizzoDto,
        // email/telefono vivono nei "contatti" collegati). La Partita IVA WooCommerce base non e' un
        // campo standard (richiede plugin EU VAT -> meta_data, non gestito in questa prima versione).
        if (StringUtils.isNotBlank(billing.getEmail())) {
            try {
                Integer idClienteEsistente = jdbcTemplate.queryForObject(
                        "SELECT id_richiedente FROM d_e_contatti_clienti WHERE lower(email) = lower(?) AND fl_deleted = 0 LIMIT 1",
                        Integer.class, billing.getEmail().trim());
                if (idClienteEsistente != null) {
                    ClienteDto esistente = clientiDelegate.getById(idClienteEsistente);
                    if (esistente != null) {
                        return esistente;
                    }
                }
            } catch (EmptyResultDataAccessException ignored) {
                // nessun match, si procede con la creazione
            }
        }

        ClienteDto cliente = new ClienteDto();
        cliente.setDenominazione(StringUtils.defaultIfBlank(billing.getNominativo(), "Cliente WooCommerce"));
        cliente.setCitta(billing.getCity());
        cliente.setNazione(StringUtils.defaultIfBlank(billing.getCountry(), "IT"));
        cliente.setTipologia(TipologiaClienteFornitore.PRIVATO);
        cliente.setCodice(clientiDelegate.generaCodice());

        IndirizzoDto legale = new IndirizzoDto();
        legale.setTipologia(IndirizzoDto.TipologiaIndirizzo.SEDE_LEGALE.getValore());
        legale.setIndirizzo(billing.getAddress1());
        legale.setCitta(billing.getCity());
        legale.setCap(billing.getPostcode());
        legale.setProvincia(billing.getState());
        legale.setNazione(StringUtils.defaultIfBlank(billing.getCountry(), "IT"));

        Integer id = clientiDelegate.insert(cliente, List.of(legale), List.of(), List.of(), List.of());
        cliente.setId(id.longValue());

        // Registriamo un contatto con l'email WooCommerce, cosi' i prossimi ordini dello stesso
        // cliente vengono associati invece di generare un duplicato.
        if (StringUtils.isNotBlank(billing.getEmail())) {
            try {
                ContattoDto contatto = new ContattoDto();
                contatto.setTipologia(ContattoDto.TipologiaContatto.ALTRO.getValore());
                contatto.setDescrizione("Email WooCommerce");
                contatto.setEmail(billing.getEmail().trim());
                contatto.setTelefono(billing.getPhone());
                contatto.setIdRichiedente(id);
                contatto.setUserCreated(0L);
                new ContattiDao(jdbcTemplate).insert(ContattoDto.Richiedente.CLIENTI.getValore(), contatto);
            } catch (Exception e) {
                log.warn("Impossibile salvare il contatto email per il nuovo cliente {}: {}", id, e.getMessage());
            }
        }

        return cliente;
    }

    private ProdottoDto findOrCreateArticolo(WooLineItemDto item) throws Exception {
        String codice = StringUtils.isNotBlank(item.getSku()) ? item.getSku().trim() : null;

        if (codice != null) {
            try {
                Integer idEsistente = jdbcTemplate.queryForObject(
                        "SELECT k_d_e_prodotti FROM d_e_prodotti WHERE codice = ? AND fl_deleted = 0 LIMIT 1",
                        Integer.class, codice);
                if (idEsistente != null) {
                    ProdottoDto esistente = new ProdottoDto();
                    esistente.setId(idEsistente.longValue());
                    esistente.setDescrizione(item.getName());
                    esistente.setIdAliquotaIva(jdbcTemplate.queryForObject(
                            "SELECT k_d_e_aliquoteiva FROM d_e_prodotti WHERE k_d_e_prodotti = ?", Integer.class, idEsistente));
                    return esistente;
                }
            } catch (EmptyResultDataAccessException ignored) {
                // nessun match, si procede con la creazione
            }
        }

        ProdottoDto nuovo = new ProdottoDto();
        nuovo.setCodice(StringUtils.isNotBlank(codice) ? codice : prodottiDelegate.getProssimoCodice());
        nuovo.setDescrizione(StringUtils.defaultIfBlank(item.getName(), "Articolo WooCommerce"));
        nuovo.setTipologia("A");
        nuovo.setGestMagazzino(0);
        nuovo.setIdDivisione(0);
        nuovo.setIdAliquotaIva(resolveAliquotaIvaDefault());
        nuovo.setIdUnitaMisura1(resolveUnitaMisuraDefault());
        nuovo.setIdContoRicavo(new ContoResolverService(jdbcTemplate).resolveContoRuolo("RICAVI_VENDITE"));

        long id = prodottiDelegate.insert(nuovo);
        nuovo.setId(id);
        return nuovo;
    }

    private Integer resolveAliquotaIvaDefault() throws Exception {
        List<AliquotaIvaDto> lista = aliquoteIvaDelegate.getByAliquota(Double.parseDouble(CODICE_IVA_DEFAULT));
        if (lista != null && !lista.isEmpty()) {
            return (int) lista.get(0).getId();
        }
        throw new IllegalStateException("Nessuna aliquota IVA al " + CODICE_IVA_DEFAULT + "% configurata: impossibile creare l'articolo automaticamente");
    }

    private Integer resolveUnitaMisuraDefault() throws Exception {
        List<UnitaMisuraDto> lista = unitaMisuraDelegate.getListForCombo();
        if (lista != null && !lista.isEmpty()) {
            return (int) lista.get(0).getId();
        }
        throw new IllegalStateException("Nessuna unita' di misura configurata: impossibile creare l'articolo automaticamente");
    }

    private BigDecimal parseImporto(String valore) {
        if (StringUtils.isBlank(valore)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(valore).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String formatDataItaliana(String isoDate) {
        try {
            LocalDateTime dt = LocalDateTime.parse(isoDate, WOO_DATE_FORMAT);
            return String.format("%02d/%02d/%04d", dt.getDayOfMonth(), dt.getMonthValue(), dt.getYear());
        } catch (Exception e) {
            LocalDateTime now = LocalDateTime.now();
            return String.format("%02d/%02d/%04d", now.getDayOfMonth(), now.getMonthValue(), now.getYear());
        }
    }
}
