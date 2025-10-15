package de.tub.dima.policyliner.database.policyliner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PrivacyMetricRepository implements PanacheRepository<PrivacyMetric> {
    public PrivacyMetric findById(String id){
        return find("id", id).firstResult();
    }

    public List<PrivacyMetric> findByPolicyId(String policyId){
        return list("policy.id", policyId);
    }

}
