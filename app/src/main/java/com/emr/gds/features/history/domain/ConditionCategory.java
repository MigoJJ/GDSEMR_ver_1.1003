package com.emr.gds.features.history.domain;

public enum ConditionCategory {
    ENDOCRINE,
    CANCER,
    CARDIOVASCULAR,
    GENETIC;

    @Override
    public String toString() {
        // Capitalize first letter, lower case rest
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
