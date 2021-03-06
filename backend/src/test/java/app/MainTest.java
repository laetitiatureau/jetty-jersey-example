package app;

import app.filter.CorsFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MainTest {
    @Test
    public void testInstantiateServer() throws URISyntaxException, IOException {
        URI uri = UriBuilder.fromUri(new URI("http://localhost:0")).build();

        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.property(Config.WEBCACHE, false);

        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();

        resourceConfig.property(Config.WORKDIR, tempDir.toString());

        HttpServer server = Main.instantiateServer(uri, resourceConfig);
        assertEquals(false,
                server.getListener("grizzly").getFileCache().isEnabled());


    }

    @Test
    public void testBuildBaseUriFromConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put(Config.HTTP_PORT, 12345);
        config.put(Config.HTTP_URI, "http://test");
        URI uri = Main.buildServerURI(config);
        assertEquals(12345, uri.getPort());
        assertEquals("test", uri.getHost());
        assertEquals("http", uri.getScheme());
    }

    @Test
    public void testCreateResourceConfig() throws IOException {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(Config.CORS, "true");
        cfg.put("foo", 1);

        ResourceConfig rc = Main.createResourceConfig(cfg);

        assertEquals("true", rc.getProperty(Config.CORS));
        assertEquals(1, rc.getProperty("foo"));
        assertThat(rc.getInstances(), contains(instanceOf(CorsFilter.class)));

    }
}
