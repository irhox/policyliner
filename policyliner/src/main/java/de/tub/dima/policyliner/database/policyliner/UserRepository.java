package de.tub.dima.policyliner.database.policyliner;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findById(String id){
        return find("id", id).firstResult();
    }
}
