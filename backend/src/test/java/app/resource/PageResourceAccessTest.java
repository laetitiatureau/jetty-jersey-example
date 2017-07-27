package app.resource;

import app.service.PageService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PageResourceAccessTest extends JerseyTest {
    private PageService pageService;

    @Override
    protected Application configure() {
        pageService = mock(PageService.class);

        return new ResourceConfig().
                register(new PageResource(pageService)).
                register(createAuthFilter()).
                register(RolesAllowedDynamicFeature.class);

    }

    @Test
    public void verifyNoAccessToPageList() {
        Response response = target("pages").request().get();
        assertEquals(403, response.getStatus());
    }

    @Test
    public void verifyNoAccessToIndividualPage() {
        Response response = target("pages/foo").request().get();
        assertEquals(403, response.getStatus());
    }

    @Test
    public void verifyNoAccessToPutIndividualPage() {
        Response response = target("pages/foo").request().put(Entity.json(""));
        assertEquals(403, response.getStatus());
    }

    @Test
    public void verifyNoAccessToDeleteIndividualPage() {
        Response response = target("pages/foo").request().delete();
        assertEquals(403, response.getStatus());
    }

    private ContainerRequestFilter createAuthFilter() {
        SecurityContext mockContext = mock(SecurityContext.class);
        when(mockContext.isUserInRole("invalid")).thenReturn(true);

        ContainerRequestFilter filter = mock(ContainerRequestFilter.class);
        try {
            doAnswer(invocationOnMock -> {
                ContainerRequestContext ctx = (ContainerRequestContext) invocationOnMock.getArguments()[0];
                ctx.setSecurityContext(mockContext);
                return null;
            }).when(filter).filter(any(ContainerRequestContext.class));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filter;
    }

}
