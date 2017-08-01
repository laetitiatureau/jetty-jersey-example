package app.resource;

import app.data.Token;
import app.data.User;
import app.exception.UnauthorizedException;
import app.service.TokenService;
import app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthResourceTest extends JerseyTest {
    private ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;
    private TokenService tokenService;

    @Override
    protected Application configure() {
        tokenService = mock(TokenService.class);
        userService = mock(UserService.class);

        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig().packages("app.mappers").register(new AuthResource(tokenService, userService));
    }

    @Test
    public void testSuccessfulAuth() throws UnauthorizedException, IOException {
        String tokenString = "123";

        String username = "joe@example.com";
        String password = "password1";

        User user = new User(username, Collections.singleton("user"));
        when(userService.authenticate(username, password)).thenReturn(user);

        Token token = new Token(tokenString, user.getName());
        when(tokenService.forUser(user)).thenReturn(token);

        Form form = new Form();
        form.param("username", username);
        form.param("password", password);

        Response response = target("/users").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));

        assertEquals(200, response.getStatus());

        ObjectReader reader = objectMapper.readerFor(Token.class);
        Token receivedToken = reader.readValue(response.readEntity(String.class));

//        assertNull(receivedToken.getUsername());

        assertEquals(token.getToken(), receivedToken.getToken());
    }

    @Test
    public void testAuthWithUserServiceException() throws UnauthorizedException {
        when(userService.authenticate(any(), any())).thenThrow(
                new UnauthorizedException());

        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");

        Response response = target("/users").
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.form(form));

        assertEquals(401, response.getStatus());
    }

    @Test
    public void allOtherExceptionsShouldReturnInternalServerError() throws UnauthorizedException {
        when(userService.authenticate(any(), any())).thenThrow(new RuntimeException("BOOM!"));

        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");

        Response response = target("/users").
                request(MediaType.APPLICATION_JSON_TYPE).
                post(Entity.form(form));

        assertEquals(500, response.getStatus());
    }
}
