package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import placesreviews.app.persistence.entity.User;
import placesreviews.app.service.UserService;
import placesreviews.app.service.model.Result;

import java.net.URI;
import java.util.List;

@Path("/users")
@DenyAll
public class UsersResource {

    private final Template usersTemplate;

    private final UserService userService;

    public UsersResource(@Location("users.qute.html") Template usersTemplate, UserService userService) {
        this.usersTemplate = usersTemplate;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed("moderator")
    public Response showPage (
            @Context SecurityContext securityContext
    ) {
        List<User> users = userService.findAll();

        return Response.ok(usersTemplate
                .data("users", users)
                .data("loggedUsername", securityContext.getUserPrincipal().getName())
        ).build();
    }

    @GET
    @RolesAllowed("moderator")
    @Path("/modify/{username}")
    public Response setRoleToModerator (
            @Context SecurityContext securityContext,
            @PathParam("username") String username
    ) {
        User user = userService.getByUsername(username);

        User loggedUser = userService.getByUsername(securityContext.getUserPrincipal().getName());

        if (user.getId() != loggedUser.getId()) {
            Result result = userService.setRoleToModeratorFromId(user.getId());
        }

        return Response.seeOther(URI.create("/users")).build();
    }
}
