package de.tub.dima.policyliner.services.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.constants.MetricSeverity;
import de.tub.dima.policyliner.constants.PrivacyMetricName;
import de.tub.dima.policyliner.database.data.metrics.popuniqueness.PopulationUniquenessEstimationCalculator;
import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import de.tub.dima.policyliner.entities.PopulationUniquenessReport;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PopulationUniquenessEstimationService implements PrivacyMetricService<PopulationUniquenessReport> {

    @Inject
    ObjectMapper objectMapper;

    private final PrivacyMetricRepository privacyMetricRepository;
    private final PopulationUniquenessEstimationCalculator calculator;

    private JsonQuasiIdentifiers quasiIdentifiers;

    public PopulationUniquenessEstimationService(
            PopulationUniquenessEstimationCalculator calculator,
            PrivacyMetricRepository privacyMetricRepository) {
        this.calculator = calculator;
        this.privacyMetricRepository = privacyMetricRepository;
    }
    @Override
    public PopulationUniquenessReport computeMetricForTable(String tableName, JsonQuasiIdentifier localQuasiIdentifier) {
        if (quasiIdentifiers == null && localQuasiIdentifier == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        } else if (localQuasiIdentifier != null) {
            quasiIdentifiers = new JsonQuasiIdentifiers(List.of(localQuasiIdentifier));
        }
        Optional<JsonQuasiIdentifier> identifier = quasiIdentifiers.getQuasiIdentifiers().stream().filter(q -> q.getViewName().equals(tableName)).findFirst();

        if (identifier.isPresent()) {
            return calculator.getPrivacyMetricReportOfTable(tableName, identifier.get().getColumns(), List.of());
        } else {
            Log.error("No quasi-identifier object found for view " + tableName);
        }
        return null;
    }

    @Override
    public List<PopulationUniquenessReport> computeMetricForTables(List<String> tableNames, JsonQuasiIdentifiers localQuasiIdentifiers) {
        if (quasiIdentifiers == null && localQuasiIdentifiers == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        } else if (localQuasiIdentifiers != null) {
            quasiIdentifiers = localQuasiIdentifiers;
        }

        List<PopulationUniquenessReport> reports = new ArrayList<>();

        for (JsonQuasiIdentifier identifier : quasiIdentifiers.getQuasiIdentifiers()) {
            if (tableNames.contains(identifier.getViewName())) {
                try {
                    PopulationUniquenessReport report = calculator.getPrivacyMetricReportOfTable(
                            identifier.getViewName(),
                            identifier.getColumns(),
                            identifier.getSensitiveAttributes()
                    );
                    report.setViewName(identifier.getViewName());
                    reports.add(report);
                } catch (Exception e) {
                    Log.error("Failed to get population uniqueness report for view: " + identifier.getViewName(), e);
                }
            }
        }

        return reports;
    }

    @Override
    public void evaluatePolicyAgainstMetric(Policy policy, JsonQuasiIdentifiers localQuasiIdentifiers) {
        Log.info("Evaluating policy " + policy.id + " against population uniqueness report");
        List<PrivacyMetric> policyPrivacyMetrics = privacyMetricRepository.findByPolicyId(policy.getId());
        PrivacyMetric lowerPopUniqueness = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals(PrivacyMetricName.POPULATION_UNIQUENESS.getValue()) && p.metricSeverity.equals(MetricSeverity.LOWER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric upperPopUniqueness = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals(PrivacyMetricName.POPULATION_UNIQUENESS.getValue()) && p.metricSeverity.equals(MetricSeverity.UPPER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric middlePopUniqueness = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals(PrivacyMetricName.POPULATION_UNIQUENESS.getValue()) && p.metricSeverity.equals(MetricSeverity.MIDDLE_VALUE))
                .findFirst().orElse(null);
        if (lowerPopUniqueness == null && upperPopUniqueness == null && middlePopUniqueness == null) {
            Log.warn("No privacy metrics found for policy " + policy.id + ". Skipping population uniqueness report evaluation.");
        } else {
            String viewName = policy.viewName != null ? policy.viewName : policy.materializedViewName;
            Instant start = Instant.now();
            PopulationUniquenessReport report;
            if (localQuasiIdentifiers == null) {
                report = computeMetricForTable(viewName, null);
                System.out.println("report: " + report.toString());
            } else {
                report = computeMetricForTable(viewName, localQuasiIdentifiers.getQuasiIdentifiers().getFirst());
            }
            Instant end = Instant.now();
            if (report == null) {
                Log.error("No population uniqueness report found for view " + viewName);
                return;
            }
            if (upperPopUniqueness != null && report.getEstimatedPopulationUniquenessRatio().doubleValue() > Double.parseDouble(upperPopUniqueness.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.SEVERE;
                newAlert.message = """
                            Policy with view %s has high population uniqueness of %.6f.
                            This policy is highly susceptible to re-identification attacks. Please take action.
                            Estimator used: %s
                            """.formatted(
                        report.getViewName(),
                        report.getEstimatedPopulationUniquenessRatio().doubleValue(),
                        report.getEstimatorUsed()
                );
                newAlert.persist();
                policy.alerts.add(newAlert);
            } else if (middlePopUniqueness != null && report.getEstimatedPopulationUniquenessRatio().doubleValue() > Double.parseDouble(middlePopUniqueness.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.WARNING;
                newAlert.message = """
                            Policy with view %s has a population uniqueness of %.6f.
                            Depending on the sensitivity of the data, this policy could be susceptible to a data re-identification attack.
                            Estimator used: %s
                            """.formatted(
                        report.getViewName(),
                        report.getEstimatedPopulationUniquenessRatio().doubleValue(),
                        report.getEstimatorUsed()
                );
                newAlert.persist();
                policy.alerts.add(newAlert);
            } else if (lowerPopUniqueness != null && report.getEstimatedPopulationUniquenessRatio().doubleValue() < Double.parseDouble(lowerPopUniqueness.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.INFO;
                newAlert.message = """
                            Policy with view %s has low population uniqueness of %.6f.
                            The data utility can potentially be improved with less generalization.
                            Estimator used: %s
                            """.formatted(
                        report.getViewName(),
                        report.getEstimatedPopulationUniquenessRatio().doubleValue(),
                        report.getEstimatorUsed()
                );
                newAlert.persist();
                policy.alerts.add(newAlert);
            }

            Policy.getEntityManager().merge(policy);
            Log.info("DONE Evaluating policy " + policy.id + " against population uniqueness report in: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");

        }
    }
}
