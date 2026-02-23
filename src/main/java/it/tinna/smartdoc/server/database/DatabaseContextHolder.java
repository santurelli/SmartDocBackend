package it.tinna.smartdoc.server.database;

public class DatabaseContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setClientDatabase(String clientDatabase) {
        contextHolder.set(clientDatabase);
    }

    public static void set(String dbKey) {
        setClientDatabase(dbKey);
    }

    public static String getClientDatabase() {
        return contextHolder.get();
    }

    public static void clearClientDatabase() {
        contextHolder.remove();
    }

    public static void clear() {
        clearClientDatabase();
    }
}


