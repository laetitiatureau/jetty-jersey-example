package app;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class Main extends ResourceConfig {
    private static final Logger logger = Logger.getGlobal();

    private Main() throws IOException {
        packages("app.resource");
        addProperties(Config.loadConfig(System.getProperties()));
    }

    private static URI buildServerURI(final Map<String, Object> configuration) {
        return UriBuilder
                .fromUri((String) configuration.get(Config.HTTP_URI))
                .port((Integer) configuration.get(Config.HTTP_PORT))
                .path("app")
                .build();
    }

    private static HttpServer instantiateServer(final URI baseUri, final ResourceConfig application) {
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


    public static void main(String[] args) throws Exception {
        final ResourceConfig rc = new ResourceConfig();
        final Map<String, Object> cfg = Config.loadConfig(System.getProperties());
        rc.addProperties(cfg);
        rc.packages("app.resource");

        final HttpServer server = instantiateServer(buildServerURI(cfg), rc);

        try {
            server.start();
            logger.info("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start grizzly server", e);
        }
    }
}

