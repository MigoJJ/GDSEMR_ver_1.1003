package com.emr.gds.features.history.application;

import com.emr.gds.features.history.domain.ConditionCategory;
import com.emr.gds.features.history.domain.HistoryRepository;

import java.util.Arrays;
import java.util.List;

public class FamilyHistoryService {

    private final HistoryRepository repository;

    public FamilyHistoryService(HistoryRepository repository) {
        this.repository = repository;
        initializeDefaults();
    }

    public List<String> getConditions(ConditionCategory category) {
        return repository.getConditionsByCategory(category);
    }

    public void addCondition(ConditionCategory category, String name) {
        if (name != null && !name.trim().isEmpty()) {
            repository.addCondition(category, name.trim());
        }
    }

    private void initializeDefaults() {
        checkAndPopulate(ConditionCategory.ENDOCRINE, Arrays.asList(
            "Type 1 Diabetes", "Type 2 Diabetes", "Hypothyroidism", "Hyperthyroidism", "Thyroid Cancer"
        ));
        checkAndPopulate(ConditionCategory.CANCER, Arrays.asList(
            "Breast Cancer", "Lung Cancer", "Prostate Cancer", "Colon Cancer", "Skin Cancer"
        ));
        checkAndPopulate(ConditionCategory.CARDIOVASCULAR, Arrays.asList(
            "Coronary Artery Disease", "Hypertension", "Heart Attack", "Stroke", "Arrhythmia"
        ));
        checkAndPopulate(ConditionCategory.GENETIC, Arrays.asList(
            "Cystic Fibrosis", "Huntington's Disease", "Down Syndrome", "Sickle Cell Anemia", "Hemophilia"
        ));
    }

    private void checkAndPopulate(ConditionCategory category, List<String> defaults) {
        List<String> current = repository.getConditionsByCategory(category);
        if (current.isEmpty()) {
            for (String item : defaults) {
                repository.addCondition(category, item);
            }
        }
    }
}
