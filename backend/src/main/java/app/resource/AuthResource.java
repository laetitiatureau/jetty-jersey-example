package app.resource;

import app.Config;
import app.data.Token;
import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;
import app.service.FileUserService;
import app.service.JwtTokenService;
import app.service.TokenService;
import app.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@Singleton
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
public class AuthResource {

    private final TokenService authTokenFactory;
    private final UserService userService;

    public AuthResource(@Context Configuration config) {
        this.authTokenFactory = new JwtTokenService(config);
        this.userService = new FileUserService((File) config.getProperty(Config.CONFDIR));
    }

    public AuthResource(TokenService authTokenFactory, UserService userService) {
        this.authTokenFactory = authTokenFactory;
        this.userService = userService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @PermitAll
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

    @GET
    @Path("{username}")
    @RolesAllowed("admin")
    public Response getUser(@PathParam("username") String username) {
        try {
            User user = userService.getUser(username);
            return Response.ok(user).build();
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{username}")
    @RolesAllowed("admin")
    public Response createModifyUser(@PathParam("username") String username, @FormParam("password") String password) {
        try {
            User user = userService.addOrModifyUser(username, password, null);

            return Response.ok(user).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("{username}")
    @RolesAllowed("admin")
    public Response removeUser(@PathParam("username") String username) {
        userService.removeUser(username);
        return Response.ok().build();
    }
}
