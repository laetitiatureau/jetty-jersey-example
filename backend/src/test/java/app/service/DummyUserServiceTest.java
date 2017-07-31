package app.service;

import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;

public class DummyUserServiceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = UnauthorizedException.class)
    public void nonExistingUserCannotLogin() throws UnauthorizedException {
        UserService userService = new DummyUserService(tempFolder.getRoot());
        userService.authenticate("joe@regular.com", "password1");
    }

    @Test
    public void existingUserCanLogin() throws UnauthorizedException {
        UserService userService = new DummyUserService(tempFolder.getRoot());
        User user = userService.authenticate("joe@example.com", "password1");
        assertThat(user.getName(), is(equalTo("joe@example.com")));
        assertThat(user.getRoles(), containsInAnyOrder("user"));
    }

    @Test(expected = UnauthorizedException.class)
    public void existingUserCantLoginWithInvalidPassword() throws UnauthorizedException {
        UserService userService = new DummyUserService(tempFolder.getRoot());
        userService.authenticate("joe@example.com", "bungpassword");
    }

    @Test
    public void existingUserCanBeFound() throws EntityNotFoundException {
        UserService userService = new DummyUserService(tempFolder.getRoot());
        User user = userService.getUser("joe@example.com");
        assertThat(user.getName(), is(equalTo("joe@example.com")));
        assertThat(user.getRoles(), containsInAnyOrder("user"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void gettingNonExistingUserThrowsException() throws EntityNotFoundException {
        UserService userService = new DummyUserService(tempFolder.getRoot());
        userService.getUser("joe@regular.com");
    }
}
