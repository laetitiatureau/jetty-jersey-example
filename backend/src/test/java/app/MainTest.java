package app;

import app.filter.CORSFilter;
import app.filter.JwtSecurityFilter;
import app.resource.AuthResource;
import app.resource.PageResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
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
        cfg.put("foo", 1);
        cfg.put(Config.CORS, "true");
        cfg.put(Config.SECURE, "true");

        ResourceConfig rc = Main.createResourceConfig(cfg);

        assertEquals("true", rc.getProperty(Config.CORS));
        assertEquals("true", rc.getProperty(Config.SECURE));
        assertEquals(1, rc.getProperty("foo"));

        assertThat(rc.getClasses(), hasItem(PageResource.class));
        assertThat(rc.getClasses(), hasItem(CORSFilter.class));
        assertThat(rc.getClasses(), hasItem(JwtSecurityFilter.class));
        assertThat(rc.getClasses(), hasItem(AuthResource.class));
        assertThat(rc.getClasses(), hasItem(RolesAllowedDynamicFeature.class));
    }
}
