package de.tub.dima.policyliner.services;

import de.tub.dima.policyliner.database.policyliner.PolicyRepository;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetric;
import de.tub.dima.policyliner.database.policyliner.PrivacyMetricRepository;
import de.tub.dima.policyliner.dto.PrivacyMetricDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

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
        return convertPrivacyMetricToDTO(privacyMetricRepository.findById(id));
    }

    @Transactional
    public PrivacyMetricDTO createPrivacyMetricValue(PrivacyMetricDTO privacyMetricDTO){
        PrivacyMetric newMetric = convertDTOToPrivacyMetric(privacyMetricDTO);
        PrivacyMetric.getEntityManager().merge(newMetric);
        return convertPrivacyMetricToDTO(newMetric);
    }

    private PrivacyMetric convertDTOToPrivacyMetric(PrivacyMetricDTO privacyMetricDTO) {
        PrivacyMetric newMetric = new PrivacyMetric();
        newMetric.id = privacyMetricDTO.getId();
        newMetric.name = privacyMetricDTO.getName();
        newMetric.description = privacyMetricDTO.getDescription();
        newMetric.value = privacyMetricDTO.getValue();
        newMetric.valueType = privacyMetricDTO.getValueType();
        newMetric.metricSeverity = privacyMetricDTO.getMetricSeverity();
        newMetric.policy = policyRepository.findById(privacyMetricDTO.getPolicyId());
        return newMetric;
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
