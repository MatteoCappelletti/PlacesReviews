package placesreviews.app.web;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/logout")
@DenyAll
public class LogoutResource {

    @GET
    @PermitAll
    public Response logout() {
        NewCookie removeCookie = new NewCookie.Builder("quarkus-credential")
                .path("/")
                .maxAge(0)
                .build();
        return Response.seeOther(URI.create("/login"))
                .cookie(removeCookie)
                .build();
    }
}
