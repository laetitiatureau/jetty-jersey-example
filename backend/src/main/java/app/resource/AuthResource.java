package app.resource;

import app.data.Token;
import app.data.User;
import app.exception.UnauthorizedException;
import app.service.JwtTokenService;
import app.service.DummyUserService;
import app.service.TokenService;
import app.service.UserService;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@PermitAll
@Path("auth")
public class AuthResource {

    private final TokenService authTokenFactory;
    private final UserService userService;

    public AuthResource(@Context Configuration config) {
        this.authTokenFactory = new JwtTokenService(config);
        this.userService = new DummyUserService();
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
        } catch (UnauthorizedException e) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } catch (RuntimeException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
