package app.resource;

import app.data.Token;
import app.data.User;
import app.exception.UnauthorizedException;
import app.service.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@PermitAll
@Path("auth")
public class AuthResource {

    private static final Logger log = Logger.getGlobal();

    private TokenService authTokenFactory;
    private UserService userService;

    public AuthResource() {
        this.userService = new DummyUserService();
        this.authTokenFactory = new DefaultTokenService();
    }

    public AuthResource(TokenService authTokenFactory, UserService userService) {
        this.authTokenFactory = authTokenFactory;
        this.userService = userService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String userName,
                                     @FormParam("password") String password) {
        try {
            User user = userService.authenticate(userName, password);
            Token token = authTokenFactory.forUser(user);

            return Response.ok(token).build();
        } catch (UnauthorizedException exception) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }
}
