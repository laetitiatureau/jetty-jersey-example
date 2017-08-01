package app.data;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class UserDirectoryTest {
    @Test(expected = IllegalArgumentException.class)
    public void addingUserWithNullNameShouldTriggerException() {
        UserDirectory dir = new UserDirectory();
        dir.addUser(new User(null, Collections.emptySet()), "password");
    }

    @Test
    public void addedUserCanBeFound() {
        UserDirectory dir = new UserDirectory();
        dir.addUser(new User("test", Collections.emptySet()), "password1");
        User foundUser = dir.getUser("test");
        assertEquals("test", foundUser.getName());
        assertEquals(Collections.emptySet(), foundUser.getRoles());
        assertEquals("password1", dir.getPassword("test"));
    }

    @Test
    public void userWithSameNameOverwritesExistingUser() {
        UserDirectory dir = new UserDirectory();
        dir.addUser(new User("test", Collections.emptySet()), "password1");
        dir.addUser(new User("test", Collections.singleton("foo")), "password2");
        User foundUser = dir.getUser("test");
        assertEquals("test", foundUser.getName());
        assertEquals(Collections.singleton("foo"), foundUser.getRoles());
        assertEquals("password2", dir.getPassword("test"));
    }

    @Test
    public void getNonExistingUserReturnsNull() {
        UserDirectory dir = new UserDirectory();
        assertThat(dir.getUser("foo"), is(equalTo(null)));
        assertThat(dir.getPassword("foo"), is(equalTo(null)));
    }

    @Test
    public void removingNonExistingUserReturnsFalse() {
        UserDirectory dir = new UserDirectory();
        boolean removed = dir.removeUser("foo");
        assertThat(removed, is(equalTo(false)));
    }

    @Test
    public void removingExistingUserReturnsTrue() {
        UserDirectory dir = new UserDirectory();
        dir.addUser(new User("test", Collections.singleton("foo")), "password2");
        assertNotNull(dir.getUser("test"));
        assertNotNull(dir.getPassword("test"));
        boolean removed = dir.removeUser("test");
        assertThat(removed, is(equalTo(true)));
        assertNull(dir.getUser("test"));
        assertNull(dir.getPassword("test"));
    }

    @Test
    public void constructorPopulatesData() {
        Map<String, User> users = new HashMap<>();
        users.put("foo", new User("foo", Collections.singleton("bar")));
        Map<String, String> passwords = new HashMap<>();
        passwords.put("foo", "pw");
        UserDirectory dir = new UserDirectory(users, passwords);
        User user = dir.getUser("foo");
        assertEquals("foo", user.getName());
        assertEquals(Collections.singleton("bar"), user.getRoles());
        assertEquals("pw", dir.getPassword("foo"));
    }
}
