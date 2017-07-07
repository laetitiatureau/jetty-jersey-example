package app.page;

import org.junit.Test;

import javax.ws.rs.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DefaultPageManagerTest {

    @Test(expected = NotFoundException.class)
    public void invalidEnvironmentShouldThrowException() throws IOException {

        PageManager manager =
                new DefaultPageManager(Collections.singleton("foo"), new File("/tmp"));

        manager.activatePage("bar");
        manager.deactivatePage("bar");
        manager.getPageState("bar");
    }

    @Test
    public void getPageStatesReturnsAllEnvironments() throws IOException {
        Set<String> environments = new HashSet<>();
        environments.add("foo");
        environments.add("bar");

        PageManager manager =
                new DefaultPageManager(environments, new File("/tmp"));

        PageStates pageStates = manager.getPageStates();
        Set<String> returnedEnvironments = new HashSet<>();
        for (PageState state : pageStates.getEnvironments()) {
            returnedEnvironments.add(state.getEnvironmentName());
        }
        assertEquals(environments, returnedEnvironments);
    }

    @Test
    public void getPageStateConsistentWithGetPageStates() {
        // TODO
        // create files for a couple of environments
        // call getPageStates() and cross-check the result with getPageState() for each
        // should return the same state
        fail();
    }

    @Test
    public void getPageStateReturnsCorrectState() {
        // TODO
        // put state file into workdir, e.g. for 'foo'
        // then call getPageState('foo') and check if the state
        // gets parsed correctly
        fail();
    }

    @Test
    public void activatePageStateChangesState() {

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
    public void deactivatePageStateChangesState() {

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
