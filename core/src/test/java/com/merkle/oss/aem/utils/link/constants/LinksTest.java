package com.merkle.oss.aem.utils.link.constants;

import com.merkle.oss.aem.utils.link.LinkExternalizerUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Links} class.
 */
public class LinksTest {

    /**
     * Method under test: {@link Links}
     */
    @Test
    void instantiationException() throws NoSuchMethodException {
        final Constructor<Links> constructor = Links.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertEquals(AssertionError.class, exception.getCause().getClass());
        assertTrue(exception.getCause().getMessage().contains("not meant to be instantiated"));
    }

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
     *   <li>{@link Links.Target#getTarget()}
     *   <li>{@link Links.Target#getRel()}
     *   <li>{@link Links.Target#toString()}
     * </ul>
     */
    @Test
    void target_gettersAndSetters() {
        final Links.Target target = Links.Target.valueOf("BLANK");

        assertEquals("_blank", target.getTarget());
        assertEquals("noopener", target.getRel());
        assertEquals("_blank", target.toString());

        final Links.Target targetNone = Links.Target.valueOf("NONE");

        assertEquals("", targetNone.getTarget());
        assertEquals("", targetNone.getRel());
        assertEquals("", targetNone.toString());
    }

}
