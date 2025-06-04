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
    public String policy;
    @CreationTimestamp
    public LocalDateTime createdAt;
    public LocalDateTime deactivatedAt;
    public PolicyStatus status;
    @OneToMany(mappedBy = "policy")
    public List<Alert> alerts;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
