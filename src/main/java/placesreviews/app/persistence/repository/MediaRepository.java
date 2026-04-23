package placesreviews.app.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.Media;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MediaRepository implements PanacheRepositoryBase<Media, Integer> {

    public List<Media> findByPlaceId(int placeId) {
        String query = "SELECT m FROM Media m WHERE m.place.id = :placeId";
        return find(query, Map.of(
                "placeId", placeId
        )).list();
    }
}
