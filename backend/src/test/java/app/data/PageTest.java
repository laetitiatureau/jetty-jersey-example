package app.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PageTest {
    @Test
    public void testEquals() {
        final Page page1 = new Page("1", true);

        assertEquals(page1, page1);
        assertNotEquals(page1,null);
        assertNotEquals(page1, "foo");
        assertEquals(page1, new Page(page1.getName(), page1.isActive()));
        assertNotEquals(page1, new Page("2", page1.isActive()));
        assertNotEquals(page1, new Page("2", !page1.isActive()));
        assertNotEquals(page1, new Page(page1.getName(), !page1.isActive()));
    }
}
