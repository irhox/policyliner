package de.tub.dima.policyliner.database.policyliner;

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
    public String query;
    public String userId;
    @CreationTimestamp
    public LocalDateTime createdAt;
    public QueryStatus status;
    public String message;
    @OneToMany(mappedBy = "query")
    public List<Alert> alerts;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
