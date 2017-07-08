package app.rs;

import app.page.DefaultPageManager;
import app.page.PageManager;
import app.page.PageState;
import app.page.PageStates;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@Path("/")
public class PageManagerResource {
    private PageManager manager;

    public PageManagerResource() throws IOException, URISyntaxException {
        this(new DefaultPageManager(new File("/tmp")));
    }

    public PageManagerResource(PageManager manager) {
        this.manager = manager;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getIndex() throws URISyntaxException, IOException {
        URL url = getClass().getResource("/index.html");
        java.nio.file.Path path = Paths.get(url.toURI());
        return new String(Files.readAllBytes(path));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PageStates getPageStates() {
        return manager.getPageStates();
    }

    @GET
    @Path("{environmentName}")
    @Produces(MediaType.APPLICATION_JSON)
    public PageState getCurrentState(@PathParam("environmentName") String environmentName) {
        return manager.getPageState(environmentName);
    }

    @PUT
    @Path("{environmentName}")
    @Produces(MediaType.APPLICATION_JSON)
    public PageState activatePage(@PathParam("environmentName") String environmentName) {
        return manager.activatePage(environmentName);
    }

    @DELETE
    @Path("{environmentName}")
    @Produces(MediaType.APPLICATION_JSON)
    public PageState deactivatePage(@PathParam("environmentName") String environmentName) {
        return manager.deactivatePage(environmentName);
    }
}
