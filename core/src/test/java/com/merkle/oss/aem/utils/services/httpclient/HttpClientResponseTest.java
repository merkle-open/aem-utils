package com.merkle.oss.aem.utils.services.httpclient;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link HttpClientResponse} class.
 */
class HttpClientResponseTest {

    private static final String MOCK_BODY = "{\"status\": \"success\"}";
    private static final int STATUS_OK = 200;

    /**
     * Method under test:
     * <ul>
     *   <li>{@link HttpClientResponse#HttpClientResponse(int, InputStream)}
     *   <li>{@link HttpClientResponse#getStatusCode()}
     *   <li>{@link HttpClientResponse#asString()}
     *   <li>{@link HttpClientResponse#getInputStream()}
     * </ul>
     */
    @Test
    void constructorAndGetters() throws IOException {
        final InputStream is = new ByteArrayInputStream(MOCK_BODY.getBytes(StandardCharsets.UTF_8));

        final HttpClientResponse response = new HttpClientResponse(STATUS_OK, is);

        assertEquals(STATUS_OK, response.getStatusCode(), "Status code should match");
        assertEquals(MOCK_BODY, response.asString(), "Response string should match input stream content");
        assertSame(is, response.getInputStream(), "Should return the same input stream instance");
    }

    /**
     * Method under test: {@link HttpClientResponse#HttpClientResponse(int, InputStream)}
     */
    @Test
    void constructor_null() {
        assertThrows(NullPointerException.class, () -> new HttpClientResponse(STATUS_OK, null));
    }

    /**
     * Method under test: {@link HttpClientResponse#getInputStream()}
     */
    @Test
    void streamConsumption() throws IOException {
        final InputStream is = new ByteArrayInputStream(MOCK_BODY.getBytes(StandardCharsets.UTF_8));
        final HttpClientResponse response = new HttpClientResponse(STATUS_OK, is);
        assertEquals(0, response.getInputStream().available(), "Stream should have 0 bytes available after consumption");

        response.getInputStream().reset();
        String reReadBody = IOUtils.toString(response.getInputStream(), StandardCharsets.UTF_8);
        assertEquals(MOCK_BODY, reReadBody, "Stream should be readable again after manual reset");
    }

    /**
     * Method under test: {@link HttpClientResponse#asString()}
     */
    @Test
    void asStringPersistence() throws IOException {
        final InputStream is = new ByteArrayInputStream(MOCK_BODY.getBytes(StandardCharsets.UTF_8));
        final HttpClientResponse response = new HttpClientResponse(STATUS_OK, is);
        final String firstCall = response.asString();
        final String secondCall = response.asString();

        assertEquals(firstCall, secondCall, "Multiple calls to asString should return identical content");
        assertSame(firstCall, secondCall, "Multiple calls should return the same String object instance");
    }

}
