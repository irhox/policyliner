package de.tub.dima.policyliner.database.policyliner;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    public String role;

    @OneToMany(mappedBy = "user")
    public List<DisclosureQuery> queries;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
