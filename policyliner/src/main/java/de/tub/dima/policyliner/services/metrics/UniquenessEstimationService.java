package de.tub.dima.policyliner.services.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import de.tub.dima.policyliner.constants.MetricSeverity;
import de.tub.dima.policyliner.database.data.metrics.UniquenessEstimationCalculator;
import de.tub.dima.policyliner.database.policyliner.Alert;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import de.tub.dima.policyliner.entities.SampleUniquenessReport;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UniquenessEstimationService implements PrivacyMetricService<SampleUniquenessReport> {

    @Inject
    ObjectMapper objectMapper;

    private final PrivacyMetricRepository privacyMetricRepository;
    private final UniquenessEstimationCalculator calculator;

    private JsonQuasiIdentifiers quasiIdentifiers;


    public UniquenessEstimationService(
            UniquenessEstimationCalculator calculator,
            PrivacyMetricRepository privacyMetricRepository) {
        this.calculator = calculator;
        this.privacyMetricRepository = privacyMetricRepository;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public SampleUniquenessReport computeMetricForTable(String tableName, JsonQuasiIdentifier localQuasiIdentifier) {
        if (quasiIdentifiers == null && localQuasiIdentifier == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        } else if (localQuasiIdentifier != null) {
            quasiIdentifiers = new JsonQuasiIdentifiers(List.of(localQuasiIdentifier));
        }
        Optional<JsonQuasiIdentifier> identifier = quasiIdentifiers.getQuasiIdentifiers().stream().filter(q -> q.getViewName().equals(tableName)).findFirst();
        if (identifier.isPresent()) {
            return calculator.getSampleUniquenessReportOfTable(tableName, identifier.get().getColumns());
        } else {
            Log.error("No quasi-identifier object found for view " + tableName);
            return null;
        }
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public List<SampleUniquenessReport> computeMetricForTables(List<String> tableNames, JsonQuasiIdentifiers localQuasiIdentifiers) {
        if (quasiIdentifiers == null && localQuasiIdentifiers == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        } else if (localQuasiIdentifiers != null) {
            quasiIdentifiers = localQuasiIdentifiers;
        }
        List<SampleUniquenessReport> reports = new ArrayList<>();

        for (JsonQuasiIdentifier identifier : quasiIdentifiers.getQuasiIdentifiers()) {
            if (tableNames.contains(identifier.getViewName())) {
                try {
                    SampleUniquenessReport report = calculator.getSampleUniquenessReportOfTable(
                            identifier.getViewName(),
                            identifier.getColumns()
                    );
                    report.setViewName(identifier.getViewName());
                    reports.add(report);
                } catch (Exception e) {
                    Log.error("Failed to get sample uniqueness report for view: " + identifier.getViewName(), e);
                }
            }
        }
        return reports;
    }

    @Override
    public void evaluatePolicyAgainstMetric(Policy policy, JsonQuasiIdentifiers localQuasiIdentifiers) {
        Log.info("Evaluating policy " + policy.id + " against uniqueness report");
        List<PrivacyMetric> policyPrivacyMetrics = privacyMetricRepository.findByPolicyId(policy.getId());
        PrivacyMetric lowerUniquenessRatio = policyPrivacyMetrics.stream().
                filter(p -> p.name.equals("uniquenessRatio") && p.metricSeverity.equals(MetricSeverity.LOWER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric upperUniquenessRatio = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals("uniquenessRatio") && p.metricSeverity.equals(MetricSeverity.UPPER_LIMIT))
                .findFirst().orElse(null);
        PrivacyMetric middleUniquenessRatio = policyPrivacyMetrics.stream()
                .filter(p -> p.name.equals("uniquenessRatio") && p.metricSeverity.equals(MetricSeverity.MIDDLE_VALUE))
                .findFirst().orElse(null);
        if (lowerUniquenessRatio == null && upperUniquenessRatio == null && middleUniquenessRatio == null) {
            Log.warn("No privacy metrics found for policy " + policy.id + ". Skipping uniqueness report evaluation.");
        } else {
            String viewName = policy.viewName != null ? policy.viewName : policy.materializedViewName;
            Instant start = Instant.now();
            SampleUniquenessReport report;
            if (localQuasiIdentifiers == null) {
                report = computeMetricForTable(viewName, null);
            } else {
                report = computeMetricForTable(viewName, localQuasiIdentifiers.getQuasiIdentifiers().getFirst());
            }
            Instant end = Instant.now();

            if (upperUniquenessRatio != null && report != null && report.getUniquenessRatio().doubleValue() > Double.parseDouble(upperUniquenessRatio.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.SEVERE;
                newAlert.message = """ 
                        Policy with view %s has a high uniqueness ratio of %.3f.
                        This is a cause for concern and should be reviewed. The view columns should most likely be generalized.
                        """.formatted(viewName, report.getUniquenessRatio());
                newAlert.persist();
                policy.alerts.add(newAlert);
            } else if (middleUniquenessRatio != null && report != null && report.getUniquenessRatio().doubleValue() > Double.parseDouble(middleUniquenessRatio.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.WARNING;
                newAlert.message = """ 
                        Policy with view %s has a high uniqueness ratio of %.3f.
                        Depending on the sensitivity of the data, this may be a cause for concern and should be reviewed.
                        """.formatted(viewName, report.getUniquenessRatio());
                newAlert.persist();
                policy.alerts.add(newAlert);
            } else if (lowerUniquenessRatio != null && report != null && report.getUniquenessRatio().doubleValue() < Double.parseDouble(lowerUniquenessRatio.value)) {
                Alert newAlert = new Alert();
                newAlert.type = AlertType.POLICY;
                newAlert.severity = AlertSeverity.INFO;
                newAlert.message = """ 
                        Policy with view %s has a low uniqueness ratio of %.3f.
                        Please review the generalization / masking of the data in this view.
                        The usability of the data can most likely be improved.
                """.formatted(viewName, report.getUniquenessRatio());
                newAlert.persist();
                policy.alerts.add(newAlert);
            }
            Policy.getEntityManager().merge(policy);
            Log.info("DONE Evaluating policy " + policy.id + " against uniqueness report in: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
        }
    }

}

