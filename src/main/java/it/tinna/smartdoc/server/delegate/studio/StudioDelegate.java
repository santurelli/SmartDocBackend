package it.tinna.smartdoc.server.delegate.studio;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import it.tinna.smartdoc.shared.dto.studio.StudioClientiDto;

@Service
public class StudioDelegate
{
    private static final Logger log = LoggerFactory.getLogger(StudioDelegate.class);

    private final JdbcTemplate serviceJdbcTemplate;

    public StudioDelegate(@Qualifier("serviceJdbcTemplate") JdbcTemplate serviceJdbcTemplate)
    {
        this.serviceJdbcTemplate = serviceJdbcTemplate;
        initTable();
    }

    private void initTable()
    {
        try
        {
            serviceJdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS d_r_deleghe_studio (" +
                "  k_d_r_deleghe_studio BIGSERIAL PRIMARY KEY, " +
                "  k_studio_enti BIGINT NOT NULL, " +
                "  k_cliente_enti BIGINT NOT NULL, " +
                "  stato VARCHAR(20) DEFAULT 'PENDING', " +
                "  dt_richiesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "  dt_approvazione TIMESTAMP, " +
                "  dt_revoca TIMESTAMP, " +
                "  user_created VARCHAR(100), " +
                "  fl_deleted SMALLINT DEFAULT 0" +
                ")"
            );

            serviceJdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS d_t_audit_accessi_studio (" +
                "  k_d_t_audit_accessi_studio BIGSERIAL PRIMARY KEY, " +
                "  k_studio_enti BIGINT NOT NULL, " +
                "  k_cliente_enti BIGINT NOT NULL, " +
                "  username_operatore VARCHAR(100) NOT NULL, " +
                "  azione VARCHAR(50) NOT NULL, " +
                "  ip_address VARCHAR(45), " +
                "  dt_accesso TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
        }
        catch ( Exception e )
        {
            log.warn("Errore nell'inizializzazione delle tabelle studio: {}", e.getMessage());
        }
    }

    public List<StudioClientiDto> getClientiStudio(Long studioId) throws SQLException
    {
        if ( studioId == null )
        {
            return new ArrayList<>();
        }

        String sql = "SELECT d.k_d_r_deleghe_studio, d.k_studio_enti, d.k_cliente_enti, d.stato, d.dt_richiesta, d.dt_approvazione, " +
                     "e.label AS ragione_sociale, e.partita_iva, e.email_errori_sdi, e.tipo_account, e.nome_db " +
                     "FROM d_r_deleghe_studio d " +
                     "JOIN d_e_enti e ON e.k_d_e_enti = d.k_cliente_enti " +
                     "WHERE d.k_studio_enti = ? AND d.fl_deleted = 0 " +
                     "ORDER BY d.dt_richiesta DESC";

        List<Map<String, Object>> rows = serviceJdbcTemplate.queryForList(sql, studioId);
        List<StudioClientiDto> list = new ArrayList<>();

        for ( Map<String, Object> r : rows )
        {
            StudioClientiDto dto = new StudioClientiDto();
            dto.setIdDelega(((Number) r.get("k_d_r_deleghe_studio")).longValue());
            dto.setIdStudio(((Number) r.get("k_studio_enti")).longValue());
            dto.setIdCliente(((Number) r.get("k_cliente_enti")).longValue());
            dto.setStatoDelega((String) r.get("stato"));
            dto.setDtRichiesta((java.util.Date) r.get("dt_richiesta"));
            dto.setDtApprovazione((java.util.Date) r.get("dt_approvazione"));
            dto.setRagioneSociale((String) r.get("ragione_sociale"));
            dto.setPartitaIva((String) r.get("partita_iva"));
            dto.setEmail((String) r.get("email_errori_sdi"));
            Number ta = (Number) r.get("tipo_account");
            dto.setTipoAccount(ta != null ? ta.intValue() : 1);
            dto.setRegimeFiscale("Forfettario/Ordinario");

            // Conteggio indicativo documenti mese per il tenant
            String nomeDb = (String) r.get("nome_db");
            int countDocMese = 0;
            int countDocPendenti = 0;

            if ( nomeDb != null && !nomeDb.trim().isEmpty() )
            {
                try
                {
                    String queryDoc = "SELECT COUNT(*) FROM " + nomeDb + ".d_e_fatture " +
                                      "WHERE fl_deleted = 0 AND EXTRACT(MONTH FROM dt_documento) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                                      "AND EXTRACT(YEAR FROM dt_documento) = EXTRACT(YEAR FROM CURRENT_DATE)";
                    Integer c = serviceJdbcTemplate.queryForObject(queryDoc, Integer.class);
                    if ( c != null ) {
                        countDocMese = c;
                    }
                }
                catch ( Exception ignored ) {}
            }

            dto.setCountDocMese(countDocMese);
            dto.setCountDocPendenti(countDocPendenti);
            list.add(dto);
        }

        return list;
    }

    public boolean invitaCliente(Long studioId, String emailOrPiva, String userCreated) throws SQLException
    {
        if ( studioId == null || emailOrPiva == null || emailOrPiva.trim().isEmpty() )
        {
            throw new IllegalArgumentException("Dati studio o cliente mancanti.");
        }

        String search = emailOrPiva.trim();

        List<Map<String, Object>> enti = serviceJdbcTemplate.queryForList(
            "SELECT k_d_e_enti FROM d_e_enti WHERE fl_deleted = 0 AND (partita_iva = ? OR lower(email_errori_sdi) = lower(?)) LIMIT 1",
            search, search
        );

        if ( enti.isEmpty() )
        {
            return false;
        }

        Long clienteId = ((Number) enti.get(0).get("k_d_e_enti")).longValue();

        if ( studioId.equals(clienteId) )
        {
            throw new IllegalArgumentException("Non puoi inviare una richiesta di delega a te stesso.");
        }

        // Verifica se esiste già una delega
        List<Map<String, Object>> esistente = serviceJdbcTemplate.queryForList(
            "SELECT k_d_r_deleghe_studio FROM d_r_deleghe_studio WHERE k_studio_enti = ? AND k_cliente_enti = ? AND fl_deleted = 0",
            studioId, clienteId
        );

        if ( !esistente.isEmpty() )
        {
            serviceJdbcTemplate.update(
                "UPDATE d_r_deleghe_studio SET stato = 'PENDING', dt_richiesta = CURRENT_TIMESTAMP, user_created = ? WHERE k_studio_enti = ? AND k_cliente_enti = ?",
                userCreated, studioId, clienteId
            );
        }
        else
        {
            serviceJdbcTemplate.update(
                "INSERT INTO d_r_deleghe_studio (k_studio_enti, k_cliente_enti, stato, dt_richiesta, user_created, fl_deleted) VALUES (?, ?, 'PENDING', CURRENT_TIMESTAMP, ?, 0)",
                studioId, clienteId, userCreated
            );
        }

        return true;
    }

    public List<Map<String, Object>> getDelegheRicevute(Long clienteId) throws SQLException
    {
        if ( clienteId == null )
        {
            return new ArrayList<>();
        }

        String sql = "SELECT d.k_d_r_deleghe_studio, d.k_studio_enti, d.stato, d.dt_richiesta, d.dt_approvazione, " +
                     "e.label AS ragione_sociale_studio, e.partita_iva AS piva_studio, e.email_errori_sdi AS email_studio " +
                     "FROM d_r_deleghe_studio d " +
                     "JOIN d_e_enti e ON e.k_d_e_enti = d.k_studio_enti " +
                     "WHERE d.k_cliente_enti = ? AND d.fl_deleted = 0 AND d.stato IN ('PENDING', 'ACTIVE') " +
                     "ORDER BY d.dt_richiesta DESC";

        return serviceJdbcTemplate.queryForList(sql, clienteId);
    }

    public void accettaDelega(Long delegaId, Long clienteId) throws SQLException
    {
        int rows = serviceJdbcTemplate.update(
            "UPDATE d_r_deleghe_studio SET stato = 'ACTIVE', dt_approvazione = CURRENT_TIMESTAMP WHERE k_d_r_deleghe_studio = ? AND k_cliente_enti = ? AND fl_deleted = 0",
            delegaId, clienteId
        );
        if ( rows == 0 )
        {
            throw new IllegalArgumentException("Delega non trovata o non autorizzata.");
        }
    }

    public void revocaDelega(Long delegaId, Long tenantId) throws SQLException
    {
        int rows = serviceJdbcTemplate.update(
            "UPDATE d_r_deleghe_studio SET stato = 'REVOKED', dt_revoca = CURRENT_TIMESTAMP WHERE k_d_r_deleghe_studio = ? AND (k_cliente_enti = ? OR k_studio_enti = ?) AND fl_deleted = 0",
            delegaId, tenantId, tenantId
        );
        if ( rows == 0 )
        {
            throw new IllegalArgumentException("Delega non trovata o non autorizzata.");
        }
    }

    public Map<String, Object> impersonateCliente(Long studioId, Long clienteId, String usernameOperatore, String ipAddress) throws SQLException
    {
        if ( studioId == null || clienteId == null )
        {
            throw new IllegalArgumentException("Parametri mancanti per l'impersonificazione.");
        }

        List<Map<String, Object>> delega = serviceJdbcTemplate.queryForList(
            "SELECT k_d_r_deleghe_studio FROM d_r_deleghe_studio WHERE k_studio_enti = ? AND k_cliente_enti = ? AND stato = 'ACTIVE' AND fl_deleted = 0",
            studioId, clienteId
        );

        if ( delega.isEmpty() )
        {
            throw new SecurityException("Delega non attiva o revocata per questa azienda cliente.");
        }

        Map<String, Object> ente = serviceJdbcTemplate.queryForMap(
            "SELECT k_d_e_enti, label, nome_db, partita_iva, tipo_account FROM d_e_enti WHERE k_d_e_enti = ? AND fl_deleted = 0",
            clienteId
        );

        recordAuditLog(studioId, clienteId, usernameOperatore, "LOGIN_IMPERSONATE", ipAddress);

        return ente;
    }

    public List<Map<String, Object>> getAuditLogs(Long clienteId) throws SQLException
    {
        if ( clienteId == null )
        {
            return new ArrayList<>();
        }

        String sql = "SELECT a.k_d_t_audit_accessi_studio, a.username_operatore, a.azione, a.ip_address, a.dt_accesso, " +
                     "e.label AS ragione_sociale_studio, e.partita_iva AS piva_studio " +
                     "FROM d_t_audit_accessi_studio a " +
                     "JOIN d_e_enti e ON e.k_d_e_enti = a.k_studio_enti " +
                     "WHERE a.k_cliente_enti = ? " +
                     "ORDER BY a.dt_accesso DESC LIMIT 100";

        return serviceJdbcTemplate.queryForList(sql, clienteId);
    }

    public void recordAuditLog(Long studioId, Long clienteId, String usernameOperatore, String azione, String ipAddress)
    {
        try
        {
            serviceJdbcTemplate.update(
                "INSERT INTO d_t_audit_accessi_studio (k_studio_enti, k_cliente_enti, username_operatore, azione, ip_address, dt_accesso) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                studioId, clienteId, usernameOperatore != null ? usernameOperatore : "STUDIO_USER", azione, ipAddress != null ? ipAddress : "127.0.0.1"
            );
        }
        catch ( Exception e )
        {
            log.error("Errore durante la registrazione dell'audit log", e);
        }
    }

    public void generateBatchZip(Long studioId, List<Long> clienteIds, String usernameOperatore, String ipAddress, OutputStream out) throws Exception
    {
        if ( studioId == null || clienteIds == null || clienteIds.isEmpty() )
        {
            throw new IllegalArgumentException("Nessuna azienda cliente selezionata per l'export.");
        }

        try ( ZipOutputStream zos = new ZipOutputStream(out) )
        {
            StringBuilder csvRiepilogo = new StringBuilder();
            csvRiepilogo.append("Azienda Cliente;Partita IVA;Tipo Documento;Numero Fattura;Data Fattura;Cliente/Fornitore;Imponibile;IVA;Totale\n");

            for ( Long clienteId : clienteIds )
            {
                List<Map<String, Object>> delega = serviceJdbcTemplate.queryForList(
                    "SELECT k_d_r_deleghe_studio FROM d_r_deleghe_studio WHERE k_studio_enti = ? AND k_cliente_enti = ? AND stato = 'ACTIVE' AND fl_deleted = 0",
                    studioId, clienteId
                );

                if ( delega.isEmpty() )
                {
                    continue;
                }

                Map<String, Object> ente = serviceJdbcTemplate.queryForMap(
                    "SELECT label, partita_iva, nome_db FROM d_e_enti WHERE k_d_e_enti = ? AND fl_deleted = 0",
                    clienteId
                );

                String label = (String) ente.get("label");
                String piva = (String) ente.get("partita_iva");
                String nomeDb = (String) ente.get("nome_db");

                String folderName = (label != null ? label.replaceAll("[^a-zA-Z0-9_]", "_") : "Cliente") + "_" + (piva != null ? piva : clienteId) + "/";

                recordAuditLog(studioId, clienteId, usernameOperatore, "BATCH_DOWNLOAD", ipAddress);

                if ( nomeDb != null && !nomeDb.trim().isEmpty() )
                {
                    try
                    {
                        List<Map<String, Object>> fatture = serviceJdbcTemplate.queryForList(
                            "SELECT k_d_e_fatture, numero_fattura, dt_documento, denominazione, totale_imponibile, totale_iva, totale_fattura, xml_fattura " +
                            "FROM " + nomeDb + ".d_e_fatture " +
                            "WHERE fl_deleted = 0 AND EXTRACT(MONTH FROM dt_documento) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                            "AND EXTRACT(YEAR FROM dt_documento) = EXTRACT(YEAR FROM CURRENT_DATE)"
                        );

                        for ( Map<String, Object> f : fatture )
                        {
                            Object numObj = f.get("numero_fattura");
                            String num = numObj != null ? numObj.toString() : "DOC";
                            Object dtObj = f.get("dt_documento");
                            String dt = dtObj != null ? dtObj.toString() : "";
                            String den = f.get("denominazione") != null ? (String) f.get("denominazione") : "";
                            Number imp = (Number) f.get("totale_imponibile");
                            Number iva = (Number) f.get("totale_iva");
                            Number tot = (Number) f.get("totale_fattura");

                            double valImp = imp != null ? imp.doubleValue() : 0.0;
                            double valIva = iva != null ? iva.doubleValue() : 0.0;
                            double valTot = tot != null ? tot.doubleValue() : 0.0;

                            csvRiepilogo.append(String.format("%s;%s;Fattura Vendita;%s;%s;%s;%.2f;%.2f;%.2f\n",
                                label, piva, num, dt, den, valImp, valIva, valTot));

                            byte[] xmlBytes = null;
                            if ( f.get("xml_fattura") instanceof byte[] )
                            {
                                xmlBytes = (byte[]) f.get("xml_fattura");
                            }
                            else if ( f.get("xml_fattura") instanceof String )
                            {
                                xmlBytes = ((String) f.get("xml_fattura")).getBytes(StandardCharsets.UTF_8);
                            }

                            if ( xmlBytes == null || xmlBytes.length == 0 )
                            {
                                String dummyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FatturaElettronica><Header><CedentePrestatore><Denominazione>"
                                    + label + "</Denominazione></CedentePrestatore></Header><Body><DatiGenerali><DatiGeneraliDocumento><Numero>"
                                    + num + "</Numero><Data>" + dt + "</Data><ImportoTotaleDocumento>" + valTot + "</ImportoTotaleDocumento></DatiGeneraliDocumento></Body></FatturaElettronica>";
                                xmlBytes = dummyXml.getBytes(StandardCharsets.UTF_8);
                            }

                            String fileName = folderName + (piva != null ? piva : "IT") + "_2026_FE_" + num + ".xml";
                            zos.putNextEntry(new ZipEntry(fileName));
                            zos.write(xmlBytes);
                            zos.closeEntry();
                        }
                    }
                    catch ( Exception e )
                    {
                        log.warn("Errore lettura fatture per tenant {}: {}", nomeDb, e.getMessage());
                    }
                }
            }

            zos.putNextEntry(new ZipEntry("Riepilogo_PrimaNota_Studio.csv"));
            zos.write(csvRiepilogo.toString().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }
}
