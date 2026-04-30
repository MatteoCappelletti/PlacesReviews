package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import placesreviews.app.persistence.entity.Place;
import placesreviews.app.service.PlaceService;

import java.util.List;

@Path("/home")
@DenyAll
public class HomeResource {

    private final Template homeTemplate;

    private final PlaceService placeService;

    public HomeResource(@Location("home.qute.html") Template homeTemplate, PlaceService placeService) {
        this.homeTemplate = homeTemplate;
        this.placeService = placeService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response showPage() {
        List<Place> places = placeService.findMostRecent(10);

        if (places.isEmpty()) {
            return Response.ok(homeTemplate.data("places", null).data("errorMessage", "No place found")).build();
        }
        return Response.ok(homeTemplate.data("places", places).data("errorMessage", null)).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response search(
            @QueryParam("name") String name,
            @QueryParam("city") String city
    ) {
        List<Place> places = placeService.findByNameContainsAndCity(name, city);

        if (places.isEmpty()) {
            return Response.ok(homeTemplate.data("places", null).data("errorMessage", "No place found")).build();
        }
        return Response.ok(homeTemplate.data("places", places).data("errorMessage", null)).build();
    }
}
