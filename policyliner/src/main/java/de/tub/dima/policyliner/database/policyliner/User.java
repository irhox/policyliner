package de.tub.dima.policyliner.database.policyliner;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
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
