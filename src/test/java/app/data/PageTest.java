package app.data;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by ak on 14/07/17.
 */
public class PageTest {
    @Test
    public void testEquals() {
        Page page1 = new Page();
        page1.setName("1");
        page1.setActive(true);

        assertTrue(page1.equals(page1));
        assertFalse(page1.equals(null));
        assertFalse(page1.equals("foo"));

        Page page2 = new Page("1", true);

        assertTrue(page1.equals(page2));

        Page page3 = new Page("2", true);

        assertFalse(page1.equals(page3));

        Page page4 = new Page("1", false);

        assertFalse(page1.equals(page4));
    }
}
