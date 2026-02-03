package com.merkle.oss.aem.utils.services.httpclient;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

/**
 * A centralized service for executing HTTP requests with specialized support for AEM security contexts.
 * <p>
 * This service abstracts the boilerplate of Apache HttpClient management and provides built-in
 * integration with AEM's global Truststore and individual Service User Keystores for secure
 * MTLS (Mutual TLS) communication.
 * </p>
 */
public interface HttpClientService {
    /**
     * Executes a GET request using Adobes recommended default timeouts.
     *
     * @param httpGet The GET request to execute.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException If the request fails due to network or execution issues.
     */
    @NonNull HttpClientResponse httpGet(@NonNull final HttpGet httpGet) throws IOException;

    /**
     * Executes a GET request with configurable timeouts.
     *
     * @param httpGet           The GET request to execute.
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on execution failure.
     */
    @NonNull HttpClientResponse httpGet(@NonNull final HttpGet httpGet, final int connectionTimeout, final int socketTimeout) throws IOException;

    /**
     * Executes a GET request with SSL Context of AEM's global trust store
     * using Adobes recommended default timeouts
     *
     * @param httpGet The GET request to execute.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on execution failure.
     */
    @NonNull HttpClientResponse httpGetWithTrustStore(@NonNull final HttpGet httpGet) throws IOException;

    /**
     * Executes a GET request with SSL Context of AEM's global trust store
     * with configurable timeouts.
     *
     * @param httpGet           The GET request to execute.
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpGetWithTrustStore(@NonNull final HttpGet httpGet, final int connectionTimeout, final int socketTimeout) throws IOException;


    /**
     * Executes a GET request with SSL Context of an AEM service user's key store
     * using Adobes recommended default timeouts
     *
     * @param httpGet               The GET request to execute.
     * @param keyStoreServiceUserId of the service user to retrieve the key store from.
     * @param keyStorePassword      password of the user's key store.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpGetWithKeyStore(@NonNull final HttpGet httpGet, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword) throws IOException;

    /**
     * Executes a GET request with SSL Context of an AEM service user's key store
     * with configurable timeouts.
     *
     * @param httpGet               The GET request to execute.
     * @param keyStoreServiceUserId of the service user to retrieve the key store from.
     * @param keyStorePassword      password of the user's key store.
     * @param connectionTimeout     The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout         The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpGetWithKeyStore(@NonNull final HttpGet httpGet, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword, final int connectionTimeout, final int socketTimeout) throws IOException;

    /**
     * Executes a POST request using Adobes recommended default timeouts.
     *
     * @param httpPost The POST request to execute.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpPost(@NonNull final HttpPost httpPost) throws IOException;

    /**
     * Executes a POST request with configurable timeouts.
     *
     * @param httpPost          The POST request to execute.
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpPost(@NonNull final HttpPost httpPost, final int connectionTimeout, final int socketTimeout) throws IOException;

    /**
     * Executes a POST request with SSL Context of AEM's global trust store
     * using Adobes recommended default timeouts
     *
     * @param httpPost The POST request to execute.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpPostWithTrustStore(@NonNull final HttpPost httpPost) throws IOException;

    /**
     * Executes a POST request with SSL Context of AEM's global trust store
     * with configurable timeouts.
     *
     * @param httpPost          The POST request to execute.
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpPostWithTrustStore(@NonNull final HttpPost httpPost, final int connectionTimeout, final int socketTimeout) throws IOException;

    /**
     * Executes a POST request with SSL Context of an AEM service user's key store
     * using Adobes recommended default timeouts
     *
     * @param httpPost              The POST request to execute.
     * @param keyStoreServiceUserId of the service user to retrieve the key store from.
     * @param keyStorePassword      password of the user's key store.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpPostWithKeyStore(@NonNull final HttpPost httpPost, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword) throws IOException;

    /**
     * Executes a POST request with SSL Context of an AEM service user's key store
     * with configurable timeouts.
     *
     * @param httpPost              The POST request to execute.
     * @param keyStoreServiceUserId of the service user to retrieve the key store from.
     * @param keyStorePassword      password of the user's key store.
     * @param connectionTimeout     The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout         The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    @NonNull HttpClientResponse httpPostWithKeyStore(@NonNull final HttpPost httpPost, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword, final int connectionTimeout, final int socketTimeout) throws IOException;

}
