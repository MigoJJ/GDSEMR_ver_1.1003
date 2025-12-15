package com.emr.gds.infrastructure.persistence.jdbc;

import com.emr.gds.core.db.AppDatabaseManager;
import com.emr.gds.core.repository.AbbreviationRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC-backed repository for abbreviations stored in SQLite.
 */
public class JdbcAbbreviationRepository implements AbbreviationRepository {

    private final AppDatabaseManager dbManager = AppDatabaseManager.getInstance();

    @Override
    public Map<String, String> findAll() throws SQLException {
        Map<String, String> abbreviations = new HashMap<>();
        Connection conn = dbManager.getAbbreviationConnection();
        ensureTable(conn);

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT short, full FROM abbreviations ORDER BY short")) {
            while (rs.next()) {
                abbreviations.put(rs.getString("short"), rs.getString("full"));
            }
        }
        return abbreviations;
    }

    private void ensureTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS abbreviations (short TEXT PRIMARY KEY, full TEXT NOT NULL)");
        }
    }
}
