package app.filter;

import app.data.Token;
import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.InvalidTokenException;
import app.service.TokenService;
import app.service.UserService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtSecurityFilterTest extends JerseyTest {
    private static final User testUser = new User("foo", Collections.singleton("user"));
    private UserService userService;
    private TokenService tokenService;

    @Override
    protected Application configure() {
        tokenService = mock(TokenService.class);
        userService = mock(UserService.class);
        forceSet(TestProperties.CONTAINER_PORT, "0");
        JwtSecurityFilter filter = new JwtSecurityFilter(tokenService, userService);
        return new ResourceConfig().
                register(filter).
                register(RolesAllowedDynamicFeature.class).
                register(MockAuthResource.class).
                register(MockContentResource.class);
    }

    @Test
    public void filterShouldLetAuthRequestsPassThrough() {
        when(userService.getAnonymousUser()).thenReturn(new User("anonymous",
                Collections.emptySet()));

        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");

        Response response = target("/auth").request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.form(form));

        assertEquals(200, response.getStatus());
    }

    @Test
    public void filterReturnsOkForOptionsRequests() {
        Response response = target("content").request().options();
        assertEquals(200, response.getStatus());

        String payload = response.readEntity(String.class);
        assertEquals(0, payload.length());

        String acceptHeader = response.getHeaderString("Accept");
        assertThat(acceptHeader, containsString("GET"));
        assertThat(acceptHeader, containsString("OPTIONS"));
        assertThat(acceptHeader, containsString("PUT"));
        assertThat(acceptHeader, containsString("DELETE"));
    }

    @Test
    public void missingAuthHeaderShouldTriggerUnauthorizedError() {
        Response response = target("content").request().get();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void authHeaderWithoutValidPrefixShouldTriggerUnauthorizedError() {
        Response response = target("content").
                request().header(HttpHeaders.AUTHORIZATION, "123").get();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void invalidAuthHeaderShouldTriggerUnauthorizedError() throws InvalidTokenException {
        when(tokenService.forJwtString(anyString())).thenThrow(new InvalidTokenException());
        Response response = target("content").
                request().header(HttpHeaders.AUTHORIZATION, "Bearer 123").get();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void tokenFromUnknownUserShouldTriggerUnauthorizedError() throws InvalidTokenException, EntityNotFoundException {
        when(tokenService.forJwtString(anyString())).thenReturn(new Token("123", "foo"));
        when(userService.getUser("foo")).thenThrow(new EntityNotFoundException());
        Response response = target("content").
                request().header(HttpHeaders.AUTHORIZATION, "Bearer 123").get();
        assertEquals(401, response.getStatus());
    }

    @Test
    public void userWithInvalidRoleShouldNotGetAccess() throws InvalidTokenException, EntityNotFoundException {
        Response response = testAccessWithRole("notsuper");
        assertEquals(403, response.getStatus());
    }

    @Test
    public void userWithValidRoleShouldGetAccess() throws InvalidTokenException, EntityNotFoundException {
        Response response = testAccessWithRole("superman");
        assertEquals(200, response.getStatus());
        assertEquals("hello", response.readEntity(String.class));
    }

    private Response testAccessWithRole(String role) throws InvalidTokenException, EntityNotFoundException {
        when(tokenService.forJwtString(anyString())).thenReturn(new Token("123", "foo"));
        when(userService.getUser("foo")).thenReturn(new User("foo", Collections.singleton(role)));
        Response response = target("content").
                request().header(HttpHeaders.AUTHORIZATION, "Bearer 123").get();
        return response;
    }

    @PermitAll
    @Path("auth")
    public static final class MockAuthResource {
        @POST
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        public Response post() {
            return Response.ok().build();
        }
    }

    @Path("content")
    public static final class MockContentResource {
        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        @RolesAllowed("superman")
        public Response call() {
            return Response.ok("hello").build();
        }
    }
}
