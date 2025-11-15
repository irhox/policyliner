package de.tub.dima.policyliner.constants;

public enum PrivacyMetricName {

    UNIQUENESS_RATIO("uniquenessRatio"),
    POPULATION_UNIQUENESS("populationUniquenessRatio"),
    T_CLOSENESS("tCloseness"),
    DELTA_PRESENCE("deltaPresence");


    private final String value;

    PrivacyMetricName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
