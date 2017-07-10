package app.resource;

import app.data.Page;
import app.data.PageList;
import app.service.DefaultPageService;
import app.service.PageService;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Singleton
@Path("pages")
@Produces(MediaType.APPLICATION_JSON)
public class PageResource {
    private PageService service;

    public PageResource(@Context Configuration config) throws IOException {
        this(new DefaultPageService(config));
    }

    public PageResource(final PageService service) {
        this.service = service;
    }

    @GET
    public PageList getPageList() {
        return service.getPageList();
    }

    @GET
    @Path("{pageName}")
    public Page getPage(@PathParam("pageName") String pageName) {
        return service.getPage(pageName);
    }

    @PUT
    @Path("{pageName}")
    public Page activatePage(@PathParam("pageName") String pageName) {
        return service.activatePage(pageName);
    }

    @DELETE
    @Path("{pageName}")
    public Page deactivatePage(@PathParam("pageName") String pageName) {
        return service.deactivatePage(pageName);
    }
}
