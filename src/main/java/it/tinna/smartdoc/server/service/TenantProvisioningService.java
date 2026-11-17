package it.tinna.smartdoc.server.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Popola i dati di base (gruppi/ruoli e regimi fiscali) di un nuovo tenant creato dalla
 * registrazione da sito web.
 *
 * Va invocato con il tenant gia' impostato in DatabaseContextHolder (come per l'insert in
 * d_e_utenti), cosi' TenantAwareDataSource applica "SET LOCAL app.current_tenant" e la RLS
 * accetta le insert con il tenant_id del nuovo ente.
 */
@Service
@Slf4j
public class TenantProvisioningService {

    /** Gruppi (ruoli) creati per ogni nuovo tenant. */
    private static final String[] GRUPPI = { "ADMIN", "ACCOUNTING", "SALES", "WAREHOUSE", "WAREHOUSE_OPERATOR" };

    /** Regimi fiscali (descrizione, value) allineati a RegimeFiscaleEnum. */
    private static final String[][] REGIMI_FISCALI = {
        { "Regime Ordinario", "ORDINARIO" },
        { "Contribuenti minimi (art.1, c.96-117, L. 244/07)", "CONTRIBUENTI_MINIMI" },
        { "Nuove iniziative produttive (art.13, L. 388/00)", "INIZIATIVE_PRODUTTIVE" },
        { "Agricoltura e attività connesse e pesca (artt.34 e 34-bis, DPR 633/72)", "AGRICOLTURA_PESCA" },
        { "Vendita sali e tabacchi (art.74, c.1, DPR. 633/72)", "SALI_TABACCHI" },
        { "Commercio fiammiferi (art.74, c.1, DPR 633/72)", "FIAMMIFERI" },
        { "Editoria (art.74, c.1, DPR 633/72)", "EDITORIA" },
        { "Gestione servizi telefonia pubblica (art.74, c.1, DPR 633/72)", "TELEFONIA" },
        { "Rivendita documenti di trasporto pubblico e di sosta (art.74, c.1, DPR 633/72)", "TRASPORTO_PUBBLICO" },
        { "Intrattenimenti, giochi e altre attività di cui alla tariffa allegata al DPR 640/72 (art.74, c.6, DPR 633/72)", "INTRATTENIMENTI" },
        { "Agenzie viaggi e turismo (art.74-ter, DPR 633/72)", "VIAGGI_TURISMO" },
        { "Agriturismo (art.5, c.2, L. 413/91)", "AGRITURISMO" },
        { "Vendite a domicilio (art.25-bis, c.6, DPR 600/73)", "DOMICILIO" },
        { "Rivendita beni usati, oggetti d'arte, d'antiquariato o da collezione (art.36, DL 41/95)", "RIVENDITA_ARTE_COLLEZIONE" },
        { "Agenzie di vendite all'asta di oggetti d'arte, antiquariato o da collezione (art.40-bis, DL 41/95)", "AGENZIE_ARTE_COLLEZIONE" },
        { "IVA per cassa P.A. (art.6, c.5, DPR 633/72)", "CASSA_PA" },
        { "IVA per cassa (art. 32-bis, DL 83/2012)", "CASSA" },
        { "Altro", "ALTRO" },
        { "Regime forfettario (art.1, c.54-89, L. 190/2014)", "REGIME_FORFETTARIO" } };

    /** Aliquote IVA (codice, imposta, indetraibilita, classe, descrizione, note, predefinita). */
    private static final String[][] ALIQUOTE_IVA = {
        { "10", "10.00", null, "IM", "Aliquota 10%", null, "0" },
        { "21detr", "21.00", "50.00", "IM", "Aliquota 21% detraibile al 50%", null, "0" },
        { "21", "21.00", null, "IM", "Aliquota 21%", null, "0" },
        { "22", "22.00", null, "IM", "Aliquota 22%", null, "1" },
        { "21exUe", "21.00", null, "EU", "Aliq. 21% acquisti extra-Ue", null, "0" },
        { "21i", "21.00", "100.00", "IM", "Aliq. 21% indetraibile", null, "0" },
        { "21inUe", "21.00", null, "IU", "Aliq. 21% acquisti intra-Ue", null, "0" },
        { "21rc", "21.00", null, "RC", "Aliq. 21% reverse-charge", null, "0" },
        { "4", "4.00", null, "IM", "Aliquota 4%", null, "0" },
        { "22detr", "22.00", "50.00", "IM", "Aliq. 22% detr. al 50%", null, "0" },
        { "22exUe", "22.00", null, "EU", "Aliq. 22% acquisti extra-Ue", null, "0" },
        { "22i", "22.00", "100.00", "IM", "Aliq. 22% indetraibile", null, "0" },
        { "22inUe", "22.00", null, "IU", "Aliq. 22% acquisti intra-Ue", null, "0" },
        { "22rc", "22.00", null, "RC", "Aliq. 22% reverse-charge", null, "0" },
        { "E41", "0.00", null, "NON_IMPONIBILI", "Non imp. Art. 41 D.L. 331/1993", null, "0" },
        { "E71", "0.00", null, "NON_IMPONIBILI", "Non Imp. art. 71 DPR 633/72", null, "0" },
        { "FC", "0.00", null, "NON_IMPONIBILI", "Fuori campo IVA", "(Riservato alle Spese fuori campo Iva)", "0" },
        { "N40", "0.00", null, "NON_IMPONIBILI", "Non imp. Art. 40 L. 427/93", null, "0" },
        { "N8", "0.00", null, "NON_IMPONIBILI", "Non Imp. art. 8 DPR 633/42", null, "0" },
        { "X15", "0.00", null, "ESCLUSE_ART_15", "Escl. art. 15 DPR 633/72", "Bolli su fatture", "0" }
    };

