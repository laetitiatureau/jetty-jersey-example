package app.resource;

import app.data.AllPages;
import app.data.Page;
import app.service.DefaultPageService;
import app.service.PageService;

import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Path("/pages")
public class PageResource {
    private PageService service;

    public PageResource(@Context Configuration config) throws IOException, URISyntaxException {
        this(new DefaultPageService(config));
    }

    public PageResource(final PageService service) {
        this.service = service;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream getIndex() {
        return getClass().getResourceAsStream("/index.html");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AllPages getPages() {
        return service.getPages();
    }

    @GET
    @Path("{pageName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page getPage(@PathParam("pageName") String pageName) {
        return service.getPage(pageName);
    }

    @PUT
    @Path("{pageName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page activatePage(@PathParam("pageName") String pageName) {
        return service.activatePage(pageName);
    }

    @DELETE
    @Path("{pageName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Page deactivatePage(@PathParam("pageName") String pageName) {
        return service.deactivatePage(pageName);
    }
}
