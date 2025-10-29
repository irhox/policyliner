package de.tub.dima.policyliner.services.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.entities.JsonQuasiIdentifiers;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.io.InputStream;
import java.util.List;

public interface PrivacyMetricService<T> {

    T computeMetricForTable(String tableName);

    List<T> computeMetricForTables(List<String> tableNames);

    void evaluatePolicyAgainstMetric(Policy policy);

    default JsonQuasiIdentifiers initializeQuasiIdentifiers(ObjectMapper objectMapper) {
        Config config = ConfigProvider.getConfig();
        String quasiIdentifiersFilePath = config.getOptionalValue("policyLiner.quasi-identifiers.file-path", String.class)
                .orElseThrow(() -> new RuntimeException("Missing configuration value for data.quasi-identifiers.file-path"));
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(quasiIdentifiersFilePath)) {
            return objectMapper.readValue(is, JsonQuasiIdentifiers.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON quasi-identifiers file", e);
        }
    }
}
