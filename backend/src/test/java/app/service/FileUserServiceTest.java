package app.service;

import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FileUserServiceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void configDirectoryMustBeProvided() {
        new FileUserService(null);
    }

    @Test
    public void emptyConfigFileAdminUserGetsCreated() {
        UserService userService = new FileUserService(tempFolder.getRoot());
        User user = userService.getUser("admin");
        assertEquals("admin", user.getName());
        assertEquals(Collections.singleton("admin"), user.getRoles());
    }

    @Test(expected = UnauthorizedException.class)
    public void nonExistingUserCannotLogin() throws UnauthorizedException {
        UserService userService = new FileUserService(tempFolder.getRoot());
        userService.addOrModifyUser("joe@example.com", "password1", null);
        userService.authenticate("joe@regular.com", "password1");
    }

    @Test
    public void existingUserCanLogin() throws UnauthorizedException {
        UserService userService = new FileUserService(tempFolder.getRoot());
        userService.addOrModifyUser("joe@example.com", "password1", null);
        User user = userService.authenticate("joe@example.com", "password1");
        assertThat(user.getName(), is(equalTo("joe@example.com")));
        assertThat(user.getRoles(), containsInAnyOrder("user"));
    }

    @Test(expected = UnauthorizedException.class)
    public void existingUserCantLoginWithInvalidPassword() throws UnauthorizedException {
        UserService userService = new FileUserService(tempFolder.getRoot());
        userService.addOrModifyUser("joe@example.com", "password1", null);
        userService.authenticate("joe@example.com", "bungpassword");
    }

    @Test
    public void existingUserCanBeFound() throws EntityNotFoundException {
        UserService userService = new FileUserService(tempFolder.getRoot());
        userService.addOrModifyUser("joe@example.com", "password1", null);
        User user = userService.getUser("joe@example.com");
        assertThat(user.getName(), is(equalTo("joe@example.com")));
        assertThat(user.getRoles(), containsInAnyOrder("user"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void gettingNonExistingUserThrowsException() throws EntityNotFoundException {
        UserService userService = new FileUserService(tempFolder.getRoot());
        userService.addOrModifyUser("joe@example.com", "password1", null);
        userService.getUser("joe@regular.com");
    }

    @Test(expected = EntityNotFoundException.class)
    public void removeUserRemovesTheUser() {
        UserService userService = new FileUserService(tempFolder.getRoot());
        userService.addOrModifyUser("joe@example.com", "password1", null);
        assertNotNull(userService.getUser("joe@example.com"));
        userService.removeUser("joe@example.com");
        assertNull(userService.getUser("joe@example.com"));
    }
}
