package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.database.policyliner.PolicyRepository;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.dto.PrivacyMetricDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PrivacyMetricValuesService {
    private final PrivacyMetricRepository privacyMetricRepository;
    private final PolicyRepository policyRepository;

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
