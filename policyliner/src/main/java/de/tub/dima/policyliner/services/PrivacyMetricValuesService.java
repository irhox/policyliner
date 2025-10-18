package de.tub.dima.policyliner.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.tub.dima.policyliner.database.policyliner.Policy;
import de.tub.dima.policyliner.database.policyliner.PolicyRepository;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.dto.PrivacyMetricDTO;
import de.tub.dima.policyliner.entities.DefaultPrivacyMetrics;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PrivacyMetricValuesService {
    private final PrivacyMetricRepository privacyMetricRepository;
    private final PolicyRepository policyRepository;

    @ConfigProperty(name = "policyLiner.default-metrics.file-path")
    String defaultMetricsPath;
    @Inject
    ObjectMapper objectMapper;

    public PrivacyMetricValuesService(
            PrivacyMetricRepository privacyMetricRepository,
            PolicyRepository policyRepository) {
        this.privacyMetricRepository = privacyMetricRepository;
        this.policyRepository = policyRepository;
    }

    public List<PrivacyMetricDTO> getPrivacyMetricValuesByPolicyId(String policyId){
        return privacyMetricRepository.findByPolicyId(policyId)
                .stream().map(this::convertPrivacyMetricToDTO).toList();
    }

    public PrivacyMetricDTO getPrivacyMetricValueById(String id){
        Optional<PrivacyMetric> metric = privacyMetricRepository.findById(id);
        if (metric.isEmpty()) throw new RuntimeException("No privacy metric with id " + id);
        return convertPrivacyMetricToDTO(metric.get());
    }

    @Transactional
    public PrivacyMetricDTO createPrivacyMetricValue(PrivacyMetricDTO privacyMetricDTO){
        PrivacyMetric newMetric = convertDTOToPrivacyMetric(privacyMetricDTO);
        PrivacyMetric.getEntityManager().merge(newMetric);
        return convertPrivacyMetricToDTO(newMetric);
    }

    public void storeAllDefaultPrivacyMetricValuesForPolicy(Policy policy){
        try(InputStream in = getClass().getResourceAsStream(defaultMetricsPath)) {
            DefaultPrivacyMetrics defaultMetrics = objectMapper.readValue(in, DefaultPrivacyMetrics.class);
            defaultMetrics.getDefaultMetrics().forEach(metric -> {
                metric.policy = policy;
                privacyMetricRepository.persist(metric);
            });
            Log.info("Stored default privacy metrics for policy " + policy.id);
        } catch (Exception e){
            throw new RuntimeException("Could not load default privacy metrics from file " + defaultMetricsPath);
        }
    }

    private PrivacyMetric convertDTOToPrivacyMetric(PrivacyMetricDTO privacyMetricDTO) {
        Optional<PrivacyMetric> newMetricOpt = privacyMetricRepository.findById(privacyMetricDTO.getId());
        PrivacyMetric metric = new PrivacyMetric();
        if (newMetricOpt.isPresent()) {
            metric = newMetricOpt.get();
        } else {
            metric.policy = policyRepository.findById(privacyMetricDTO.getPolicyId());
        }
        metric.name = privacyMetricDTO.getName();
        metric.description = privacyMetricDTO.getDescription();
        metric.value = privacyMetricDTO.getValue();
        metric.valueType = privacyMetricDTO.getValueType();
        metric.metricSeverity = privacyMetricDTO.getMetricSeverity();

        return metric;
    }

    private PrivacyMetricDTO convertPrivacyMetricToDTO(PrivacyMetric privacyMetric) {
        return new PrivacyMetricDTO(
                privacyMetric.id,
                privacyMetric.name,
                privacyMetric.description,
                privacyMetric.value,
                privacyMetric.valueType,
                privacyMetric.metricSeverity,
                privacyMetric.policy.id
        );
    }
}
