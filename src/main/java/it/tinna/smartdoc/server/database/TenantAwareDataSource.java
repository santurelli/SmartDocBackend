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
        setupTenant(conn);
        return wrapConnection(conn);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = super.getConnection(username, password);
        setupTenant(conn);
        return wrapConnection(conn);
    }

    private void setupTenant(Connection conn) throws SQLException {
        String dbKey = DatabaseContextHolder.getClientDatabase();
        
        // Se dbKey è nullo, vuoto o coincide con servicedb, consideriamo nessun tenant specifico
        if (dbKey == null || dbKey.trim().isEmpty() || "servicedb".equalsIgnoreCase(dbKey)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("RESET app.current_tenant");
            }
            return;
        }

        Long tenantId = getTenantIdFromDbKey(dbKey);
        
        if (log.isDebugEnabled()) {
            log.debug("Setting app.current_tenant = {} (resolved from dbKey '{}') for connection", tenantId, dbKey);
        }
        
        try (Statement stmt = conn.createStatement()) {
            if (tenantId == null) {
                stmt.execute("RESET app.current_tenant");
            } else {
                stmt.execute("SET app.current_tenant = '" + tenantId + "'");
            }
        } catch (SQLException e) {
            log.error("Failed to set app.current_tenant to {}", tenantId, e);
            throw e;
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
                        if (log.isDebugEnabled()) {
                            log.debug("Resetting app.current_tenant on connection close");
                        }
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute("RESET app.current_tenant");
                        } catch (SQLException e) {
                            log.warn("Failed to reset app.current_tenant on connection close", e);
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
