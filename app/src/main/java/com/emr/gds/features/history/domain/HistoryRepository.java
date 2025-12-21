package com.emr.gds.features.history.domain;

import java.util.List;

public interface HistoryRepository {
    List<String> getConditionsByCategory(ConditionCategory category);
    void addCondition(ConditionCategory category, String conditionName);
}
