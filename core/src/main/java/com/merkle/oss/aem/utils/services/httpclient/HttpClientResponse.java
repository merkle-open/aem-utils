package com.merkle.oss.aem.utils.services.httpclient;

import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Represents a memory-buffered HTTP response returned by the {@link HttpClientService}.
 * <p>
 * This class captures the HTTP status code and the response body. Upon construction,
 * the provided {@link InputStream} is fully consumed and converted into a UTF-8 String.
 * This allows the calling service to close the network connection immediately while
 * preserving the response data for the consumer.
 * </p>
 */
public class HttpClientResponse {

    private final int statusCode;

    private final InputStream inputStream;

    private final String responseString;

    /**
     * Constructs a new {@code HttpClientResponse} by consuming the provided input stream.
     * <p>
     * The input stream is read to completion and converted to a String using
     * {@link StandardCharsets#UTF_8}.
     * </p>
     *
     * @param statusCode  The HTTP status code received from the server (e.g., 200, 404).
     * @param inputStream The {@link InputStream} containing the raw response body. Must not be null.
     * @throws IOException          If an error occurs while reading the stream or converting it to a string.
     * @throws NullPointerException if the {@code inputStream} is null.
     */
    public HttpClientResponse(int statusCode, @NonNull final InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);

        this.statusCode = statusCode;
        this.inputStream = inputStream;
        this.responseString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Gets the HTTP status code associated with this response.
     *
     * @return The integer status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the raw input stream of the response body.
     *
     * @return The {@link InputStream} of the response body.
     * @apiNote Because the stream is consumed during the construction of this object,
     * the behavior of this stream depends on the implementation of the {@code inputStream}
     * passed to the constructor (e.g., if a {@link java.io.ByteArrayInputStream} was used,
     * it may be reset or re-read depending on the caller's logic).
     */
    public @NonNull InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Returns the response body as a UTF-8 encoded String.
     * <p>
     * This method returns the pre-consumed string generated during instantiation,
     * making it highly efficient for multiple calls.
     *
     * @return The response body as a String.
     */
    public @NonNull String asString() {
        return responseString;
    }

}
