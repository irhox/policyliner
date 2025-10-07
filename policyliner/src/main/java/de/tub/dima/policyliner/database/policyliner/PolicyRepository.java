package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.PolicyStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@ApplicationScoped
public class PolicyRepository implements PanacheRepository<Policy> {

    public Policy findById(String id) {
        return find("id", id).firstResult();
    }

    public List<Policy> findByViewName(String viewName) {
        return list("viewName", viewName);
    }

    public List<Policy> findByStatus(PolicyStatus status) {
        return list("status", status);
    }

    public List<Policy> findPoliciesBetween(LocalDateTime from, LocalDateTime to) {
        return list(" createdAt >= ?1 AND createdAt <= ?2", from, to);
    }

    public List<Policy> findByNames(Collection<String> policyNameList) {
        return list("materializedViewName in ?1 OR viewName in ?1", policyNameList);
    }

    public PanacheQuery<Policy> findFilteredPolicies(String filter) {
        String filterString = "%" + filter + "%";
        return find("id LIKE ?1 OR policy LIKE ?1 OR viewName LIKE ?1 OR materializedViewName LIKE ?1 OR str(status) LIKE ?1 OR allowedUserRole LIKE ?1 ORDER BY status, createdAt desc", filterString);
    }

    public PanacheQuery<Policy> findFilteredPoliciesByStatus(String filter, String status) {
        String filterString = "%" + filter + "%";
        return find("status = ?2 AND (id LIKE ?1 OR policy LIKE ?1 OR viewName LIKE ?1 OR materializedViewName LIKE ?1 OR str(status) LIKE ?1 OR allowedUserRole LIKE ?1) ORDER BY status, createdAt desc", filterString, status);
    }

}
