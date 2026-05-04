package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import placesreviews.app.persistence.entity.*;
import placesreviews.app.service.CategoryService;
import placesreviews.app.service.PlaceService;
import placesreviews.app.service.ReviewService;
import placesreviews.app.service.UserService;
import placesreviews.app.service.model.Result;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/place")
@DenyAll
public class PlaceResource {

    private final Template placeTemplate;

    private final Template addPlaceTemplate;

    private final Template modifyPlaceTemplate;

    private final PlaceService placeService;

    private final ReviewService reviewService;

    private final UserService userService;

    private final CategoryService categoryService;

    public PlaceResource(
            @Location("place.qute.html") Template placeTemplate,
            @Location("place.add.qute.html") Template addPlaceTemplate,
            @Location("place.modify.qute.html") Template modifyPlaceTemplate,
            PlaceService placeService,
            ReviewService reviewService,
            UserService userService,
            CategoryService categoryService
    ) {
        this.placeTemplate = placeTemplate;
        this.addPlaceTemplate = addPlaceTemplate;
        this.modifyPlaceTemplate = modifyPlaceTemplate;
        this.placeService = placeService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response showPage(
            @Context SecurityContext securityContext,
            @PathParam("id") int id,
            @QueryParam("reviewErrorMessage") String reviewErrorMessage
    ) {
        Optional<Place> optionalPlace = placeService.findById(id);

        User user = userService.getByUsername(securityContext.getUserPrincipal().getName());

        String role = null;
        if (user != null) {
            role = user.getRole();
        }

        if (optionalPlace.isEmpty()) {
            return Response.ok(placeTemplate
                    .data("place", null)
                    .data("reviews", null)
                    .data("errorMessage", "No place found")
                    .data("reviewErrorMessage", null)
                    .data("averageRating", null)
                    .data("reviewNumber", null)
                    .data("loggedUserRole", role)
            ).build();
        }

        List<Review> reviews = reviewService.findByPlaceId(id);

        List<Review> approvedReviews = reviewService.findByPlaceIdApproved(id);
        int averageRating = 0;
        for (Review review : approvedReviews) {
            averageRating += review.getRating();
        }
        averageRating /= approvedReviews.size();

        return Response.ok(placeTemplate
                .data("place", optionalPlace.get())
                .data("reviews", reviews)
                .data("errorMessage", null)
                .data("reviewErrorMessage", reviewErrorMessage)
                .data("averageRating", averageRating)
                .data("reviewNumber", approvedReviews.size())
                .data("loggedUserRole", role)
        ).build();
    }

    @GET
    @Path("/add")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("user")
    public Response showAddPage (
    ) {
        List<Category> categories = categoryService.findAll();

        return Response.ok(addPlaceTemplate
                .data("categories", categories)
                .data("errorMessage", null)
        ).build();
    }

    @POST
    @Path("/add")
    @RolesAllowed("user")
    public Response addPlace (
            @Context SecurityContext securityContext,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("city") String city,
            @FormParam("address") String address,
            @FormParam("latitude") double latitude,
            @FormParam("longitude") double longitude,
            @FormParam("categories") List<String> categories,
            @FormParam("image-url") String imageUrl
    ) {
        User user = userService.getByUsername(securityContext.getUserPrincipal().getName());

        Set<Category> categorySet = categoryService.findByNamesList(categories);

        List<String> mediaUrlList = new ArrayList<>();
        mediaUrlList.add(imageUrl);

        Result result = placeService.insert(
                user.getId(),
                name,
                description,
                city,
                address,
                latitude,
                longitude,
                categorySet,
                mediaUrlList
        );

        if (!result.ok()) {
            return Response.ok(addPlaceTemplate
                    .data("categories", categories)
                    .data("errorMessage", result.errorMessage())
            ).build();
        }

        return Response.seeOther(URI.create("/home")).build();
    }

    @GET
    @Path("/{id}/modify")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({"user", "moderator"})
    public Response showModifyPage (
            @Context SecurityContext securityContext,
            @PathParam("id") int id
    ) {
        Optional<Place> optionalPlace = placeService.findById(id);

        if (optionalPlace.isEmpty()) {
            return Response.ok(modifyPlaceTemplate
                    .data("place", null)
                    .data("categories", null)
                    .data("errorMessage", "No place found")
            ).build();
        }

        Place place = optionalPlace.get();

        User user = userService.getByUsername(securityContext.getUserPrincipal().getName());

        if (
                !"moderator".equals(user.getRole())
                && place.getUser().getId() != user.getId()
        ) {
            return Response.seeOther(URI.create("/home")).build();
        }

        List<Category> categories = categoryService.findAll();

        return Response.ok(modifyPlaceTemplate
                .data("place", place)
                .data("categories", categories)
                .data("errorMessage", null)
        ).build();
    }

    @POST
    @Path("/{id}/modify")
    @RolesAllowed({"user", "moderator"})
    public Response modifyPlace (
            @Context SecurityContext securityContext,
            @PathParam("id") int id,
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("city") String city,
            @FormParam("address") String address,
            @FormParam("latitude") double latitude,
            @FormParam("longitude") double longitude,
            @FormParam("categories") List<String> categories,
            @FormParam("image-url") String imageUrl
    ) {
        Optional<Place> optionalPlace = placeService.findById(id);

        if (optionalPlace.isEmpty()) {
            return Response.ok(modifyPlaceTemplate
                    .data("place", null)
                    .data("categories", null)
                    .data("errorMessage", "No place found")
            ).build();
        }

        Place place = optionalPlace.get();

        User user = userService.getByUsername(securityContext.getUserPrincipal().getName());

        if (
                !"moderator".equals(user.getRole())
                && place.getUser().getId() != user.getId()
        ) {
            return Response.seeOther(URI.create("/home")).build();
        }

        Set<Category> categorySet = categoryService.findByNamesList(categories);

        List<String> mediaUrlList = new ArrayList<>();
        mediaUrlList.add(imageUrl);

        Result result = placeService.modify(
                id,
                name,
                description,
                city,
                address,
                latitude,
                longitude,
                categorySet,
                mediaUrlList
        );

        if (!result.ok()) {
            List<Category> allCategories = categoryService.findAll();

            return Response.ok(modifyPlaceTemplate
                    .data("place", place)
                    .data("categories", allCategories)
                    .data("errorMessage", result.errorMessage())
            ).build();
        }

        return Response.seeOther(URI.create("/place/" + id)).build();
    }

    @POST
    @Path("/{id}")
    @RolesAllowed("user")
    public Response addReview(
            @Context SecurityContext securityContext,
            @PathParam("id") int id,
            @FormParam("rating-select") int rating,
            @FormParam("review-description") String description
    ) {
        User user = userService.getByUsername(securityContext.getUserPrincipal().getName());

        Result result = reviewService.insert(
                user.getId(),
                id,
                rating,
                description
        );

        if(!result.ok()) {
            return Response.seeOther(URI.create("/place/" + id + "?reviewErrorMessage=" + result.errorMessage().replace(" ", "-") )).build();
        }

        return Response.seeOther(URI.create("/place/" + id)).build();
    }

    @POST
    @Path("/{placeId}/review/{reviewId}/approve")
    @RolesAllowed("moderator")
    public Response approveReview (
            @Context SecurityContext securityContext,
            @PathParam("reviewId") int reviewId,
            @PathParam("placeId") int placeId
    ) {
        User user = userService.getByUsername(securityContext.getUserPrincipal().getName());

        Result result = reviewService.approve(reviewId, user.getId());

        return Response.seeOther(URI.create("place/" + placeId)).build();
    }

    @POST
    @Path("/{placeId}/review/{reviewId}/delete")
    @RolesAllowed("moderator")
    public Response deleteReview (
            @PathParam("reviewId") int reviewId,
            @PathParam("placeId") int placeId
    ) {
        Result result = reviewService.deleteById(reviewId);

        return Response.seeOther(URI.create("place/" + placeId)).build();
    }
}
