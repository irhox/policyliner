package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.PolicyStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disclosure_policy")
public class Policy extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;
    @Column(name="policy", columnDefinition="TEXT")
    public String policy;
    public String viewName;
    public String materializedViewName;
    @CreationTimestamp
    public LocalDateTime createdAt;
    public LocalDateTime deactivatedAt;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    public PolicyStatus status;
    public String allowedUserRole;
    @ManyToMany
    @JoinTable(
            name = "policy_alert",
            joinColumns = @JoinColumn(name = "policy_id"),
            inverseJoinColumns = @JoinColumn(name = "alert_id")
    )
    public List<Alert> alerts;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
