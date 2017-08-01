package app.resource;

import app.data.Page;
import app.data.PageList;
import app.exception.EntityNotFoundException;
import app.service.DefaultPageService;
import app.service.PageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Singleton
@Path("pages")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class PageResource {
    private final PageService service;
    private final ObjectWriter objectMapper = new ObjectMapper().writerWithDefaultPrettyPrinter();

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
        try {
            return service.getPage(pageName);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("{pageName}")
    public Response activatePage(@PathParam("pageName") String pageName) {
        try {
            Boolean updated = service.activatePage(pageName);
            return Response.ok(objectMapper.writeValueAsString(Collections.singletonMap("updated", String.valueOf(updated)))).build();
        } catch (JsonProcessingException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("{pageName}")
    public Response deactivatePage(@PathParam("pageName") String pageName) {
        try {
            boolean updated =  service.deactivatePage(pageName);
            return Response.ok(objectMapper.writeValueAsString(Collections.singletonMap("updated", String.valueOf(updated)))).build();
        } catch (JsonProcessingException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
