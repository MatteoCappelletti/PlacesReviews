package placesreviews.app.persistence.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import placesreviews.app.persistence.entity.Review;

import java.util.List;
import java.util.Map;

public class ReviewRepository implements PanacheRepositoryBase<Review, Integer> {

    public List<Review> findByPlaceId(int placeId) {
        String query = "SELECT r FROM Review r WHERE r.place.id = :placeId";
        return find(query, Map.of(
                "placeId", placeId
        )).list();
    }

    public List<Review> findByReviewerId(int reviewerId) {
        String query = "SELECT r FROM Review r WHERE r.reviewer.id = :reviewerId";
        return find(query, Map.of(
                "reviewerId", reviewerId
        )).list();
    }

    public List<Review> findNotApproved() {
        String query = "SELECT r FROM Review r WHERE r.approver is null";
        return find(query).list();
    }
}
