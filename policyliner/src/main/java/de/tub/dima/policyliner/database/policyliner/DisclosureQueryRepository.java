package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.QueryStatus;
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
        return list("userId", userId);
    }

    public List<DisclosureQuery> findByStatus(QueryStatus status) {
        return list("status", status);
    }

    public List<DisclosureQuery> findQueriesBetween(LocalDateTime from, LocalDateTime to) {
        return list(" createdAt >= ?1 and createdAt <= ?2", from, to);
    }
}
