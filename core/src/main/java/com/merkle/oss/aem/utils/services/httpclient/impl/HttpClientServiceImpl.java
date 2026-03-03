package com.merkle.oss.aem.utils.services.httpclient.impl;

import com.adobe.granite.keystore.KeyStoreNotInitialisedException;
import com.adobe.granite.keystore.KeyStoreService;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientResponse;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientService;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientServiceErrorCode;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientServiceException;
import com.merkle.oss.aem.utils.services.resourceresolver.ResourceResolverService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;

/**
 * Implementation of {@link HttpClientService} using Apache HttpClient 4.x.
 * <p>
 * This implementation manages the creation of {@link org.apache.http.impl.client.CloseableHttpClient} instances on a per-request basis,
 * ensuring that SSL contexts are correctly applied from AEM's JCR-based keystores.
 *
 * @apiNote To prevent connection leaks, the response body is fully consumed into
 * a {@link java.io.ByteArrayInputStream} before the connection is closed and returned to the caller.
 * This ensures that the underlying HTTP connection is released back to the pool immediately.
 */
@Component(service = HttpClientService.class)
public class HttpClientServiceImpl implements HttpClientService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientServiceImpl.class);

    /**
     * Default timeout (in milliseconds) for establishing a connection with the remote host.
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    /**
     * Default timeout (in milliseconds) for waiting for data (packets) from the remote host.
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    @Reference
    private KeyStoreService keyStoreService;

    @Reference
    private ResourceResolverService resourceResolverService;

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpGet(@NonNull final HttpGet httpGet) throws IOException {
        return httpGet(httpGet, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpGet(@NonNull final HttpGet httpGet, final int connectionTimeout, final int socketTimeout) throws IOException {
        return execute(httpGet, connectionTimeout, socketTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpGetWithTrustStore(@NonNull final HttpGet httpGet) throws IOException {
        return httpGetWithTrustStore(httpGet, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpGetWithTrustStore(@NonNull final HttpGet httpGet, final int connectionTimeout, final int socketTimeout) throws IOException {
        return executeWithTrustStore(httpGet, connectionTimeout, socketTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpGetWithKeyStore(@NonNull final HttpGet httpGet, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword) throws IOException {
        return httpGetWithKeyStore(httpGet, keyStoreServiceUserId, keyStorePassword, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpGetWithKeyStore(@NonNull final HttpGet httpGet, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword, final int connectionTimeout, final int socketTimeout) throws IOException {
        return executeWithKeyStore(httpGet, keyStoreServiceUserId, keyStorePassword, connectionTimeout, socketTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpPost(@NonNull final HttpPost httpPost) throws IOException {
        return httpPost(httpPost, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpPost(@NonNull final HttpPost httpPost, final int connectionTimeout, final int socketTimeout) throws IOException {
        return execute(httpPost, connectionTimeout, socketTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpPostWithTrustStore(@NonNull final HttpPost httpPost) throws IOException {
        return httpPostWithTrustStore(httpPost, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpPostWithTrustStore(@NonNull final HttpPost httpPost, final int connectionTimeout, final int socketTimeout) throws IOException {
        return executeWithTrustStore(httpPost, connectionTimeout, socketTimeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpPostWithKeyStore(@NonNull final HttpPost httpPost, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword) throws IOException {
        return httpPostWithKeyStore(httpPost, keyStoreServiceUserId, keyStorePassword, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull HttpClientResponse httpPostWithKeyStore(@NonNull final HttpPost httpPost, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword, final int connectionTimeout, final int socketTimeout) throws IOException {
        return executeWithKeyStore(httpPost, keyStoreServiceUserId, keyStorePassword, connectionTimeout, socketTimeout);
    }

    /**
     * Executes a standard HTTP request without custom SSL context.
     *
     * @param request           to execute.
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException on http client execution failure.
     */
    private @NonNull HttpClientResponse execute(@NonNull final HttpUriRequest request, final int connectionTimeout, final int socketTimeout) throws IOException {
        LOG.debug("Http Client execution attempt for URI {}", request.getURI());

        final HttpClientBuilder httpClientBuilder = getHttpClientBuilder(connectionTimeout, socketTimeout);
        return buildAndExecuteClientRequest(httpClientBuilder, request);
    }

    /**
     * Executes a request using an SSL context built from AEM's global TrustStore.
     * <p>
     * Use this method for APIs protected by self-signed certificates that have been
     * uploaded to the global AEM Truststore.
     *
     * @param request           to execute.
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException                on http client execution failure.
     * @throws HttpClientServiceException HttpClientServiceException If the global TrustStore cannot be loaded, SSL algorithms
     *                                    are missing, or the SSL context initialization fails.
     */
    private @NonNull HttpClientResponse executeWithTrustStore(@NonNull final HttpUriRequest request, final int connectionTimeout, final int socketTimeout) throws IOException {
        LOG.debug("Http Client execution attempt for URI {}", request.getURI());

        try {
            final SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(loadAEMTrustStore(), null);

            final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);

            final HttpClientBuilder httpClientBuilder = getHttpClientBuilder(connectionTimeout, socketTimeout);
            httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);

            return buildAndExecuteClientRequest(httpClientBuilder, request);

        } catch (NoSuchAlgorithmException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.NO_SUCH_ALGORITHM, "Unable to load trust material.", e);
        } catch (KeyStoreException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.KEY_STORE, "Unable to load trust material.", e);
        } catch (KeyManagementException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.KEY_MANAGEMENT, "Failed to initialize the SSL connection factory.", e);
        }

    }

    /**
     * Retrieves the global AEM TrustStore using the internal ResourceResolver service.
     *
     * @return The {@link KeyStore} instance representing the global TrustStore
     * @throws HttpClientServiceException If the system user cannot be authenticated (LoginException)
     *                                    or if the KeyStore is in an uninitialized state.
     */
    private @Nullable KeyStore loadAEMTrustStore() {
        try (final ResourceResolver coreReader = resourceResolverService.createTruststoreReader()) {
            return keyStoreService.getTrustStore(coreReader);
        } catch (KeyStoreNotInitialisedException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.KEY_STORE_NOT_INITIALISED, "Unable to load global truststore.", e);
        } catch (LoginException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.LOGIN, "Unable to create trust store reader.", e);
        }
    }

    /**
     * Executes a request using a custom SSL context for Mutual TLS (mTLS) authentication.
     * <p>
     * This method loads client certificates from the specified system user's KeyStore
     * in AEM to authenticate the request against the remote server.
     * </p>
     *
     * @param request               to execute.
     * @param keyStoreServiceUserId of the service user to retrieve the key store from.
     * @param keyStorePassword      password of the user's key store.
     * @param connectionTimeout     The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout         The time (in milliseconds) allowed for data to be received.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException                on http client execution failure.
     * @throws HttpClientServiceException HttpClientServiceException If the KeyStore cannot be loaded, SSL algorithms
     *                                    are missing, or the SSL context initialization fails.
     */
    private @NonNull HttpClientResponse executeWithKeyStore(@NonNull final HttpUriRequest request, @NonNull final String keyStoreServiceUserId, @NonNull final String keyStorePassword, final int connectionTimeout, final int socketTimeout) throws IOException {
        LOG.debug("Http Client execution attempt for URI {}.", request.getURI());

        try {
            final SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadKeyMaterial(loadKeyStore(keyStoreServiceUserId), keyStorePassword.toCharArray());

            final SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);

            final HttpClientBuilder httpClientBuilder = getHttpClientBuilder(connectionTimeout, socketTimeout);
            httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);

            return buildAndExecuteClientRequest(httpClientBuilder, request);

        } catch (NoSuchAlgorithmException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.NO_SUCH_ALGORITHM, "Unable to load key material.", e);
        } catch (KeyStoreException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.KEY_STORE, "Unable to load key material.", e);
        } catch (UnrecoverableKeyException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.UNRECOVERABLE_KEY, "Unable to load key material.", e);
        } catch (KeyManagementException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.KEY_MANAGEMENT, "Failed to initialize the SSL connection factory.", e);
        }

    }

    /**
     * Retrieves the KeyStore associated with a specific AEM system user.
     *
     * @param serviceUserId The ID of the system user to retrieve the keystore from.
     * @return The {@link KeyStore} instance, or {@code null} if the keystore is not initialized for this user.
     * @throws HttpClientServiceException If the system user cannot be authenticated (LoginException)
     *                                    or if the KeyStore is in an uninitialized state.
     */
    private @Nullable KeyStore loadKeyStore(@NonNull final String serviceUserId) {
        try (final ResourceResolver trustStoreReader = resourceResolverService.createUsersReader()) {
            return keyStoreService.getKeyStore(trustStoreReader, serviceUserId);
        } catch (KeyStoreNotInitialisedException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.KEY_STORE_NOT_INITIALISED, "Unable to load keystore because keystore is not initialised for given service user id {}.", serviceUserId, e);
        } catch (LoginException e) {
            throw new HttpClientServiceException(HttpClientServiceErrorCode.LOGIN, "Unable to create users reader.", e);
        }
    }

    /**
     * Creates and configures an {@link org.apache.http.impl.client.HttpClientBuilder} with mandatory timeout settings.
     *
     * @param connectionTimeout The time (in milliseconds) allowed to establish the connection.
     * @param socketTimeout     The time (in milliseconds) allowed for data to be received.
     * @return A pre-configured HttpClientBuilder ready for SSL customization or execution.
     */
    private @NonNull HttpClientBuilder getHttpClientBuilder(final int connectionTimeout, final int socketTimeout) {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionTimeout)
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        return HttpClientBuilder.create().useSystemProperties().setDefaultRequestConfig(requestConfig);
    }

    /**
     * Orchestrates the lifecycle of the HTTP client and request execution.
     * <p>
     * This method builds the client, executes the request, and immediately consumes the
     * response entity into a byte array. This allows the {@code CloseableHttpClient}
     * and {@code CloseableHttpResponse} to be closed before the method returns,
     * effectively preventing connection leaks.
     *
     * @param httpClientBuilder The builder configured with SSL and timeout settings.
     * @param request           The actual HTTP request to be performed.
     * @return An {@link HttpClientResponse} containing the client response.
     * @throws IOException If the request fails or if the response entity cannot be read.
     */
    private @NonNull HttpClientResponse buildAndExecuteClientRequest(@NonNull final HttpClientBuilder httpClientBuilder, @NonNull final HttpUriRequest request) throws IOException {
        try (final CloseableHttpClient httpClient = httpClientBuilder.build()) {
            try (final CloseableHttpResponse response = httpClient.execute(request)) {
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    LOG.info("Executed request {} and received status {}", request.getURI(), response.getStatusLine().getStatusCode());
                    try (final InputStream inputStream = entity.getContent()) {
                        final byte[] bytes = IOUtils.toByteArray(inputStream);
                        return new HttpClientResponse(response.getStatusLine().getStatusCode(), new ByteArrayInputStream(bytes));
                    }
                }
            }
        }

        throw new IOException("Http Client execution attempt failed for URI " + request.getURI());
    }

}
