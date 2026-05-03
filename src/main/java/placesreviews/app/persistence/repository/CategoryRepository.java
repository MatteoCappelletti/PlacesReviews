package placesreviews.app.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.Category;

import java.util.*;

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

    public Set<Category> findByNamesList(List<String> names) {
        String query = "SELECT c FROM Category c WHERE c.name in :names";
        return new HashSet<>(find(query, Map.of(
                "names", names
        )).list());
    }
}
