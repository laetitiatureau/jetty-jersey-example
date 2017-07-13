package app;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ak on 14/07/17.
 */
public class MainTest {
    @Test
    public void test() throws URISyntaxException {
        URI uri = UriBuilder.fromUri(new URI("http://localhost:0")).build();

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property("webcache", false);


        HttpServer server = Main.instantiateServer(uri, resourceConfig);
        assertEquals(false,
                server.getListener("grizzly").getFileCache().isEnabled());


    }
}
