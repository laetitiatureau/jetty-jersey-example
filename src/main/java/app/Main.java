package app;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.DefaultErrorPageGenerator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class Main extends ResourceConfig {
    private static final Logger logger = Logger.getGlobal();
    private static final String HTTP_URI = "http.uri";
    private static final String HTTP_PORT = "http.port";
    private static final String WORKDIR = "workdir";
    private static final String WEBROOT = "webroot";
    public static final String WEBCACHE = "webcache";
    public static final String PAGES = "pages";

    private Main() throws IOException {
        packages("app.resource");
        loadConfig();
    }

    private void loadConfig() throws IOException {
        Properties props = System.getProperties();

        Map<String, Object> cfg = new LinkedHashMap<>();
        cfg.put(HTTP_URI, props.getProperty(HTTP_URI, "http://0.0.0.0"));
        cfg.put(HTTP_PORT, Integer.parseInt(props.getProperty(HTTP_PORT, "8080")));

        String workDir = props.getProperty(WORKDIR);
        if (workDir != null) {
            File workDirFile = new File(workDir);
            if (!workDirFile.exists() || !workDirFile.canRead() || !workDirFile.isDirectory()) {
                throw new ConfigurationException("Config setting for 'workdir' invalid - can't access " + workDir);
            }
            cfg.put(WORKDIR, workDir);
        } else {
            File tmpWorkDir = Files.createTempDirectory(null).toFile();
            tmpWorkDir.deleteOnExit();
            cfg.put(WORKDIR, tmpWorkDir.toString());
            logger.warning("Config setting for 'workdir' not defined - " +
                    "using temporary directory. Files will be deleted on shutdown.");
        }

        if (props.getProperty(PAGES) == null) {
            throw new ConfigurationException("Config setting for 'pages' (comma-separated list of valid pagenames) not defined");
        }
        cfg.put(PAGES, props.getProperty(PAGES));

        String webRoot = props.getProperty(WEBROOT);
        if (webRoot != null) {
            File webRootDir = new File(webRoot);
            if (!webRootDir.exists() || !webRootDir.canRead()) {
                throw new ConfigurationException("Config setting for 'webroot' is invalid - can access directory " + webRoot);
            }
            cfg.put(WEBROOT, webRoot);
        }

        String webCache = props.getProperty(WEBCACHE);
        if (webCache != null) {
            cfg.put(WEBCACHE, Boolean.parseBoolean(webCache));
        } else {
            cfg.put(WEBCACHE, true);
        }

        for (Map.Entry<String, Object> entry : cfg.entrySet()) {
            logger.info("config: " + entry.getKey() + "=" + entry.getValue());
        }

        addProperties(cfg);
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        String uri = main.getProperty(HTTP_URI).toString();
        int port = (int) main.getProperty(HTTP_PORT);
        URI baseUri = UriBuilder.fromUri(uri).path("app").port(port).build();

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, main, false);

        // prod: load static files from classpath
        String webroot = (String) main.getProperty(WEBROOT);
        if (webroot != null) {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Serving static files from dir: " + webroot);
            }
            server.getServerConfiguration().addHttpHandler(
                    new StaticHttpHandler(webroot), "/*");

        } else {
            logger.info("Serving static files from /static/* on classpath");
            server.getServerConfiguration().addHttpHandler(
                    new CLStaticHttpHandler(Main.class.getClassLoader(),
                            "/static/"), "/*");
        }

        if (!(Boolean) main.getProperty(WEBCACHE)) {
            logger.info("Deactivating grizzly file cache ...");
            server.getListener("grizzly").getFileCache().setEnabled(false);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Stopping server..");
                server.shutdown();
            }
        }));

        server.getServerConfiguration().setDefaultErrorPageGenerator(new DefaultErrorPageGenerator());

        try {
            server.start();
            logger.info("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start grizzly server", e);
        }
    }
}

