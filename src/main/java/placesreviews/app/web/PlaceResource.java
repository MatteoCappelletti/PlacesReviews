package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import placesreviews.app.persistence.entity.Place;
import placesreviews.app.persistence.entity.Review;
import placesreviews.app.service.PlaceService;
import placesreviews.app.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Path("/place")
@DenyAll
public class PlaceResource {

    private final Template placeTemplate;

    private final Template addPlaceTemplate;

    private final Template modifyPlaceTemplate;

    private final PlaceService placeService;

    private final ReviewService reviewService;

    public PlaceResource(
            @Location("place.qute.html") Template placeTemplate,
            @Location("place.add.qute.html") Template addPlaceTemplate,
            @Location("place.modify.qute.html") Template modifyPlaceTemplate,
            PlaceService placeService,
            ReviewService reviewService
    ) {
        this.placeTemplate = placeTemplate;
        this.addPlaceTemplate = addPlaceTemplate;
        this.modifyPlaceTemplate = modifyPlaceTemplate;
        this.placeService = placeService;
        this.reviewService = reviewService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response showPage(
            @PathParam("id") int id
    ) {
        Optional<Place> optionalPlace = placeService.findById(id);

        if (optionalPlace.isEmpty()) {
            return Response.ok(placeTemplate
                    .data("place", null)
                    .data("reviews", null)
                    .data("errorMessage", "No place found")
            ).build();
        }

        List<Review> reviews = reviewService.findByPlaceId(id);

        return Response.ok(placeTemplate
                .data("place", optionalPlace.get())
                .data("reviews", reviews)
                .data("errorMessage", null)
        ).build();
    }
}
