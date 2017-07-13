package app.service;

import app.data.Page;
import app.data.PageList;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultPageServiceTest {

    @Test(expected = NotFoundException.class)
    public void activateForinvalidPageNameShouldThrowException() throws IOException {
        Set<String> pageNames = Collections.singleton("foo");
        PageService service = new DefaultPageService(pageNames, "/tmp");
        service.activatePage("bar");
    }

    @Test(expected = NotFoundException.class)
    public void deactivateForinvalidPageNameShouldThrowException() throws IOException {
        Set<String> pageNames = Collections.singleton("foo");
        PageService service = new DefaultPageService(pageNames, "/tmp");
        service.deactivatePage("bar");
    }

    @Test(expected = NotFoundException.class)
    public void getPageForinvalidPageNameShouldThrowException() throws IOException {
        Set<String> pageNames = Collections.singleton("foo");
        PageService service = new DefaultPageService(pageNames, "/tmp");
        service.getPage("bar");
    }

    @Test
    public void getPagesReturnsAllNames() throws IOException {
        Set<String> pageNames = new HashSet<>();
        pageNames.add("foo");
        pageNames.add("bar");

        PageService service = new DefaultPageService(pageNames, "/tmp");

        PageList pageList = service.getPageList();
        Set<String> returnedPageNames = new HashSet<>();
        for (Page state : pageList.getPages()) {
            returnedPageNames.add(state.getName());
        }
        assertEquals(pageNames, returnedPageNames);
    }

    @Test
    public void getPageConsistentWithGetPages() throws IOException {
        final Set<String> allPages = new HashSet<>(Arrays.asList("foo", "bar", "baz"));
        final String tempDir = Files.createTempDirectory(null).toString();

        new File(tempDir, "foo").createNewFile();
        new File(tempDir, "baz").createNewFile();

        PageService pageService = new DefaultPageService(allPages, tempDir);

        for (Page bulkPage : pageService.getPageList().getPages()) {
            Page singlePage = pageService.getPage(bulkPage.getName());
            assertEquals(bulkPage, singlePage);
        }
    }

    @Test
    public void getPageForPageReturnsCorrectState() throws IOException {
        final String tempDir = Files.createTempDirectory(null).toString();
        final String pageName = "foo";
        final File pageFile = new File(tempDir, pageName);
        pageFile.createNewFile();

        PageService pageService = new DefaultPageService(
                Collections.singleton(pageName), tempDir);

        Page page = pageService.getPage(pageName);

        assertEquals(pageName, page.getName());
        assertEquals(true, page.isActive());

        pageFile.delete();

        Page pageAfterDelete = pageService.getPage(pageName);

        assertEquals(pageName, pageAfterDelete.getName());
        assertEquals(false, pageAfterDelete.isActive());
    }

    @Test
    public void activatePageChangesState() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();
        final String pageName = "foo";
        final File pageFile = new File(tempDir, pageName);

        PageService pageService = new DefaultPageService(
                Collections.singleton(pageName), tempDir.toString());

        assertFalse(pageFile.exists());
        Page page = pageService.activatePage(pageName);
        assertTrue(pageFile.exists());
        assertTrue(page.isActive());
    }

    @Test(expected = PageServiceException.class)
    public void activatePageFileCantBeWritten() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        final String pageName = "foo";
        PageService pageService = new DefaultPageService(
                Collections.singleton(pageName), tempDir.toString());
        tempDir.delete();
        pageService.activatePage(pageName);
    }

    @Test
    public void deactivatePageChangesState() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();
        final String pageName = "foo";
        final File pageFile = new File(tempDir, pageName);
        pageFile.createNewFile();

        PageService pageService = new DefaultPageService(
                Collections.singleton(pageName), tempDir.toString());

        assertTrue(pageFile.exists());
        Page page = pageService.deactivatePage(pageName);
        assertFalse(pageFile.exists());
        assertFalse(page.isActive());

        pageService.deactivatePage(pageName);
    }

    @Test
    public void deactivateInactivePageIsIgnored() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();
        final String pageName = "foo";
        PageService pageService = new DefaultPageService(
                Collections.singleton(pageName), tempDir.toString());
        pageService.deactivatePage(pageName);
    }

    @Test(expected = PageServiceException.class)
    public void workDirDoesntExist() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();
        final String nonExistingDir = new File(tempDir, "foo").toString();
        new DefaultPageService(Collections.singleton("foo"), nonExistingDir);
    }

    @Test(expected = PageServiceException.class)
    public void workDirReadOnly() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();
        final File readOnlyDir = new File(tempDir, "foo");
        readOnlyDir.setReadOnly();
        new DefaultPageService(Collections.singleton("foo"), readOnlyDir.toString());
    }

    @Test
    public void instantiateWithConfiguration() throws IOException {
        final File tempDir = Files.createTempDirectory(null).toFile();
        tempDir.deleteOnExit();

        Configuration cfg = mock(Configuration.class);
        when(cfg.getProperty("pages")).thenReturn("foo,bar");
        when(cfg.getProperty("workdir")).thenReturn(tempDir.toString());
        DefaultPageService service = new DefaultPageService(cfg);

        Set<String> pageNames = new HashSet<>();
        for (Page page : service.getPageList().getPages()) {
            pageNames.add(page.getName());
        }
        assertEquals(2, pageNames.size());
        assertTrue(pageNames.contains("foo"));
        assertTrue(pageNames.contains("bar"));
        assertEquals(tempDir, service.getWorkdir());

    }
}
