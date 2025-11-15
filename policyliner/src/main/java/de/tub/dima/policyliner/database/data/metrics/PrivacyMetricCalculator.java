package de.tub.dima.policyliner.database.data.metrics;

import java.util.List;

public interface PrivacyMetricCalculator<T> {
    T getPrivacyMetricReportOfTable(String viewName, List<String> columns, List<String> sensitiveAttributes);
}
