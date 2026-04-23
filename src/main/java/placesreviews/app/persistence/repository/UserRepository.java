package placesreviews.app.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.User;

import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Integer> {

    private PanacheQuery<User> getOrFindByUsername(String username) {
        String query = "SELECT u from User u WHERE u.username = :username";
        return find(query, Map.of(
                "username", username
        ));
    }

    public User getByUsername(String username) {
        return getOrFindByUsername(username).singleResult();
    }

    public Optional<User> findByUsername(String username) {
        return getOrFindByUsername(username).firstResultOptional();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }
}
