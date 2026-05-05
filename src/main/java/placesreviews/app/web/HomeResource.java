package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import placesreviews.app.persistence.entity.Place;
import placesreviews.app.persistence.entity.User;
import placesreviews.app.service.PlaceService;
import placesreviews.app.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Path("/home")
@DenyAll
public class HomeResource {

    private final Template homeTemplate;

    private final PlaceService placeService;

    private final UserService userService;

    public HomeResource(@Location("home.qute.html") Template homeTemplate, PlaceService placeService, UserService userService) {
        this.homeTemplate = homeTemplate;
        this.placeService = placeService;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response showPage(
            @Context SecurityContext securityContext
    ) {
        List<Place> places = placeService.findMostRecent(10);

        String role = null;
        if (securityContext.getUserPrincipal() != null) {
            User user = userService.getByUsername(securityContext.getUserPrincipal().getName());
            role = user.getRole();
        }

        if (places.isEmpty()) {
            return Response.ok(homeTemplate
                    .data("places", null)
                    .data("errorMessage", "No place found")
                    .data("role", role)
            ).build();
        }
        return Response.ok(homeTemplate
                .data("places", places)
                .data("errorMessage", null)
                .data("role", role)
        ).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/search")
    @PermitAll
    public Response search(
            @Context SecurityContext securityContext,
            @QueryParam("name") String name,
            @QueryParam("city") String city
    ) {
        List<Place> places = placeService.findByNameContainsAndCity(name, city);

        String role = null;
        if (securityContext.getUserPrincipal() != null) {
            User user = userService.getByUsername(securityContext.getUserPrincipal().getName());
            role = user.getRole();
        }

        if (places.isEmpty()) {
            return Response.ok(homeTemplate
                    .data("places", null)
                    .data("errorMessage", "No place found")
                    .data("role", role)
            ).build();
        }
        return Response.ok(homeTemplate
                .data("places", places)
                .data("errorMessage", null)
                .data("role", role)
        ).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/coordinates")
    @PermitAll
    public Response searchByCoordinates(
            @Context SecurityContext securityContext,
            @QueryParam("latitudesearch") double latitude ,
            @QueryParam("longitudesearch") double longitude
    ) {
        List<Place> placesInArea = placeService.findInTwoKm(latitude, longitude);

        String role = null;
        if (securityContext.getUserPrincipal() != null) {
            User user = userService.getByUsername(securityContext.getUserPrincipal().getName());
            role = user.getRole();
        }

        if (placesInArea.isEmpty()) {
            return Response.ok(homeTemplate
                    .data("places", null)
                    .data("errorMessage", "No place found in your area (2 Km)")
                    .data("role", role)
            ).build();
        }
        return Response.ok(homeTemplate
                .data("places", placesInArea)
                .data("errorMessage", null)
                .data("role", role)
        ).build();
    }
}
