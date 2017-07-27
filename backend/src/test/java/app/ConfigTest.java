package app;

import app.exception.ConfigurationException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ak on 11/07/17.
 */
public class ConfigTest {

    @Test(expected = ConfigurationException.class)
    public void loadEmptyPropertiesShouldFail() throws IOException {
        Properties properties = new Properties();
        Config.loadConfig(properties);
    }

    @Test
    public void justMandatories() throws IOException {
        Properties properties = new Properties();
        properties.put(Config.PAGES, "page1,page2");
        Map<String, Object> processed = Config.loadConfig(properties);

        assertEquals(properties.get(Config.PAGES),
                processed.get(Config.PAGES));
        assertTrue(processed.containsKey(Config.HTTP_URI));
        assertTrue(processed.containsKey(Config.HTTP_PORT));
        assertTrue(processed.containsKey(Config.WEBCACHE));
        assertTrue((Boolean) processed.get(Config.WEBCACHE));
        assertTrue(processed.containsKey(Config.WORKDIR));
        assertTrue(new File(processed.get(Config.WORKDIR).toString()).exists());
        assertTrue(new File(processed.get(Config.WORKDIR).toString()).canWrite());
    }

    @Test(expected = ConfigurationException.class)
    public void invalidWebrootGetsRejected() throws IOException {
        Properties properties = new Properties();
        properties.put(Config.PAGES, "page1");
        properties.put(Config.WEBROOT, "/" + UUID.randomUUID().toString());
        Config.loadConfig(properties);
    }

    @Test(expected = ConfigurationException.class)
    public void invalidWorkDirGetsRejected() throws IOException {
        Properties properties = new Properties();
        properties.put(Config.PAGES, "page1");
        properties.put(Config.WORKDIR, "/" + UUID.randomUUID().toString());
        Config.loadConfig(properties);
    }

    @Test
    public void validWebRootGetsPassedThrough() throws IOException {
        Properties properties = new Properties();
        properties.put(Config.PAGES, "page1");
        File f = Files.createTempDirectory(null).toFile();
        f.deleteOnExit();
        properties.put(Config.WEBROOT, f.toString());
        Map<String, Object> processed =
                Config.loadConfig(properties);
        assertEquals(properties.get(Config.WEBROOT),
                processed.get(Config.WEBROOT));
    }

    @Test
    public void validWorkDirGetsPassedThrough() throws IOException {
        Properties properties = new Properties();
        properties.put(Config.PAGES, "page1");
        File f = Files.createTempDirectory(null).toFile();
        f.deleteOnExit();
        properties.put(Config.WORKDIR, f.toString());
        Map<String, Object> processed =
                Config.loadConfig(properties);
        assertEquals(properties.get(Config.WORKDIR),
                processed.get(Config.WORKDIR));
    }

    @Test
    public void validWebCacheGetsPassedThrough() throws IOException {
        Properties properties = new Properties();
        properties.put(Config.PAGES, "page1");
        properties.put(Config.WEBCACHE, "false");
        Map<String, Object> processed = Config.loadConfig(properties);
        assertEquals(false, processed.get(Config.WEBCACHE));
    }
}
