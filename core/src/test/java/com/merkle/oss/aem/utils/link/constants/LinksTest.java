package com.merkle.oss.aem.utils.link.constants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Links} class.
 */
class LinksTest {

    /**
     * Method under test: {@link Links.Target#of(String)}
     */
    @Test
    void target_testOf() {
        assertEquals(Links.Target.NONE, Links.Target.of(null));
        assertEquals(Links.Target.NONE, Links.Target.of(""));
        assertEquals(Links.Target.BLANK, Links.Target.of("_blank"));
    }

    /**
     * Method under test: {@link Links.Target#isOpenInNewWindow(String)}
     */
    @Test
    void target_isOpenInNewWindow() {
        assertTrue(Links.Target.isOpenInNewWindow("_blank"));
        assertFalse(Links.Target.isOpenInNewWindow(""));
        assertFalse(Links.Target.isOpenInNewWindow(null));
        assertFalse(Links.Target.isOpenInNewWindow("_self"));
        assertFalse(Links.Target.isOpenInNewWindow("_parent"));
        assertFalse(Links.Target.isOpenInNewWindow("_top"));
        assertFalse(Links.Target.isOpenInNewWindow("download"));
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link Links.Target#getValue()}</li>
     *   <li>{@link Links.Target#getRel()}</li>
     *   <li>{@link Links.Target#toString()}</li>
     * </ul>
     */
    @Test
    void target_gettersAndSetters() {
        final Links.Target target = Links.Target.valueOf("BLANK");

        assertEquals("_blank", target.getValue());
        assertEquals("noopener", target.getRel());
        assertEquals("_blank", target.toString());

        final Links.Target targetNone = Links.Target.valueOf("NONE");

        assertEquals("", targetNone.getValue());
        assertEquals("", targetNone.getRel());
        assertEquals("", targetNone.toString());
    }

}
