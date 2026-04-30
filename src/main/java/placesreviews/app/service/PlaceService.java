package placesreviews.app.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import placesreviews.app.persistence.entity.Category;
import placesreviews.app.persistence.entity.Place;
import placesreviews.app.persistence.entity.User;
import placesreviews.app.persistence.repository.PlaceRepository;
import placesreviews.app.persistence.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.*;

@ApplicationScoped
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final UserRepository userRepository;

    public PlaceService(PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Result insert(int userId, String name, String description, String city, String address, double latitude, double longitude, Set<Category> categories) {
        if (name == null || name.isBlank()) {
            return Result.error("Name is required");
        }
        if (description == null || description.isBlank()) {
            return Result.error("Description is required");
        }
        if (city == null || city.isBlank()) {
            return Result.error("City is required");
        }
        if (address == null || address.isBlank()) {
            return Result.error("Address is required");
        }
        if (categories == null) {
            categories = new HashSet<>();
        }

        Optional<User> optionalUser = userRepository.findByIdOptional(userId);
        if (optionalUser.isEmpty()) {
            return Result.error("User does not exists");
        }

        Place place = new Place();
        place.setUser(optionalUser.get());
        place.setName(name);
        place.setDescription(description);
        place.setCity(city);
        place.setAddress(address);
        place.setLatitude(latitude);
        place.setLongitude(longitude);
        place.setCategories(categories);
        place.setCreatedAt(OffsetDateTime.now());
        placeRepository.persist(place);

        return Result.success();
    }

    public List<Place> findByCity(String city) {
        if (city == null || city.isBlank()) {
            return new ArrayList<>();
        }

        return placeRepository.findByCity(city);
    }

    public List<Place> findByUserId(int userId) {
        return placeRepository.findByUserId(userId);
    }

    public List<Place> findByNameContains(String name) {
        if (name == null) {
            return new ArrayList<>();
        }
        if (name.isBlank()) {
            return placeRepository.listAll();
        }

        return placeRepository.findByNameContains(name);
    }
}
