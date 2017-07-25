package app.resource;

import app.data.Token;
import app.data.User;
import app.service.TokenService;
import app.service.UserService;
import com.google.gson.Gson;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthResourceTest extends JerseyTest {
    private Gson gson = new Gson();
    private UserService userService;
    private TokenService tokenService;

    @Override
    protected Application configure() {
        tokenService = mock(TokenService.class);
        userService = mock(UserService.class);

        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig().register(new AuthResource(tokenService, userService));
    }

    @Test
    public void testSuccessfulAuth() {
        String tokenString = "123";

        String username = "joe@example.com";
        String password = "password1";

        User user = new User(username, Collections.singleton("user"), 0);
        when(userService.authenticate(username, password)).thenReturn(user);

        Token token = new Token(tokenString);
        when(tokenService.createToken(user.getName(), user.getRoles(), user.getVersion())).thenReturn(token);

        Form form = new Form();
        form.param("username", username);
        form.param("password", password);

        Response response = target("/auth").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));

        assertEquals(200, response.getStatus());

        String authHeader = response.getHeaderString(HttpHeaders.AUTHORIZATION);

        assertEquals("Bearer " + tokenString, authHeader);
    }

    @Test
    public void testAuthWithUserServiceException() {
        when(userService.authenticate(anyString(), anyString())).thenThrow(
                new NotAuthorizedException("User not found"));

        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");

        Response response = target("/auth").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));

        assertEquals(401, response.getStatus());


    }

}
