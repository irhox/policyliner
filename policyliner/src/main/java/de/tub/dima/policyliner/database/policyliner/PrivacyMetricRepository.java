package de.tub.dima.policyliner.database.policyliner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PrivacyMetricRepository implements PanacheRepository<PrivacyMetric> {
    public Optional<PrivacyMetric> findById(String id){
        return find("id", id).firstResultOptional();
    }

    public List<PrivacyMetric> findByPolicyId(String policyId){
        return list("policy.id", policyId);
    }

}