    /**
     * Tipi di pagamento di base (descrizione, modalita' SDI, salda subito, giorni, fine mese).
     * Ognuno ha un'unica scadenza al 100% del totale.
     */
    private static final String[][] TIPI_PAGAMENTO = {
        { "Contanti", "MP01", "1", "0", "0" },
        { "Carta di credito", "MP08", "1", "0", "0" },
        { "Bonifico bancario vista fattura", "MP05", "0", "0", "0" },
        { "Bonifico bancario 30 gg data fattura", "MP05", "0", "30", "0" },
        { "Bonifico bancario 30 gg fine mese", "MP05", "0", "30", "1" },
        { "Bonifico bancario 60 gg data fattura", "MP05", "0", "60", "0" },
        { "Bonifico bancario 60 gg fine mese", "MP05", "0", "60", "1" },
        { "Bonifico bancario 90 gg data fattura", "MP05", "0", "90", "0" },
        { "Ri.Ba. 30 gg fine mese", "MP12", "0", "30", "1" },
        { "Ri.Ba. 60 gg fine mese", "MP12", "0", "60", "1" },
        { "Assegno", "MP02", "1", "0", "0" } };

    @Autowired
    @Qualifier("sharedJdbcTemplate")
    private JdbcTemplate sharedJdbcTemplate;

    /** Hash bcrypt della password dell'utente "support" (smartdoc.support.password-hash), uguale per tutti i tenant. */
    @Value("${smartdoc.support.password-hash:}")
    private String supportPasswordHash;

