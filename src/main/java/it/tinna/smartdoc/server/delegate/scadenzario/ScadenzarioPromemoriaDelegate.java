package it.tinna.smartdoc.server.delegate.scadenzario;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import freemarker.template.Configuration;
import freemarker.template.Template;
import it.tinna.smartdoc.server.dao.scadenzario.ScadenzarioRegoleDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.service.mail.MailSenderService;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzaPromemoriaDto;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzarioInvioDto;
import it.tinna.smartdoc.shared.dto.scadenzario.ScadenzarioRegolaDto;

@Transactional(readOnly = true)
@Service("scadenzarioPromemoriaDelegate")
public class ScadenzarioPromemoriaDelegate extends BaseDelegate
{

    public static final String TIPO_INCASSO    = "INCASSO";

    public static final String TIPO_PAGAMENTO  = "PAGAMENTO";

    @Autowired
    private DatiAziendaDelegate datiAziendaDelegate;

    @Autowired
    @Qualifier("mailSenderServiceGeneric")
    private MailSenderService  mailSenderService;

    @Autowired
    @Qualifier("scadenzarioFreeMarkerConfiguration")
    private Configuration      freeMarkerConfiguration;

    public List<ScadenzarioInvioDto> getInviiByCliente(long idCliente) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        return dao.getInviiByCliente(idCliente);
    }

    public List<ScadenzarioInvioDto> getInviiByFattura(long idFattura) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        return dao.getInviiByFattura(idFattura);
    }

    public List<ScadenzarioInvioDto> getInviiByFatturaFornitore(long idFatturaFornitore) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        return dao.getInviiByFatturaFornitore(idFatturaFornitore);
    }

    public List<ScadenzarioRegolaDto> getList(String tipo) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        return dao.getList(tipo);
    }

    public ScadenzarioRegolaDto getById(long id) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        return dao.getById(id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public long insert(ScadenzarioRegolaDto dto) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        return dao.insert(dto);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void update(ScadenzarioRegolaDto dto) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        dao.update(dto);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void delete(long id) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        dao.delete(id);
    }

    /**
     * Elabora tutte le regole attive (incasso e pagamento) per il tenant corrente
     * ed invia i promemoria dovuti per la data odierna.
     */
    @Transactional(rollbackFor = Exception.class)
    public void processaPromemoria() throws SQLException
    {
        _log.info("Avvio elaborazione promemoria scadenzario (INCASSO + PAGAMENTO)");
        processaPromemoriaPerTipo(TIPO_INCASSO);
        processaPromemoriaPerTipo(TIPO_PAGAMENTO);
        _log.info("Elaborazione promemoria scadenzario terminata");
    }

    private void processaPromemoriaPerTipo(String tipo) throws SQLException
    {
        ScadenzarioRegoleDao dao = new ScadenzarioRegoleDao(jdbcTemplate);
        List<ScadenzarioRegolaDto> regole = dao.getAttiveByTipo(tipo);
        _log.info("[{}] {} regola/e attiva/e trovata/e", tipo, regole.size());
        if ( regole.isEmpty() )
        {
            return;
        }

        DatiAziendaDto datiAzienda = datiAziendaDelegate.getDatiAzienda();

        for ( ScadenzarioRegolaDto regola : regole )
        {
            String dataTarget = calcolaDataTarget(regola.getGiorniOffset());
            List<ScadenzaPromemoriaDto> scadenze = TIPO_INCASSO.equals(tipo)
                ? dao.getScadenzeIncasso(dataTarget)
                : dao.getScadenzePagamento(dataTarget);

            _log.info("[{}] Regola {} (offset {} giorni) -> data target {} -> {} scadenza/e trovata/e",
                tipo, regola.getId(), regola.getGiorniOffset(), dataTarget, scadenze.size());

            for ( ScadenzaPromemoriaDto scadenza : scadenze )
            {
                String oggetto = sostituisciPlaceholder(regola.getOggetto(), scadenza, regola.getGiorniOffset(), datiAzienda);
                try
                {
                    if ( dao.isGiaInviato(regola.getId(), tipo, scadenza.getIdScadenza()) )
                    {
                        _log.info("[{}] Scadenza {} gia' notificata per la regola {}, salto", tipo, scadenza.getIdScadenza(), regola.getId());
                        continue;
                    }

                    String destinatario = TIPO_INCASSO.equals(tipo)
                        ? scadenza.getEmailDestinatario()
                        : datiAzienda != null ? datiAzienda.getEmail() : null;

                    if ( StringUtils.isBlank(destinatario) )
                    {
                        _log.warn("[{}] Nessun destinatario per la scadenza {} (regola {}): promemoria non inviato", tipo, scadenza.getIdScadenza(), regola.getId());
                        dao.logInvio(regola.getId(), tipo, scadenza.getIdScadenza(), null, "KO", "Nessun indirizzo email disponibile per il destinatario", oggetto);
                        continue;
                    }

                    String corpo = sostituisciPlaceholder(regola.getCorpo(), scadenza, regola.getGiorniOffset(), datiAzienda);
                    boolean scaduta = regola.getGiorniOffset() != null && regola.getGiorniOffset() > 0;
                    String html = renderTemplate(oggetto, corpo, scadenza, datiAzienda, scaduta);

                    mailSenderService.send(oggetto, html, null, new String[] { destinatario });
                    dao.logInvio(regola.getId(), tipo, scadenza.getIdScadenza(), destinatario, "OK", null, oggetto);
                    _log.info("[{}] Promemoria inviato a {} per la scadenza {} (regola {})", tipo, destinatario, scadenza.getIdScadenza(), regola.getId());
                }
                catch ( MailException e )
                {
                    _log.error("Errore nell'invio del promemoria per la scadenza {} (regola {})", scadenza.getIdScadenza(), regola.getId(), e);
                    dao.logInvio(regola.getId(), tipo, scadenza.getIdScadenza(), null, "KO", StringUtils.abbreviate(e.getMessage(), 500), oggetto);
                }
                catch ( Exception e )
                {
                    _log.error("Errore generico nell'elaborazione del promemoria per la scadenza {} (regola {})", scadenza.getIdScadenza(), regola.getId(), e);
                    dao.logInvio(regola.getId(), tipo, scadenza.getIdScadenza(), null, "KO", StringUtils.abbreviate(e.getMessage(), 500), oggetto);
                }
            }
        }
    }

    private String calcolaDataTarget(int giorniOffset)
    {
        // giorniOffset negativo = promemoria X giorni PRIMA della scadenza -> scadenza.data = oggi - giorniOffset
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -giorniOffset);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    private String sostituisciPlaceholder(String testo,
                                          ScadenzaPromemoriaDto scadenza,
                                          int giorniOffset,
                                          DatiAziendaDto datiAzienda)
    {
        if ( StringUtils.isBlank(testo) )
        {
            return "";
        }
        String risultato = testo;
        risultato = risultato.replace("{{cliente}}", StringUtils.defaultString(scadenza.getNomeSoggetto()));
        risultato = risultato.replace("{{fornitore}}", StringUtils.defaultString(scadenza.getNomeSoggetto()));
        risultato = risultato.replace("{{numeroFattura}}", StringUtils.defaultString(scadenza.getNumeroDocumento()));
        risultato = risultato.replace("{{dataFattura}}", StringUtils.defaultString(scadenza.getDataDocumento()));
        risultato = risultato.replace("{{dataScadenza}}", StringUtils.defaultString(scadenza.getDataScadenza()));
        risultato = risultato.replace("{{importo}}", scadenza.getImporto() != null ? scadenza.getImporto().toString() + " Euro" : "");
        risultato = risultato.replace("{{totaleFattura}}", scadenza.getTotaleFattura() != null ? scadenza.getTotaleFattura().toString() + " Euro" : "");
        risultato = risultato.replace("{{descrizioneImporto}}", descriviImporto(scadenza));
        risultato = risultato.replace("{{giorniRitardo}}", String.valueOf(Math.max(giorniOffset, 0)));
        risultato = risultato.replace("{{ragioneSocialeAzienda}}", datiAzienda != null ? StringUtils.defaultString(datiAzienda.getDenominazione()) : "");
        return risultato;
    }

    /**
     * Descrive l'importo dovuto adattando la formulazione al caso: se la scadenza copre
     * l'intero totale della fattura (fattura in unica soluzione) evita di parlare di "rata".
     */
    private String descriviImporto(ScadenzaPromemoriaDto scadenza)
    {
        if ( scadenza.getImporto() == null )
        {
            return "";
        }
        String importoStr = scadenza.getImporto().toString() + " Euro";
        boolean unicaRata = scadenza.getTotaleFattura() == null
            || scadenza.getTotaleFattura().compareTo(scadenza.getImporto()) == 0;
        if ( unicaRata )
        {
            return "dell'importo di " + importoStr;
        }
        return "di una rata di " + importoStr + " (sul totale fattura di " + scadenza.getTotaleFattura() + " Euro)";
    }

    private String renderTemplate(String oggetto,
                                  String corpo,
                                  ScadenzaPromemoriaDto scadenza,
                                  DatiAziendaDto datiAzienda,
                                  boolean scaduta) throws Exception
    {
        Map<String, Object> model = new HashMap<>();
        model.put("oggetto", oggetto);
        model.put("corpo", corpo);
        model.put("aziendaNome", datiAzienda != null ? StringUtils.defaultIfBlank(datiAzienda.getDenominazione(), "") : "");
        model.put("importo", scadenza.getImporto() != null ? scadenza.getImporto().toString() + " Euro" : "");
        model.put("dataScadenza", StringUtils.defaultString(scadenza.getDataScadenza()));
        model.put("numeroFattura", StringUtils.defaultString(scadenza.getNumeroDocumento()));
        model.put("scaduta", scaduta);
        model.put("currentYear", new SimpleDateFormat("yyyy").format(new Date()));

        Template template = freeMarkerConfiguration.getTemplate("promemoria_scadenza.ftl");
        Writer out = new StringWriter();
        template.process(model, out);
        return out.toString();
    }

}
