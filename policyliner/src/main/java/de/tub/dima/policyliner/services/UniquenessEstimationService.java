package de.tub.dima.policyliner.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.database.data.DataDBService;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifier;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import de.tub.dima.policyliner.entities.SampleUniquenessReport;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UniquenessEstimationService implements PrivacyMetricService<SampleUniquenessReport> {

    @Inject
    ObjectMapper objectMapper;

    private final DataDBService dataDBService;

    private JsonQuasiIdentifiers quasiIdentifiers;


    public UniquenessEstimationService(DataDBService dataDBService) {
        this.dataDBService = dataDBService;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public SampleUniquenessReport computeMetricForTable(String tableName) {
        if (quasiIdentifiers == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        }
        Optional<JsonQuasiIdentifier> identifier = quasiIdentifiers.getQuasiIdentifiers().stream().filter(q -> q.getViewName().equals(tableName)).findFirst();
        if (identifier.isPresent()) {
            return dataDBService.getSampleUniquenessReportOfTable(tableName, identifier.get().getColumns());
        } else {
            throw new RuntimeException("No quasi-identifier object found for view " + tableName);
        }
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public List<SampleUniquenessReport> computeMetricForTables(List<String> tableNames) {
        if (quasiIdentifiers == null) {
            quasiIdentifiers = initializeQuasiIdentifiers(objectMapper);
        }
        List<SampleUniquenessReport> reports = new ArrayList<>();

        for (JsonQuasiIdentifier identifier : quasiIdentifiers.getQuasiIdentifiers()) {
            if (tableNames.contains(identifier.getViewName())) {
                try {
                    SampleUniquenessReport report = dataDBService.getSampleUniquenessReportOfTable(
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

}

