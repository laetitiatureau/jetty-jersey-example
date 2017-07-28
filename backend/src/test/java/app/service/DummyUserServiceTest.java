package app.service;

import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;

public class DummyUserServiceTest {

    @Test
    public void anonymousUserShouldntHaveAnyRoles() {
        UserService userService = new DummyUserService();
        User user = userService.getAnonymousUser();
        assertThat(user.getName(), is(equalTo("anonymous")));
        assertThat(user.getRoles(), empty());
    }

    @Test(expected = UnauthorizedException.class)
    public void nonExistingUserCannotLogin() throws UnauthorizedException {
        UserService userService = new DummyUserService();
        userService.authenticate("joe@regular.com", "password1");
    }

    @Test
    public void existingUserCanLogin() throws UnauthorizedException {
        UserService userService = new DummyUserService();
        User user = userService.authenticate("joe@example.com", "password1");
        assertThat(user.getName(), is(equalTo("joe@example.com")));
        assertThat(user.getRoles(), containsInAnyOrder("user"));
    }

    @Test(expected = UnauthorizedException.class)
    public void existingUserCantLoginWithInvalidPassword() throws UnauthorizedException {
        UserService userService = new DummyUserService();
        userService.authenticate("joe@example.com", "bungpassword");
    }

    @Test
    public void existingUserCanBeFound() throws EntityNotFoundException {
        UserService userService = new DummyUserService();
        User user = userService.getUser("joe@example.com");
        assertThat(user.getName(), is(equalTo("joe@example.com")));
        assertThat(user.getRoles(), containsInAnyOrder("user"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void gettingNonExistingUserThrowsException() throws EntityNotFoundException {
        UserService userService = new DummyUserService();
        userService.getUser("joe@regular.com");
    }
}
