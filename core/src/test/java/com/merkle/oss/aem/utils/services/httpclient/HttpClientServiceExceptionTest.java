package com.merkle.oss.aem.utils.services.httpclient;

import com.merkle.oss.aem.utils.exceptions.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link HttpClientServiceException} class.
 */
class HttpClientServiceExceptionTest {

    /**
     * Method under test: {@link HttpClientServiceException#HttpClientServiceException(ErrorCode, String, Object...)}
     */
    @Test
    void testConstructorWithMessageAndArgs() {
        final String message = "Hello {}";
        final String arg = "World";
        final String expectedMessage = "[TEST_ERROR] Hello World";
        final HttpClientServiceException exception = new HttpClientServiceException(TestCode.TEST_ERROR, message, arg);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(TestCode.TEST_ERROR, exception.getErrorCode());
    }

    /**
     * Method under test: {@link HttpClientServiceException#HttpClientServiceException(ErrorCode, String, Object...)}
     */
    @Test
    void testConstructorWithCause() {
        final Exception cause = new RuntimeException("Original Error");
        final String message = "Failure at {}";
        final HttpClientServiceException exception = new HttpClientServiceException(TestCode.TEST_ERROR, message, "Endpoint", cause);

        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("Failure at Endpoint"));
        assertEquals(TestCode.TEST_ERROR, exception.getErrorCode());
    }

    /**
     * Method under test: {@link HttpClientServiceException#HttpClientServiceException(ErrorCode, String, Object...)}
     */
    @Test
    void testMessageFormattingWithMultipleArgs() {
        final HttpClientServiceException exception = new HttpClientServiceException(TestCode.TEST_ERROR, "Error {} occurred on {} attempts", "Timeout", 3);

        assertEquals("[TEST_ERROR] Error Timeout occurred on 3 attempts", exception.getMessage());
    }

    private enum TestCode implements ErrorCode {
        TEST_ERROR
    }

}
