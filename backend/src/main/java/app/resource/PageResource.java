package app.resource;

import app.data.Page;
import app.data.PageList;
import app.service.DefaultPageService;
import app.service.PageService;

import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("pages")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class PageResource {
    private final PageService service;

    public PageResource(@Context Configuration config) {
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
