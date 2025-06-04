package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
public class Alert extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;
    public String message;
    public AlertSeverity severity;
    public AlertType type;
    public Boolean isResolved;
    public LocalDateTime resolvedAt;
    @CreationTimestamp
    public LocalDateTime createdAt;
    @ManyToOne
    public Policy policy;
    @ManyToOne
    public DisclosureQuery query;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
