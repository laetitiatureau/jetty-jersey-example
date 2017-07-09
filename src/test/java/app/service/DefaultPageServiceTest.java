package app.service;

import app.data.PageList;
import app.data.Page;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DefaultPageServiceTest {

    @Test(expected = NotFoundException.class)
    public void invalidPageNameShouldThrowException() throws IOException {
        Set<String> pageNames = Collections.singleton("foo");

        PageService service = new DefaultPageService(pageNames, "/tmp");

        service.activatePage("bar");
        service.deactivatePage("bar");
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
    public void getPageConsistentWithGetPages() {
        // TODO
        // create files for a couple of environments
        // call getPageList() and cross-check the result with getPage() for each
        // should return the same state
        fail();
    }

    @Test
    public void getPageReturnsCorrectState() {
        // TODO
        // put state file into workdir, e.g. for 'foo'
        // then call getPage('foo') and check if the state
        // gets parsed correctly
        fail();
    }

    @Test
    public void activatePageChangesState() {

        // TODO
        // put state file into workdir, e.g. for 'foo'
        // with state 'inactive'
        // call activatePage('foo') and examine the result. should have been updated
        // to 'active'
        // ... run the same test with initial state 'active', the file shouldnt
        // be touched at all (compare timestamp)
        fail();
    }

    @Test
    public void deactivatePageChangesState() {

        // TODO
        // put state file into workdir, e.g. for 'foo'
        // with state 'active'
        // call deactivatePage('foo') and examine the result. should have been updated
        // to 'inactive'
        // ... run the same test with initial state 'inactive', the file shouldnt
        // be touched at all (compare timestamp)
        fail();
    }
}
