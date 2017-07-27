package app.data;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UserTest {

    @Test
    public void testEquals() {
        User u1 = new User("foo", Collections.singleton("role1"));
        assertEquals(u1, u1);
        assertNotEquals(u1, null);
        assertEquals(u1, new User("foo", Collections.singleton("role1")));
        assertNotEquals(u1, new User("bar", Collections.singleton("role1")));
        assertNotEquals(u1, new User("foo", Collections.singleton("role2")));
    }

    @Test
    public void testHashCode() {
        User u1 = new User("foo", Collections.singleton("role1"));
        assertEquals(u1.hashCode(), new User("foo", Collections.singleton("role1")).hashCode());
        assertNotEquals(u1.hashCode(), new User("bar", Collections.singleton("role1")).hashCode());
        assertNotEquals(u1.hashCode(), new User("foo", Collections.singleton("role2")).hashCode());

    }
}
