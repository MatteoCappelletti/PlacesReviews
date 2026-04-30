package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import placesreviews.app.service.Result;
import placesreviews.app.service.UserService;

import java.net.URI;

@Path("/register")
@DenyAll
public class RegisterResource {

    private final Template registerTemplate;

    private final UserService userService;

    public RegisterResource(@Location("register.qute.html") Template registerTemplate, UserService userService) {
        this.registerTemplate = registerTemplate;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response showPage() {
        return Response.ok(registerTemplate.data("errorMessage", null)).build();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response doRegister(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("verified_password") String verifiedPassword
    ) {
        Result result = userService.insert(username, password, verifiedPassword);

        if (!result.ok()) {
            return Response.ok(registerTemplate.data("errorMessage", result.errorMessage())).build();
        }
        return Response.seeOther(URI.create("/login")).build();
    }
}
