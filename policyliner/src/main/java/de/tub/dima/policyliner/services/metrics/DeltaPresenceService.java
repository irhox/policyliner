package de.tub.dima.policyliner.services.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.constants.MetricSeverity;
import de.tub.dima.policyliner.database.data.metrics.DeltaPresenceCalculator;
import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.entities.DeltaPresenceReport;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DeltaPresenceService implements PrivacyMetricService<DeltaPresenceReport> {

    @Inject
    ObjectMapper objectMapper;

    private final PrivacyMetricRepository privacyMetricRepository;
    private final DeltaPresenceCalculator calculator;

    private JsonQuasiIdentifiers quasiIdentifiers;


    public DeltaPresenceService(
            DeltaPresenceCalculator calculator,
            PrivacyMetricRepository privacyMetricRepository) {
        this.calculator = calculator;
        this.privacyMetricRepository = privacyMetricRepository;
    }

    @Override
    public DeltaPresenceReport computeMetricForTable(String tableName) {
        if (quasiIdentifiers == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        }

        Optional<JsonQuasiIdentifier> identifier = quasiIdentifiers.getQuasiIdentifiers().stream().filter(q -> q.getViewName().equals(tableName)).findFirst();
        if (identifier.isPresent()) {
                return calculator.getDeltaPresenceReportOfTable(tableName, identifier.get().getColumns());
        } else {
            Log.error("No quasi-identifier object found for view " + tableName);
        }
        return null;
    }

    @Override
    public List<DeltaPresenceReport> computeMetricForTables(List<String> tableNames) {
        return List.of();
    }

    @Override
    public void evaluatePolicyAgainstMetric(Policy policy) {
        Log.info("Evaluating policy " + policy.id + " against delta presence report");
        List<PrivacyMetric> policyPrivacyMetrics = privacyMetricRepository.findByPolicyId(policy.getId());
        PrivacyMetric lowerDeltaPresence = policyPrivacyMetrics.stream().
                filter(p -> p.name.equals("deltaPresence") && p.metricSeverity.equals(MetricSeverity.LOWER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric upperDeltaPresence = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals("deltaPresence") && p.metricSeverity.equals(MetricSeverity.UPPER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric middleDeltaPresence = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals("deltaPresence") && p.metricSeverity.equals(MetricSeverity.MIDDLE_VALUE))
                .findFirst().orElse(null);
        if (lowerDeltaPresence == null && upperDeltaPresence == null && middleDeltaPresence == null) {
            Log.warn("No privacy metrics found for policy " + policy.id + ". Skipping delta presence report evaluation.");
        } else {
            String viewName = policy.viewName != null ? policy.viewName : policy.materializedViewName;
            Instant start = Instant.now();
            DeltaPresenceReport report = computeMetricForTable(viewName);
            Instant end = Instant.now();

            if (upperDeltaPresence != null && report != null && report.getMaxDeltaPresence().doubleValue() > Double.parseDouble(upperDeltaPresence.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.SEVERE;
                newAlert.message = """
                        Policy with view %s has a high delta presence of %.3f.
                        This is a cause for concern and should be reviewed. The view columns should most likely be generalized / masked better.
                        """.formatted(report.getViewName(), report.getMaxDeltaPresence());
                newAlert.persist();
                policy.alerts.add(newAlert);
            } else if (middleDeltaPresence != null && report != null && report.getMaxDeltaPresence().doubleValue() > Double.parseDouble(middleDeltaPresence.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.WARNING;
                newAlert.message = """
                        Policy with view %s has a high delta presence of %.3f.
                        Depending on the sensitivity of the data, this may be a cause for concern and should be reviewed.
                """.formatted(report.getViewName(), report.getMaxDeltaPresence());
                newAlert.persist();
                policy.alerts.add(newAlert);
            } else if (lowerDeltaPresence != null && report != null && report.getMaxDeltaPresence().doubleValue() < Double.parseDouble(lowerDeltaPresence.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.INFO;
                newAlert.message = """
                        Policy with view %s has a low delta presence of %.3f.
                        Please review the generalization / masking of the data in this view.
                        The usability of the data can most likely be improved.
                        """.formatted(report.getViewName(), report.getMaxDeltaPresence());
                newAlert.persist();
                policy.alerts.add(newAlert);
            }

            Policy.getEntityManager().merge(policy);
            Log.info("DONE Evaluating policy " + policy.id + " against delta presence report in: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
        }
    }
}
