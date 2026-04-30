package placesreviews.app.service;

import jakarta.enterprise.context.ApplicationScoped;
import placesreviews.app.persistence.entity.Media;
import placesreviews.app.persistence.entity.Place;
import placesreviews.app.persistence.repository.MediaRepository;
import placesreviews.app.persistence.repository.PlaceRepository;

import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class MediaService {

    private final MediaRepository mediaRepository;

    private final PlaceRepository placeRepository;

    public MediaService(MediaRepository mediaRepository, PlaceRepository placeRepository) {
        this.mediaRepository = mediaRepository;
        this.placeRepository = placeRepository;
    }

    public Result insert(int placeId, String url) {
        if (url == null || url.isBlank()) {
            return Result.error("Url is required");
        }

        Place place = placeRepository.findById(placeId);

        Media media = new Media();
        media.setPlace(place);
        media.setPath(url);
        media.setCreatedAt(OffsetDateTime.now());
        mediaRepository.persist(media);

        return Result.success();
    }

    public List<Media> findByPlaceId(int placeId) {
        return mediaRepository.findByPlaceId(placeId);
    }
}
