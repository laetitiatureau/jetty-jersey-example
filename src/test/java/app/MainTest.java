package app;

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
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
