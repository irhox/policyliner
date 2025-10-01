package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.QueryStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class DisclosureQueryRepository implements PanacheRepository<DisclosureQuery> {

    public DisclosureQuery findById(String id) {
        return find("id", id).firstResult();
    }

    public List<DisclosureQuery> findByUserId(String userId) {
        return list("user.id", userId);
    }

    public List<DisclosureQuery> findByStatus(QueryStatus status) {
        return list("status", status);
    }

    public List<DisclosureQuery> findQueriesBetween(LocalDateTime from, LocalDateTime to) {
        return list(" createdAt >= ?1 and createdAt <= ?2", from, to);
    }

    public List<DisclosureQuery> findNewQueriesByUserId(String userId) {
        return list("user.id = ?1 and inspectionStatus = 'NEW'", userId);
    }

    public PanacheQuery<DisclosureQuery> findFilteredQueries(String filter) {
        String filterString = "%" + filter + "%";
        return find("str(id) LIKE ?1 OR query LIKE ?1 OR message LIKE ?1 OR str(status) LIKE ?1 OR str(inspectionStatus) LIKE ?1 ORDER BY createdAt desc", filterString);
    }
}
