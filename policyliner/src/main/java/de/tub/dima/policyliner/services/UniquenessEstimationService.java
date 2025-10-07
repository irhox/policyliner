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
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UniquenessEstimationService {

    @ConfigProperty(name = "data.quasi-identifiers.file-path")
    String quasiIdentifiersFilePath;

    @Inject
    ObjectMapper objectMapper;

    private final DataDBService dataDBService;

    private JsonQuasiIdentifiers quasiIdentifiers;


    public UniquenessEstimationService(DataDBService dataDBService) {
        this.dataDBService = dataDBService;
    }

    private void getQuasiIdentifiersFromJsonFile() {
        try (InputStream is = getClass().getResourceAsStream(quasiIdentifiersFilePath)) {
            quasiIdentifiers = objectMapper.readValue(is, JsonQuasiIdentifiers.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON quasi-identifiers file", e);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public SampleUniquenessReport getSampleUniquenessOfTable(String viewName) {
        if (quasiIdentifiers == null) {
            getQuasiIdentifiersFromJsonFile();
        }
        Optional<JsonQuasiIdentifier> identifier = quasiIdentifiers.getQuasiIdentifiers().stream().filter(q -> q.getViewName().equals(viewName)).findFirst();
        if (identifier.isPresent()) {
            return dataDBService.getSampleUniquenessReportOfTable(viewName, identifier.get().getColumns());
        } else {
            throw new RuntimeException("No quasi-identifier object found for view " + viewName);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<SampleUniquenessReport> getSampleUniquenessOfTables(List<String> viewNames) {
        if (quasiIdentifiers == null) {
            getQuasiIdentifiersFromJsonFile();
        }
        List<SampleUniquenessReport> reports = new ArrayList<>();

        for (JsonQuasiIdentifier identifier : quasiIdentifiers.getQuasiIdentifiers()) {
            if (viewNames.contains(identifier.getViewName())) {
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

