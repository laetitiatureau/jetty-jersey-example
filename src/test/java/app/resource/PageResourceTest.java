package app.resource;

import app.data.Page;
import app.data.PageList;
import app.service.PageService;
import com.google.gson.Gson;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageResourceTest extends JerseyTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Map<String, Page> pageIndex;
    private Set<Page> pages;
    private Gson gson;

    @Override
    protected Application configure() {

        Set<Page> pages = new HashSet<>();
        Map<String, Page> pageIndex = new HashMap<>();

        Page fooPage = new Page("foo", false);
        Page barPage = new Page("bar", false);
        Page activatedPage = new Page("activated", true);
        Page deactivatedPage = new Page("deactivated", false);

        pages.add(fooPage);
        pages.add(barPage);

        pageIndex.put(fooPage.getName(), fooPage);
        pageIndex.put(barPage.getName(), barPage);
        pageIndex.put(activatedPage.getName(), activatedPage);
        pageIndex.put(deactivatedPage.getName(), deactivatedPage);

        this.gson = new Gson();
        this.pages = Collections.unmodifiableSet(pages);
        this.pageIndex = Collections.unmodifiableMap(pageIndex);
        PageService service = mock(PageService.class);

        when(service.getPageList()).thenReturn(new PageList(pages));
        when(service.getPage(fooPage.getName())).thenReturn(fooPage);
        when(service.getPage(barPage.getName())).thenReturn(barPage);
        when(service.activatePage(activatedPage.getName())).thenReturn(activatedPage);
        when(service.deactivatePage(deactivatedPage.getName())).thenReturn(deactivatedPage);

        when(service.getPage("invalid")).thenThrow(new NotFoundException());
        when(service.activatePage("invalid")).thenThrow(new NotFoundException());
        when(service.deactivatePage("invalid")).thenThrow(new NotFoundException());

        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig().register(new PageResource(service));
    }

    @Test
    public void getPagesAsJson() {
        Response response = target("/pages").request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        PageList result = gson.fromJson(response.readEntity(String.class), PageList.class);
        Set<Page> resultPages = new HashSet<>(result.getPages());
        assertEquals(pages, resultPages);
    }

    @Test
    public void getExistingPages() {
        for (Page page : pages) {
            getExistingPageAsJson(page.getName());
        }
    }

    private void getExistingPageAsJson(String name) {
        Response response = target("/pages/" + name).request(MediaType.APPLICATION_JSON).get();
        assertEquals(200, response.getStatus());
        Page resultPage = gson.fromJson(response.readEntity(String.class), Page.class);
        assertEquals(pageIndex.get(name), resultPage);
    }

    @Test
    public void getOfInvalidPageReturns404() {
        Response response = target("/pages/invalid").request(MediaType.APPLICATION_JSON).get();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void activatePage() {
        final String pageName = "activated";
        Response response = target("/pages/" + pageName).request(MediaType.APPLICATION_JSON)
                .put(Entity.json(""));
        assertEquals(200, response.getStatus());
        Page resultPage = gson.fromJson(response.readEntity(String.class), Page.class);
        assertEquals(pageIndex.get(pageName), resultPage);
    }

    @Test
    public void deactivatePage() {
        final String pageName = "deactivated";
        Response response = target("/pages/" + pageName).request(MediaType.APPLICATION_JSON).delete();
        assertEquals(200, response.getStatus());
        Page resultPage = gson.fromJson(response.readEntity(String.class), Page.class);
        assertEquals(pageIndex.get(pageName), resultPage);
    }

    @Test
    public void activateInvalidPage() {
        Response response = target("/pages/invalid").request(MediaType.APPLICATION_JSON).put(Entity.json(""));
        assertEquals(404, response.getStatus());
    }

    @Test
    public void deactivateInvalidPage() {
        Response response = target("/pages/invalid").request(MediaType.APPLICATION_JSON).delete();
        assertEquals(404, response.getStatus());
    }

    @Test
    public void instantiateWithConfiguration() throws IOException {
        final File tempDir = temporaryFolder.newFolder();
        tempDir.deleteOnExit();

        Configuration cfg = mock(Configuration.class);
        when(cfg.getProperty("pages")).thenReturn("foo,bar");
        when(cfg.getProperty("workdir")).thenReturn(tempDir.toString());

        PageResource resource = new PageResource(cfg);
        PageList pageList = resource.getPageList();
        List<Page> pages = pageList.getPages();
        assertThat(pages, containsInAnyOrder(new Page("foo", false), new Page("bar", false)));

    }
}
