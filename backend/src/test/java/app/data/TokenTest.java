package app.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TokenTest {
    @Test
    public void testEquals() {
        Token t = new Token("foo", "bar");

        assertEquals(t, t);
        assertEquals(t, new Token("foo", "bar"));

        assertNotEquals(t, null);
        assertNotEquals(t, new Object());
        assertNotEquals(t, new Token("bar", "bar"));
        assertNotEquals(t, new Token("foo", "foo"));
    }

    @Test
    public void testHashCode() {
        Token t = new Token("foo", "bar");
        assertEquals(t.hashCode(), new Token("foo", "bar").hashCode());
        assertNotEquals(t.hashCode(), new Token("foo1", "bar").hashCode());
        assertNotEquals(t.hashCode(), new Token("foo", "bar1").hashCode());
    }
}
