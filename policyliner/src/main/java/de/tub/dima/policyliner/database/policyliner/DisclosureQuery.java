package de.tub.dima.policyliner.database.policyliner;

import de.tub.dima.policyliner.constants.QueryInspectionStatus;
import de.tub.dima.policyliner.constants.QueryStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disclosure_query")
public class DisclosureQuery extends PanacheEntityBase {

    public DisclosureQuery(){}

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;
    @Column(name="query", columnDefinition="TEXT")
    public String query;
    @ManyToOne(cascade = CascadeType.MERGE)
    public User user;
    @CreationTimestamp
    public LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    public QueryStatus status;
    @Enumerated(EnumType.STRING)
    public QueryInspectionStatus inspectionStatus = QueryInspectionStatus.NEW;
    public String message;
    @ManyToMany
    @JoinTable(
            name = "query_alert",
            joinColumns = @JoinColumn(name = "query_id"),
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
