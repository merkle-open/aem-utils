package com.merkle.oss.aem.utils.jcr;

import com.merkle.oss.aem.utils.exceptions.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for the {@link PermissionException} class.
 */
class PermissionExceptionTest {

    /**
     * Method under test: {@link PermissionException#PermissionException(ErrorCode, String, Object...)}
     */
    @Test
    void testPermissionFormatting() {
        final String message = "User {} is missing {} permission on path {}";
        final String user = "anonymous";
        final String action = "replicate";
        final String path = "/content/dam";

        final PermissionException exception = new PermissionException(TestErrorCode.ACCESS_DENIED, message, user, action, path);
        final String expected = "[ACCESS_DENIED] User anonymous is missing replicate permission on path /content/dam";

        assertEquals(expected, exception.getMessage());
        assertEquals(TestErrorCode.ACCESS_DENIED, exception.getErrorCode());
    }

    /**
     * Method under test: {@link PermissionException#PermissionException(ErrorCode, String, Object...)}
     */
    @Test
    void testPermissionWithCause() {
        final Exception cause = new SecurityException("ACL Check failed");
        final String path = "/var/audit";

        final PermissionException exception = new PermissionException(TestErrorCode.ACCESS_DENIED, "Restricted access to {}", path, cause);

        assertEquals("[ACCESS_DENIED] Restricted access to /var/audit", exception.getMessage());
        assertSame(cause, exception.getCause(), "The root cause should be preserved in the exception chain");
    }

    /**
     * Method under test: {@link PermissionException#PermissionException(ErrorCode, String, Object...)}
     */
    @Test
    void testMissingArguments() {
        final PermissionException exception = new PermissionException(TestErrorCode.ACCESS_DENIED, "Permission denied for {}");

        assertEquals("[ACCESS_DENIED] Permission denied for {}", exception.getMessage());
    }

    private enum TestErrorCode implements ErrorCode {
        ACCESS_DENIED
    }

}