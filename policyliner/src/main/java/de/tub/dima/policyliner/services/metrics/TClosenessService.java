package de.tub.dima.policyliner.services.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.constants.MetricSeverity;
import de.tub.dima.policyliner.constants.PrivacyMetricName;
import de.tub.dima.policyliner.database.data.metrics.TClosenessCalculator;
import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import de.tub.dima.policyliner.entities.TClosenessReport;
import de.tub.dima.policyliner.entities.TClosenessReports;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TClosenessService implements PrivacyMetricService<TClosenessReports>{

    @Inject
    ObjectMapper objectMapper;

    private JsonQuasiIdentifiers quasiIdentifiers;

    private final TClosenessCalculator calculator;
    private final PrivacyMetricRepository privacyMetricRepository;

    public TClosenessService(
            TClosenessCalculator calculator,
            PrivacyMetricRepository privacyMetricRepository) {
        this.calculator = calculator;
        this.privacyMetricRepository = privacyMetricRepository;
    }


    @Override
    public TClosenessReports computeMetricForTable(String tableName) {
        if (quasiIdentifiers == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        }
        Optional<JsonQuasiIdentifier> identifier = quasiIdentifiers.getQuasiIdentifiers().stream().filter(q -> q.getViewName().equals(tableName)).findFirst();
        if (identifier.isPresent()) {
            return calculator.getTClosenessReportOfTable(tableName, identifier.get().getColumns(), identifier.get().getSensitiveAttributes());
        } else {
            Log.error("No quasi-identifier object found for view " + tableName);
            return null;
        }
    }

    @Override
    public List<TClosenessReports> computeMetricForTables(List<String> tableNames) {
        return List.of();
    }

    @Override
    public void evaluatePolicyAgainstMetric(Policy policy) {
        Log.info("Evaluating policy " + policy.id + " against TCloseness report");
        List<PrivacyMetric> policyPrivacyMetrics = privacyMetricRepository.findByPolicyId(policy.getId());
        PrivacyMetric lowerT = policyPrivacyMetrics.stream().
                filter(p -> p.name.equals(PrivacyMetricName.T_CLOSENESS.getValue()) && p.metricSeverity.equals(MetricSeverity.LOWER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric upperT = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals(PrivacyMetricName.T_CLOSENESS.getValue()) && p.metricSeverity.equals(MetricSeverity.UPPER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric middleT = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals(PrivacyMetricName.T_CLOSENESS.getValue()) && p.metricSeverity.equals(MetricSeverity.MIDDLE_VALUE))
                .findFirst().orElse(null);
        if (lowerT == null && upperT == null && middleT == null) {
            Log.warn("No privacy metrics found for policy " + policy.id + ". Skipping TCloseness report evaluation.");
        } else {
            String viewName = policy.viewName != null ? policy.viewName : policy.materializedViewName;
            Instant start = Instant.now();
            TClosenessReports reports = computeMetricForTable(viewName);
            Instant end = Instant.now();

            for (TClosenessReport report : reports.getReports()) {
                if (upperT != null && report != null && report.getMaxDistance().doubleValue() > Double.parseDouble(upperT.value)) {
                    Alert newAlert = new Alert();
                    newAlert.type = AlertType.POLICY;
                    newAlert.severity = AlertSeverity.SEVERE;
                    newAlert.message = """
                            Policy with view %s has a high TCloseness value of %.3f on sensitive value %s.
                            This policy is most likely susceptible to Attribute Inference Attacks. The attributes in equivalence classes should be distributed better.
                            """.formatted(report.getViewName(), report.getMaxDistance(), report.getSensitiveAttribute());
                    newAlert.persist();
                    policy.alerts.add(newAlert);
                } else if (middleT != null && report != null && report.getMaxDistance().doubleValue() > Double.parseDouble(middleT.value)) {
                    Alert newAlert = new Alert();
                    newAlert.type = AlertType.POLICY;
                    newAlert.severity = AlertSeverity.WARNING;
                    newAlert.message = """
                            Policy with view %s has a TCloseness value of %.3f on sensitive value %s.
                            Depending on the sensitivity of the data, this policy could susceptible to Attribute Inference Attacks.
                            """.formatted(report.getViewName(), report.getMaxDistance(), report.getSensitiveAttribute());
                    newAlert.persist();
                    policy.alerts.add(newAlert);
                } else if (lowerT != null && report != null && report.getMaxDistance().doubleValue() < Double.parseDouble(lowerT.value)) {
                    Alert newAlert = new Alert();
                    newAlert.type = AlertType.POLICY;
                    newAlert.severity = AlertSeverity.INFO;
                    newAlert.message = """
                            Policy with view %s has a low TCloseness value of %.3f on sensitive value %s.
                            Please review the generalization / masking of the data in this view.
                            The usability of the data can most likely be improved.
                            """.formatted(report.getViewName(), report.getMaxDistance(), report.getSensitiveAttribute());
                    newAlert.persist();
                    policy.alerts.add(newAlert);
                }
            }

            Policy.getEntityManager().merge(policy);
            Log.info("DONE Evaluating policy " + policy.id + " against TCloseness report in: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
        }
    }
}
