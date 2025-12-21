package com.emr.gds.features.history.adapter.out.persistence;

import com.emr.gds.core.db.AppDatabaseManager;
import com.emr.gds.features.history.domain.ConditionCategory;
import com.emr.gds.features.history.domain.HistoryRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcHistoryRepository implements HistoryRepository {

    public JdbcHistoryRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS conditions (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "category TEXT NOT NULL, " +
                     "name TEXT NOT NULL, " +
                     "UNIQUE(category, name))";
        try (Connection conn = AppDatabaseManager.getInstance().getHistoryConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace(); // Log appropriately in real app
        }
    }

    @Override
    public List<String> getConditionsByCategory(ConditionCategory category) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT name FROM conditions WHERE category = ? ORDER BY name";
        
        try (Connection conn = AppDatabaseManager.getInstance().getHistoryConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void addCondition(ConditionCategory category, String conditionName) {
        String sql = "INSERT OR IGNORE INTO conditions (category, name) VALUES (?, ?)";
        
        try (Connection conn = AppDatabaseManager.getInstance().getHistoryConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.name());
            pstmt.setString(2, conditionName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