    /**
     * Crea (se mancanti) i gruppi e i regimi fiscali del tenant.
     *
     * @return id del gruppo ADMIN del tenant, da assegnare all'utente amministratore
     */
    public long initTenantData(long tenantId)
    {
        Long adminGroupId = sharedJdbcTemplate.execute((Connection conn) -> {
            Long adminId = null;
            for ( String gruppo : GRUPPI )
            {
                Long id = findGruppo(conn, tenantId, gruppo);
                if ( id == null )
                {
                    // La PK k_d_e_gruppi e' generata dalla sequence della tabella
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO d_e_gruppi (descrizione, tenant_id) VALUES (?, ?) RETURNING k_d_e_gruppi"))
                    {
                        ps.setString(1, gruppo);
                        ps.setLong(2, tenantId);
                        try (ResultSet rs = ps.executeQuery())
                        {
                            rs.next();
                            id = rs.getLong(1);
                        }
                    }
                }
                if ( "ADMIN".equals(gruppo) )
                {
                    adminId = id;
                }
            }

            for ( String[] regime : REGIMI_FISCALI )
            {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO c_e_tabdecod (descrizione, categoria, value, fl_deleted, tenant_id) "
                                + "SELECT ?, 'REGIME_FISCALE', ?, 0, ? "
                                + "WHERE NOT EXISTS (SELECT 1 FROM c_e_tabdecod WHERE tenant_id = ? AND categoria = 'REGIME_FISCALE' AND value = ?)"))
                {
                    ps.setString(1, regime[0]);
                    ps.setString(2, regime[1]);
                    ps.setLong(3, tenantId);
                    ps.setLong(4, tenantId);
                    ps.setString(5, regime[1]);
                    ps.executeUpdate();
                }
            }
            createAliquoteIva(conn, tenantId);
            createMagazzinoPredefinito(conn, tenantId);
            createTipiPagamento(conn, tenantId);
            createSupportUser(conn, tenantId, adminId);
            return adminId;
        });

        if ( adminGroupId == null )
        {
            throw new IllegalStateException("Gruppo ADMIN non creato per il tenant " + tenantId);
        }
        log.info("Inizializzati gruppi e regimi fiscali per il tenant {} (gruppo ADMIN = {})", tenantId, adminGroupId);
        return adminGroupId;
    }

    /** Crea i tipi di pagamento di base del tenant (solo se non ne ha ancora nessuno). */
    private void createTipiPagamento(Connection conn, long tenantId) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM d_e_tipipagamento WHERE tenant_id = ? AND fl_deleted = 0"))
        {
            ps.setLong(1, tenantId);
            try (ResultSet rs = ps.executeQuery())
            {
                if ( rs.next() && rs.getLong(1) > 0 )
                {
                    return;
                }
            }
        }

        // d_e_scadenze_tipipagamento puo' avere o meno la colonna tenant_id a seconda dello schema
        boolean scadenzeConTenant = false;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM information_schema.columns WHERE table_name = 'd_e_scadenze_tipipagamento' AND column_name = 'tenant_id' LIMIT 1");
                ResultSet rs = ps.executeQuery())
        {
            scadenzeConTenant = rs.next();
        }

        for ( String[] tp : TIPI_PAGAMENTO )
        {
            long idTipo;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO d_e_tipipagamento (descrizione, modalita, fl_salda_subito, giorno_pagamento_mese_successivo, fl_sposta_scadenze, fl_predefinito, fl_deleted, dt_created, tenant_id) "
                            + "VALUES (?, ?, ?::int, 0, 0, 0, 0, CURRENT_TIMESTAMP, ?) RETURNING k_d_e_tipipagamento"))
            {
                ps.setString(1, tp[0]);
                ps.setString(2, tp[1]);
                ps.setString(3, tp[2]);
                ps.setLong(4, tenantId);
                try (ResultSet rs = ps.executeQuery())
                {
                    rs.next();
                    idTipo = rs.getLong(1);
                }
            }

            String sql = scadenzeConTenant
                    ? "INSERT INTO d_e_scadenze_tipipagamento (k_d_e_tipipagamento, giorni, perc_totale, fl_finemese, tenant_id) VALUES (?, ?::int, 100, ?::int, ?)"
                    : "INSERT INTO d_e_scadenze_tipipagamento (k_d_e_tipipagamento, giorni, perc_totale, fl_finemese) VALUES (?, ?::int, 100, ?::int)";
            try (PreparedStatement ps = conn.prepareStatement(sql))
            {
                ps.setLong(1, idTipo);
                ps.setString(2, tp[3]);
                ps.setString(3, tp[4]);
                if ( scadenzeConTenant )
                {
                    ps.setLong(4, tenantId);
                }
                ps.executeUpdate();
            }
        }
    }

    /** Crea il magazzino predefinito del tenant (necessario ai movimenti di magazzino), se non ce n'e' gia' uno. */
    private void createMagazzinoPredefinito(Connection conn, long tenantId) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO d_e_magazzini (descrizione, fl_predefinito, fl_deleted, tenant_id) "
                        + "SELECT 'Magazzino Principale', 1, 0, ? "
                        + "WHERE NOT EXISTS (SELECT 1 FROM d_e_magazzini WHERE tenant_id = ? AND fl_deleted = 0)"))
        {
            ps.setLong(1, tenantId);
            ps.setLong(2, tenantId);
            ps.executeUpdate();
        }
    }

    /** Crea le aliquote IVA di base del tenant (solo quelle con codice non ancora presente). */
    private void createAliquoteIva(Connection conn, long tenantId) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO d_e_aliquoteiva (codice, imposta, indetraibilita, classe, descrizione, note, fl_predefinita, fl_deleted, dt_created, tenant_id) "
                        + "SELECT ?, ?::numeric, ?::numeric, ?, ?, ?, ?::int, 0, CURRENT_TIMESTAMP, ? "
                        + "WHERE NOT EXISTS (SELECT 1 FROM d_e_aliquoteiva WHERE tenant_id = ? AND codice = ? AND fl_deleted = 0)"))
        {
            for ( String[] a : ALIQUOTE_IVA )
            {
                ps.setString(1, a[0]);
                ps.setString(2, a[1]);
                ps.setString(3, a[2]);
                ps.setString(4, a[3]);
                ps.setString(5, a[4]);
                ps.setString(6, a[5]);
                ps.setString(7, a[6]);
                ps.setLong(8, tenantId);
                ps.setLong(9, tenantId);
                ps.setString(10, a[0]);
                ps.executeUpdate();
            }
        }
    }

    /**
     * Crea l'utente "support" (gruppo ADMIN) del tenant, se non esiste gia'.
     * Se la password non e' configurata l'utente non viene creato (mai con password vuota).
     */
    private void createSupportUser(Connection conn, long tenantId, Long adminGroupId) throws SQLException
    {
        if ( supportPasswordHash == null || supportPasswordHash.isBlank() )
        {
            log.error("Proprieta' smartdoc.support.password-hash non configurata: utente support NON creato per il tenant {}", tenantId);
            return;
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO d_e_utenti (nome, cognome, k_d_e_gruppi, username, password, fl_deleted, tenant_id) "
                        + "SELECT 'Support', 'SmartDOC', ?, 'support', ?, 0, ? "
                        + "WHERE NOT EXISTS (SELECT 1 FROM d_e_utenti WHERE username = 'support' AND tenant_id = ?)"))
        {
            ps.setLong(1, adminGroupId);
            ps.setString(2, supportPasswordHash.trim());
            ps.setLong(3, tenantId);
            ps.setLong(4, tenantId);
            ps.executeUpdate();
        }
    }

    private Long findGruppo(Connection conn, long tenantId, String descrizione) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement("SELECT k_d_e_gruppi FROM d_e_gruppi WHERE tenant_id = ? AND descrizione = ?"))
        {
            ps.setLong(1, tenantId);
            ps.setString(2, descrizione);
            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next() ? rs.getLong(1) : null;
            }
        }
    }
}
