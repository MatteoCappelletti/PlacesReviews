package placesreviews.app.web;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/login")
@DenyAll
public class LoginResource {

    private final Template loginTemplate;

    public LoginResource(@Location("login.qute.html") Template loginTemplate) {
        this.loginTemplate = loginTemplate;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public TemplateInstance showPage(@QueryParam("errorMessage") String errorMessage) {
        return loginTemplate.data("errorMessage", errorMessage);
    }
}
