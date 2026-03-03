package com.merkle.oss.aem.utils.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SystemException} class.
 */
class SystemExceptionTest {

    /**
     * Method under test: {@link SystemException#SystemException(ErrorCode, String, Object...)}
     */
    @Test
    void testMessageFormatting() {
        final String pattern = "User {} failed to perform action on {}";
        final String arg1 = "admin";
        final String arg2 = "/content/page";

        final SystemException exception = new SystemException(TestErrorCode.TEST_ERROR, pattern, arg1, arg2);

        assertEquals("[TEST_ERROR] User admin failed to perform action on /content/page", exception.getMessage());
        assertEquals(TestErrorCode.TEST_ERROR, exception.getErrorCode());
    }

    /**
     * Method under test: {@link SystemException#SystemException(ErrorCode, String, Object...)}
     */
    @Test
    void testWithCause() {
        final Exception cause = new RuntimeException("Original DB Error");
        final SystemException exception = new SystemException(TestErrorCode.TEST_ERROR, "Database failure in {}", "Production", cause);

        assertEquals("[TEST_ERROR] Database failure in Production", exception.getMessage());
        assertSame(cause, exception.getCause());

        final SystemException exception2 = new SystemException(TestErrorCode.TEST_ERROR, "Database failure in {} for resource {}", "Production", "Resource", cause);
        assertEquals("[TEST_ERROR] Database failure in Production for resource Resource", exception2.getMessage());

        final SystemException exception3 = new SystemException(TestErrorCode.TEST_ERROR, "Database {} failure in {} for resource {}", "Production", "Resource", cause);
        assertEquals("[TEST_ERROR] Database Production failure in Resource for resource {}", exception3.getMessage());

        final SystemException exception4 = new SystemException(TestErrorCode.TEST_ERROR, "Database failure in {} for resource", "Production", "Resource", cause);
        assertEquals("[TEST_ERROR] Database failure in Production for resource", exception4.getMessage());
    }

    /**
     * Method under test: {@link SystemException#SystemException(ErrorCode, String, Object...)}
     */
    @Test
    void testThrowableInMiddle() {
        final Exception cause = new RuntimeException("Original DB Error");
        final SystemException exception = new SystemException(TestErrorCode.TEST_ERROR, "Error: {} at path {}", cause, "/content/dam");

        assertTrue(exception.getMessage().contains("java.lang.RuntimeException: Original DB Error"));
        assertTrue(exception.getMessage().contains("at path /content/dam"));
        assertNull(exception.getCause());
    }

    /**
     * Method under test: {@link SystemException#SystemException(ErrorCode, String, Object...)}
     */
    @Test
    void testEmptyArgs() {
        final SystemException exception = new SystemException(TestErrorCode.TEST_ERROR, "Static message");

        assertEquals("[TEST_ERROR] Static message", exception.getMessage());
    }

    /**
     * Method under test: {@link SystemException#SystemException(ErrorCode, String, Object...)}
     */
    @Test
    void testEscapedBraces() {
        final SystemException exception = new SystemException(TestErrorCode.TEST_ERROR, "Use \\{} for literal braces and {} for substitution", "replaced");

        assertEquals("[TEST_ERROR] Use {} for literal braces and replaced for substitution", exception.getMessage());
    }

    /**
     * Method under test: {@link SystemException#SystemException(ErrorCode, String, Object...)}
     */
    @Test
    void testArgumentMismatch() {
        final SystemException exception = new SystemException(TestErrorCode.TEST_ERROR, "Placeholder {}, but no arg");

        assertEquals("[TEST_ERROR] Placeholder {}, but no arg", exception.getMessage());
    }

    private enum TestErrorCode implements ErrorCode {
        TEST_ERROR
    }

}