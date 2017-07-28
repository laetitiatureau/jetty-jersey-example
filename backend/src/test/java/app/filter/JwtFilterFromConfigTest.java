package app.filter;

import app.Config;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import java.security.Key;

import static org.junit.Assert.assertEquals;

public class JwtFilterFromConfigTest extends JerseyTest {

    @Override
    protected Application configure() {
        Key key = MacProvider.generateKey(SignatureAlgorithm.HS512);

        ResourceConfig rc = new ResourceConfig();
        rc.property(Config.JWT_KEY, key);
        rc.property(Config.JWT_KEY_ALG,SignatureAlgorithm.HS512);

        JwtSecurityFilter filter = new JwtSecurityFilter(rc);
        return new ResourceConfig()
                .register(filter);
    }

    @Test
    public void test() {
        Response response = target("foo").request().get();
        assertEquals(404, response.getStatus());
    }

}
