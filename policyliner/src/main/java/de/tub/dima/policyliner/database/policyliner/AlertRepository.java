package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AlertRepository implements PanacheRepository<Alert> {

    public Alert findById(String id) {
        return find("id", id).firstResult();
    }

    public List<Alert> findByAlertType(AlertType alertType) {
        return list("type", alertType);
    }

    public List<Alert> findByAlertSeverity(AlertSeverity alertSeverity) {
        return list("severity", alertSeverity);
    }

    public List<Alert> findAlertsBetween(LocalDateTime from, LocalDateTime to) {
        return list(" createdAt >= ?1 and createdAt <= ?2", from, to);
    }

    public PanacheQuery<Alert> findFilteredAlerts(String filter) {
        String filterString = "%" + filter + "%";
        return find(" message LIKE ?1 OR str(severity) LIKE ?1 OR str(type) LIKE ?1 ORDER BY isResolved, createdAt desc", filterString);
    }
}
