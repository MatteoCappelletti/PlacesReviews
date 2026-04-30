package placesreviews.app.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.Place;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PlaceRepository implements PanacheRepositoryBase<Place, Integer> {

    public List<Place> findByCity(String city) {
        String query = "SELECT p FROM Place p WHERE p.city = :city";
        return find(query, Map.of(
                "city", city
        )).list();
    }

    public List<Place> findByUserId(int userId) {
        String query = "SELECT p FROM Place p WHERE p.user.id = :userId";
        return find(query, Map.of(
                "userId", userId
        )).list();
    }

    public List<Place> findByNameContains(String contained) {
        String query = "SELECT p FROM Place p WHERE p.name ilike :name";
        return find(query, Map.of(
                "name", ("%" + contained + "%")
        )).list();
    }

    public List<Place> findMostRecent(int quantity) {
        String query = "SELECT p FROM Place p ORDER BY p.createdAt DESC LIMIT :quantity";
        return find(query, Map.of(
                "quantity", quantity
        )).list();
    }

    public List<Place> findByNameContainsAndCity(String name, String city) {
        String paramName = (name == null || name.isBlank()) ? "" : ("%" + name + "%");
        String paramCity = (city == null || city.isBlank()) ? "" : ("%" + city + "%");

        String query = "SELECT p FROM Place p WHERE p.name ilike :name or p.city ilike :city";
        return find(query, Map.of(
                "name", paramName,
                "city", paramCity
        )).list();
    }
}
