package com.emr.gds.core.repository;

import java.sql.SQLException;
import java.util.Map;

/**
 * Repository for loading abbreviations.
 */
public interface AbbreviationRepository {
    Map<String, String> findAll() throws SQLException;
}
