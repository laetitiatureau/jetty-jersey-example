package app.resource;

import app.Config;
import app.data.Token;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;

import static org.junit.Assert.assertEquals;

public class AuthResourceInitTest extends JerseyTest {
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private static final Key signingKey = MacProvider.generateKey(signatureAlgorithm);
    private static Gson gson = new Gson();

    @Override
    protected Application configure() {
        ResourceConfig rc = new ResourceConfig();
        rc.property(Config.JWT_KEY, signingKey);
        rc.property(Config.JWT_KEY_ALG, signatureAlgorithm);
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig().register(new AuthResource(rc));
    }

    @Test
    public void test() {
        Form form = new Form();
        form.param("username", "joe@example.com");
        form.param("password", "password1");

        Response response = target("/auth").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));
        assertEquals(200, response.getStatus());
        Token receivedToken = gson.fromJson(response.readEntity(String.class), Token.class);
        String tokenString = receivedToken.getToken();

        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(tokenString).getBody();
        assertEquals("joe@example.com", claims.get("username", String.class));
        assertEquals("joe@example.com", claims.getSubject());
    }
}
