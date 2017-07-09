package app;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends ResourceConfig {
    private static final Logger logger = Logger.getGlobal();

    public Main() throws IOException {
        packages("app.resource");
        loadConfig();
    }

    private void loadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(Main.class.getResourceAsStream("/app.properties"));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            property(entry.getKey().toString(), entry.getValue());
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        String uri = main.getProperty("http.uri").toString();
        int port = Integer.parseInt(main.getProperty("http.port").toString());
        URI baseUri = UriBuilder.fromUri(uri).path("app").port(port).build();

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, main);

        // prod: load static files from classpath
        HttpHandler staticHandler = new CLStaticHttpHandler(
                Main.class.getClassLoader(), "/static/");

        // dev only: load static files from source folder - allows (almost) live editing of html, js, css files
//        HttpHandler staticHandler = new StaticHttpHandler("src/main/resources/static");

        server.getServerConfiguration().addHttpHandler(staticHandler, "/*");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Stopping server..");
                server.shutdown();
            }
        }));

        try {
            server.start();
            logger.info("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start grizzly server", e);
        }
    }
}

