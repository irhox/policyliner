package de.tub.dima.policyliner.entities;

import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;

import java.util.List;

public class DefaultPrivacyMetrics {
    private List<PrivacyMetric> defaultMetrics;

    public DefaultPrivacyMetrics(List<PrivacyMetric> defaultMetrics) {
        this.defaultMetrics = defaultMetrics;
    }

    public List<PrivacyMetric> getDefaultMetrics() {
        return defaultMetrics;
    }

    public void setDefaultMetrics(List<PrivacyMetric> defaultMetrics) {
        this.defaultMetrics = defaultMetrics;
    }
}
