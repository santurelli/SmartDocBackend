package it.tinna.smartdoc.server.database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Custom DataSource that wraps connections to set the PostgreSQL session variable 
 * 'app.current_tenant' before any SQL executes, and resets it when the connection is closed.
 * It resolves the string 'dbKey' to the numeric 'k_d_e_enti' ID from the service database.
 */
@Slf4j
public class TenantAwareDataSource extends DelegatingDataSource 
{
    private final DataSource serviceDataSource;
    private final ConcurrentHashMap<String, Long> tenantIdCache = new ConcurrentHashMap<>();

    public TenantAwareDataSource(DataSource targetDataSource, DataSource serviceDataSource) {
        super(targetDataSource);
        this.serviceDataSource = serviceDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        // PgBouncer transaction mode: in modalità transaction, ogni statement può finire su un backend
        // diverso. Impostando autoCommit=false forziamo l'apertura di una transazione esplicita
        // (lazy BEGIN al primo statement), garantendo che SET LOCAL e la query successiva
        // vengano eseguiti sullo stesso backend PostgreSQL.
        conn.setAutoCommit(false);
        setupTenant(conn);
        return wrapConnection(conn);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = super.getConnection(username, password);
        conn.setAutoCommit(false);
        setupTenant(conn);
        return wrapConnection(conn);
    }

    private void setupTenant(Connection conn) throws SQLException {
        String dbKey = DatabaseContextHolder.getClientDatabase();

        // Nessun tenant specifico (servicedb o contesto non impostato): non serve SET LOCAL.
        // La transazione è aperta ma senza app.current_tenant → RLS usa il valore di default.
        if (dbKey == null || dbKey.trim().isEmpty() || "servicedb".equalsIgnoreCase(dbKey)) {
            return;
        }

        Long tenantId = getTenantIdFromDbKey(dbKey);

        if (log.isDebugEnabled()) {
            log.debug("Setting app.current_tenant = {} (resolved from dbKey '{}') for connection", tenantId, dbKey);
        }

        if (tenantId != null) {
            // SET LOCAL è transaction-scoped: persiste fino al COMMIT/ROLLBACK della transazione
            // corrente. Con PgBouncer transaction mode, questo garantisce che tutti gli statement
            // della stessa "connessione logica" usino lo stesso backend PostgreSQL e vedano
            // il valore corretto di app.current_tenant per la RLS.
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET LOCAL app.current_tenant = '" + tenantId + "'");
            } catch (SQLException e) {
                log.error("Failed to set app.current_tenant to {}", tenantId, e);
                throw e;
            }
        } else {
            log.warn("Tenant ID non trovato per dbKey '{}', la RLS non filtrerà per tenant", dbKey);
        }
    }

    private Long getTenantIdFromDbKey(String dbKey) {
        return tenantIdCache.computeIfAbsent(dbKey, key -> {
            String sql = "SELECT k_d_e_enti FROM d_e_enti WHERE nome_db = ? AND fl_deleted = 0";
            try (Connection serviceConn = serviceDataSource.getConnection();
                 PreparedStatement pstmt = serviceConn.prepareStatement(sql)) {
                pstmt.setString(1, key);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("k_d_e_enti");
                        log.info("Resolved dbKey '{}' to k_d_e_enti {}", key, id);
                        return id;
                    }
                }
            } catch (SQLException e) {
                log.error("Error querying k_d_e_enti for dbKey '{}' in smartdoc_service_db", key, e);
            }
            return null;
        });
    }

    private Connection wrapConnection(final Connection conn) {
        return (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class<?>[]{Connection.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("close".equals(method.getName())) {
                        // Chiude la transazione corrente prima di restituire la connessione al pool.
                        // Con SET LOCAL, il COMMIT reverte automaticamente app.current_tenant al valore
                        // di default. Se la transazione è già stata committata da Spring (@Transactional),
                        // il COMMIT è un no-op (PostgreSQL emette WARNING ignorabile).
                        if (log.isDebugEnabled()) {
                            log.debug("Committing transaction on connection close (SET LOCAL app.current_tenant will be reverted)");
                        }
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("COMMIT");
                        } catch (SQLException e) {
                            log.warn("Failed to commit transaction on connection close, attempting rollback", e);
                            try (Statement stmt = conn.createStatement()) {
                                stmt.execute("ROLLBACK");
                            } catch (SQLException re) {
                                log.warn("Failed to rollback on connection close", re);
                            }
                        }
                    }
                    try {
                        return method.invoke(conn, args);
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }
        );
    }
}
