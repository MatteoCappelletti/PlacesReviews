package placesreviews.app.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.Category;

import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepository implements PanacheRepositoryBase<Category, Integer> {

    public Optional<Category> findByName(String name) {
        String query = "SELECT c FROM Category c WHERE c.name = :name";
        return find(query, Map.of(
                "name", name
        )).singleResultOptional();
    }

    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
}
