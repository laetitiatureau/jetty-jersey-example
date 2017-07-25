package app;

import app.filter.AuthFilter;
import app.filter.CorsFilter;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class Main extends ResourceConfig {
    private static final Logger logger = Logger.getGlobal();

    protected static URI buildServerURI(final Map<String, Object> configuration) {
        return UriBuilder
                .fromUri((String) configuration.get(Config.HTTP_URI))
                .port((Integer) configuration.get(Config.HTTP_PORT))
                .path("api")
                .build();
    }

    protected static HttpServer instantiateServer(final URI baseUri, final ResourceConfig application) {
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, application, false);

        String webroot = (String) application.getProperty(Config.WEBROOT);
        if (webroot != null) {
            logger.info("Serving static files from dir: " + webroot);
            server.getServerConfiguration().addHttpHandler(
                    new StaticHttpHandler(webroot), "/*");

        } else {
            logger.info("Serving static files from /static/* on classpath");
            server.getServerConfiguration().addHttpHandler(
                    new CLStaticHttpHandler(Main.class.getClassLoader(),
                            "/static/"), "/*");
        }

        if (!(Boolean) application.getProperty(Config.WEBCACHE)) {
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

        return server;
    }

    protected static ResourceConfig createResourceConfig(final Map<String, Object> cfg) throws IOException {
        final ResourceConfig rc = new ResourceConfig();

        rc.addProperties(cfg);
        rc.packages("app.resource");

        if (Boolean.parseBoolean((String) cfg.get(Config.CORS))) {
            rc.register(new CorsFilter());
        }

        if (Boolean.parseBoolean((String) cfg.get(Config.SECURE))) {
            rc.register(RolesAllowedDynamicFeature.class);
            rc.register(new AuthFilter());
        }

        return rc;
    }

    protected static void startServer(Properties config) throws IOException {
        Map<String, Object> cfg = Config.loadConfig(config);
        URI baseUri = buildServerURI(cfg);
        ResourceConfig rc = createResourceConfig(cfg);

        final HttpServer server = instantiateServer(baseUri, rc);

        try {
            server.start();
            logger.info("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start grizzly server", e);
        }
    }

    public static void main(String[] args) throws Exception {
        startServer(System.getProperties());
    }
}

