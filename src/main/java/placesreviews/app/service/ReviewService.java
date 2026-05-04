package placesreviews.app.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import placesreviews.app.persistence.entity.Place;
import placesreviews.app.persistence.entity.Review;
import placesreviews.app.persistence.entity.User;
import placesreviews.app.persistence.repository.PlaceRepository;
import placesreviews.app.persistence.repository.ReviewRepository;
import placesreviews.app.persistence.repository.UserRepository;
import placesreviews.app.service.model.Result;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReviewService {
    
    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final PlaceRepository placeRepository;
    
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, PlaceRepository placeRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
    }

    @Transactional
    public Result insert(int userId, int placeId, int rating, String description) {
        if (rating < 1 || rating > 5) {
            Result.error("Rating must be between 1 and 5");
        }
        if (description == null || description.isBlank()) {
            description = "";
        }

        Optional<User> optionalUser = userRepository.findByIdOptional(userId);
        if (optionalUser.isEmpty()) {
            return Result.error("User does not exists");
        }

        Optional<Place> optionalPlace = placeRepository.findByIdOptional(placeId);
        if (optionalPlace.isEmpty()) {
            return Result.error("Place does not exists");
        }

        Review review = new Review();
        review.setReviewer(optionalUser.get());
        review.setPlace(optionalPlace.get());
        review.setRating(rating);
        review.setDescription(description);
        review.setCreatedAt(OffsetDateTime.now());
        reviewRepository.persist(review);

        return Result.success();
    }

    public List<Review> findByPlaceId(int placeId) {
        return reviewRepository.findByPlaceId(placeId);
    }

    public List<Review> findByReviewerId(int reviewerId) {
        return reviewRepository.findByReviewerId(reviewerId);
    }

    public List<Review> findNotApproved() {
        return reviewRepository.findNotApproved();
    }

    @Transactional
    public Result deleteById(int reviewId) {
        Optional<Review> optionalReview = reviewRepository.findByIdOptional(reviewId);
        if (optionalReview.isEmpty()) {
            return Result.error("Review does not exists");
        }
        reviewRepository.delete(optionalReview.get());
        return Result.success();
    }

    @Transactional
    public Result approve(int reviewId, int approverUserId) {
        Optional<Review> optionalReview = reviewRepository.findByIdOptional(reviewId);
        if (optionalReview.isEmpty()) {
            return Result.error("Review not found");
        }

        Optional<User> optionalUser = userRepository.findByIdOptional(approverUserId);
        if (optionalUser.isEmpty()) {
            return Result.error("User not found");
        }

        User user = optionalUser.get();
        if (!"moderator".equals(user.getRole())) {
            return Result.error("Approver user must be moderator");
        }

        Review review = optionalReview.get();

        review.setApprover(user);

        return Result.success();
    }
}
