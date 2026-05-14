package org.example.pidev.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper léger pour détecter dynamiquement les colonnes disponibles dans la base.
 * Permet de garder la compatibilité entre plusieurs variantes de schéma.
 */
public final class DbSchemaSupport {

    private static final Map<String, Boolean> COLUMN_CACHE = new ConcurrentHashMap<>();

    private DbSchemaSupport() {
    }

    /**
     * Vide le cache des colonnes. À appeler au démarrage de l'application
     * pour éviter des valeurs obsolètes entre sessions.
     */
    public static void clearCache() {
        COLUMN_CACHE.clear();
    }

    public static boolean hasColumn(Connection connection, String tableName, String columnName) {
        if (connection == null || tableName == null || columnName == null) {
            return false;
        }

        String cacheKey = tableName.toLowerCase(Locale.ROOT) + "." + columnName.toLowerCase(Locale.ROOT);
        return COLUMN_CACHE.computeIfAbsent(cacheKey, key -> {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                try (ResultSet rs = metaData.getColumns(connection.getCatalog(), null, tableName, columnName)) {
                    if (rs.next()) {
                        return true;
                    }
                }
                // Essai tolérant aux variations de casse / schéma
                try (ResultSet rs = metaData.getColumns(connection.getCatalog(), null,
                        tableName.toUpperCase(Locale.ROOT), columnName.toUpperCase(Locale.ROOT))) {
                    return rs.next();
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Impossible de vérifier la colonne " + tableName + "." + columnName + " : " + e.getMessage());
                return false;
            }
        });
    }

    public static int getInt(ResultSet rs, String columnName, int defaultValue) {
        try {
            if (hasColumn(rs, columnName)) {
                int value = rs.getInt(columnName);
                return rs.wasNull() ? defaultValue : value;
            }
        } catch (SQLException ignored) {
        }
        return defaultValue;
    }

    public static String getString(ResultSet rs, String columnName, String defaultValue) {
        try {
            if (hasColumn(rs, columnName)) {
                String value = rs.getString(columnName);
                return value != null ? value : defaultValue;
            }
        } catch (SQLException ignored) {
        }
        return defaultValue;
    }

    public static java.sql.Date getDate(ResultSet rs, String columnName) {
        try {
            if (hasColumn(rs, columnName)) {
                return rs.getDate(columnName);
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public static boolean hasColumn(ResultSet rs, String columnName) {
        if (rs == null || columnName == null) {
            return false;
        }
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String label = metaData.getColumnLabel(i);
                String name = metaData.getColumnName(i);
                if (columnName.equalsIgnoreCase(label) || columnName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        } catch (SQLException ignored) {
        }
        return false;
    }
}

