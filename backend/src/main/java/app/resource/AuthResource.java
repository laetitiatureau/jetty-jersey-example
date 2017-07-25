package app.resource;

import app.data.Token;
import app.data.User;
import app.service.DummyUserService;
import app.service.UserService;
import app.service.TokenService;
import app.service.DummyTokenService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
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
        this.authTokenFactory = new DummyTokenService();
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

        User user = userService.authenticate(userName, password);

        Token token = authTokenFactory.createToken(user.getName(),
                user.getRoles(), user.getVersion());

        String header = "Bearer " + token.getAuthToken();

        return Response.ok().header(HttpHeaders.AUTHORIZATION, header).build();
    }
}
