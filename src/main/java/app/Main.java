package app;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

public class Main extends ResourceConfig {
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
        URI baseUri = UriBuilder.fromUri(uri).port(port).build();
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, main);
        HttpHandler staticHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "/static/");
        server.getServerConfiguration().addHttpHandler(staticHandler,"/static/*");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Stopping server..");
                server.shutdown();
            }
        }));

        try {
            server.start();
            System.out.println("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

