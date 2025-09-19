package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.AlertSeverity;
import de.tub.dima.policyliner.constants.AlertType;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "alert")
public class Alert extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;
    @Column(name="message", columnDefinition="TEXT")
    public String message;
    @Enumerated(EnumType.STRING)
    public AlertSeverity severity;
    @Enumerated(EnumType.STRING)
    public AlertType type;
    public Boolean isResolved = false;
    public LocalDateTime resolvedAt;
    @Column(name = "createdat", nullable = false, updatable = false)
    @CreationTimestamp
    public LocalDateTime createdAt;
    @ManyToMany(mappedBy = "alerts")
    public List<Policy> policies;
    @ManyToMany(mappedBy = "alerts")
    public List<DisclosureQuery> queries;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
