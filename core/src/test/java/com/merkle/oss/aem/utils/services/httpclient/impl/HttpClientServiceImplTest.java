package com.merkle.oss.aem.utils.services.httpclient.impl;

import com.adobe.granite.keystore.KeyStoreNotInitialisedException;
import com.adobe.granite.keystore.KeyStoreService;
import com.merkle.oss.aem.utils.services.httpclient.HttpClientResponse;
import com.merkle.oss.aem.utils.services.resourceresolver.ResourceResolverService;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link HttpClientServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class HttpClientServiceImplTest {

    @Mock
    private KeyStoreService keyStoreService;

    @Mock
    private ResourceResolverService resourceResolverService;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private KeyStore keyStore;

    @Mock
    private CloseableHttpClient closeableHttpClient;

    @Mock
    private CloseableHttpResponse closeableHttpResponse;

    @Mock
    private StatusLine statusLine;

    @Mock
    private HttpEntity httpEntity;

    @Mock
    private HttpClientBuilder httpClientBuilder;

    @InjectMocks
    private HttpClientServiceImpl httpClientService;

    @BeforeEach
    void setUp() {
        lenient().when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
        lenient().when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
        lenient().when(httpClientBuilder.setDefaultRequestConfig(any(RequestConfig.class))).thenReturn(httpClientBuilder);
        lenient().when(httpClientBuilder.useSystemProperties()).thenReturn(httpClientBuilder);
        lenient().when(httpClientBuilder.setSSLSocketFactory(any())).thenReturn(httpClientBuilder);
        lenient().when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGet(HttpGet)}
     */
    @Test
    void httpGet() throws IOException {
        final String expectedResponse = "Success Response";
        final int expectedStatus = 200;

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            when(statusLine.getStatusCode()).thenReturn(expectedStatus);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8)));
            when(closeableHttpClient.execute(any(HttpUriRequest.class))).thenReturn(closeableHttpResponse);

            final HttpClientResponse response = httpClientService.httpGet(new HttpGet("http://example.com"));
            assertNotNull(response);
            assertEquals(expectedStatus, response.getStatusCode());
            assertEquals(expectedResponse, response.asString());
            verify(closeableHttpClient).close();
            verify(closeableHttpResponse).close();
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpPost(HttpPost)}
     */
    @Test
    void httpPost() throws IOException {
        final String expectedResponse = "Created";
        final int expectedStatus = 201;

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            when(statusLine.getStatusCode()).thenReturn(expectedStatus);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(expectedResponse.getBytes(StandardCharsets.UTF_8)));
            when(closeableHttpClient.execute(any(HttpUriRequest.class))).thenReturn(closeableHttpResponse);

            final HttpClientResponse response = httpClientService.httpPost(new HttpPost("http://example.com/api"));
            assertEquals(expectedStatus, response.getStatusCode());
            assertEquals(expectedResponse, response.asString());
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithTrustStore(HttpGet)}
     */
    @Test
    void httpGetWithTrustStore() throws Exception {
        when(resourceResolverService.createTruststoreReader()).thenReturn(resourceResolver);
        when(keyStoreService.getTrustStore(resourceResolver)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadTrustMaterial(any(KeyStore.class), any())).thenReturn(mockBuilder);
                         SSLContext mockSslContext = mock(SSLContext.class);
                         when(mockSslContext.getSocketFactory()).thenReturn(mock(javax.net.ssl.SSLSocketFactory.class));
                         when(mockBuilder.build()).thenReturn(mockSslContext);
                     })) {

            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);

            when(statusLine.getStatusCode()).thenReturn(200);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream("OK".getBytes()));
            when(closeableHttpClient.execute(any(HttpGet.class))).thenReturn(closeableHttpResponse);

            httpClientService.httpGetWithTrustStore(new HttpGet("https://secure.example.com"));
            verify(keyStoreService).getTrustStore(resourceResolver);
            verify(httpClientBuilder).setSSLSocketFactory(any(SSLConnectionSocketFactory.class));

            final SSLContextBuilder createdSslBuilder = mockSslBuilder.constructed().getFirst();
            verify(createdSslBuilder).loadTrustMaterial(eq(keyStore), any());
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpPostWithTrustStore(HttpPost)}
     */
    @Test
    void httpPostWithTrustStore() throws Exception {
        when(resourceResolverService.createTruststoreReader()).thenReturn(resourceResolver);
        when(keyStoreService.getTrustStore(resourceResolver)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadTrustMaterial(any(KeyStore.class), any())).thenReturn(mockBuilder);
                         SSLContext mockSslContext = mock(SSLContext.class);
                         when(mockSslContext.getSocketFactory()).thenReturn(mock(javax.net.ssl.SSLSocketFactory.class));
                         when(mockBuilder.build()).thenReturn(mockSslContext);
                     })) {

            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);

            when(statusLine.getStatusCode()).thenReturn(200);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream("OK".getBytes()));
            when(closeableHttpClient.execute(any(HttpPost.class))).thenReturn(closeableHttpResponse);

            httpClientService.httpPostWithTrustStore(new HttpPost("https://secure.example.com"));
            verify(keyStoreService).getTrustStore(resourceResolver);
            verify(httpClientBuilder).setSSLSocketFactory(any(SSLConnectionSocketFactory.class));

            final SSLContextBuilder createdSslBuilder = mockSslBuilder.constructed().getFirst();
            verify(createdSslBuilder).loadTrustMaterial(eq(keyStore), any());
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithKeyStore(HttpGet, String, String)}
     */
    @Test
    void httpGetWithKeyStore() throws Exception {
        final String userId = "service-user";
        final String password = "password";

        when(resourceResolverService.createUsersReader()).thenReturn(resourceResolver);
        when(keyStoreService.getKeyStore(resourceResolver, userId)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadKeyMaterial(any(KeyStore.class), any(char[].class))).thenReturn(mockBuilder);
                         javax.net.ssl.SSLContext mockSslContext = mock(javax.net.ssl.SSLContext.class);
                         when(mockSslContext.getSocketFactory()).thenReturn(mock(javax.net.ssl.SSLSocketFactory.class));
                         when(mockBuilder.build()).thenReturn(mockSslContext);
                     })) {

            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);

            when(statusLine.getStatusCode()).thenReturn(200);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream("OK".getBytes()));
            when(closeableHttpClient.execute(any(HttpGet.class))).thenReturn(closeableHttpResponse);

            httpClientService.httpGetWithKeyStore(new HttpGet("https://mtls.example.com"), userId, password);
            verify(keyStoreService).getKeyStore(resourceResolver, userId);
            verify(httpClientBuilder).setSSLSocketFactory(any(SSLConnectionSocketFactory.class));

            final SSLContextBuilder createdSslBuilder = mockSslBuilder.constructed().getFirst();
            verify(createdSslBuilder).loadKeyMaterial(eq(keyStore), eq(password.toCharArray()));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpPostWithKeyStore(HttpPost, String, String)}
     */
    @Test
    void httpPostWithKeyStore() throws Exception {
        final String userId = "service-user";
        final String password = "password";

        when(resourceResolverService.createUsersReader()).thenReturn(resourceResolver);
        when(keyStoreService.getKeyStore(resourceResolver, userId)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadKeyMaterial(any(KeyStore.class), any(char[].class))).thenReturn(mockBuilder);
                         javax.net.ssl.SSLContext mockSslContext = mock(javax.net.ssl.SSLContext.class);
                         when(mockSslContext.getSocketFactory()).thenReturn(mock(javax.net.ssl.SSLSocketFactory.class));
                         when(mockBuilder.build()).thenReturn(mockSslContext);
                     })) {

            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);

            when(statusLine.getStatusCode()).thenReturn(200);
            when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream("OK".getBytes()));
            when(closeableHttpClient.execute(any(HttpPost.class))).thenReturn(closeableHttpResponse);
            httpClientService.httpPostWithKeyStore(new HttpPost("https://mtls.example.com"), userId, password);

            verify(keyStoreService).getKeyStore(resourceResolver, userId);
            verify(httpClientBuilder).setSSLSocketFactory(any(SSLConnectionSocketFactory.class));

            final SSLContextBuilder createdSslBuilder = mockSslBuilder.constructed().getFirst();
            verify(createdSslBuilder).loadKeyMaterial(eq(keyStore), eq(password.toCharArray()));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithTrustStore(HttpGet)}
     */
    @Test
    void httpGetWithTrustStore_failureLogin() throws Exception {
        when(resourceResolverService.createTruststoreReader()).thenThrow(new org.apache.sling.api.resource.LoginException("Simulated Login Failure"));

        try (MockedConstruction<SSLContextBuilder> ignored = mockConstruction(SSLContextBuilder.class)) {
            final SecurityException exception = assertThrows(SecurityException.class, () -> httpClientService.httpGetWithTrustStore(new HttpGet("https://example.com")));
            assertInstanceOf(LoginException.class, exception.getCause());
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithTrustStore(HttpGet)}
     */
    @Test
    void httpGetWithTrustStore_failureKeyMgmt() throws Exception {
        when(resourceResolverService.createTruststoreReader()).thenReturn(resourceResolver);
        when(keyStoreService.getTrustStore(resourceResolver)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadTrustMaterial(any(KeyStore.class), any())).thenReturn(mockBuilder);
                         when(mockBuilder.build()).thenThrow(KeyManagementException.class);
                     })) {

            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            assertThrows(SecurityException.class, () -> httpClientService.httpGetWithTrustStore(new HttpGet("https://secure.example.com")));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithTrustStore(HttpGet)}
     */
    @Test
    void httpGetWithTrustStore_failureTrustStore() throws Exception {
        when(resourceResolverService.createTruststoreReader()).thenReturn(resourceResolver);
        when(keyStoreService.getTrustStore(resourceResolver)).thenThrow(KeyStoreNotInitialisedException.class);

        assertThrows(UnknownHostException.class, () -> httpClientService.httpGetWithTrustStore(new HttpGet("https://secure.example.com")));
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithKeyStore(HttpGet, String, String, int, int)}
     */
    @Test
    void httpGetWithKeyStore_failureKeyMgmt() throws Exception {
        final String userId = "service-user";
        final String password = "password";

        when(resourceResolverService.createUsersReader()).thenReturn(resourceResolver);
        when(keyStoreService.getKeyStore(resourceResolver, userId)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadKeyMaterial(any(KeyStore.class), any(char[].class))).thenReturn(mockBuilder);
                         when(mockBuilder.build()).thenThrow(KeyManagementException.class);
                     })) {


            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            assertThrows(SecurityException.class, () -> httpClientService.httpGetWithKeyStore(new HttpGet("https://secure.example.com"), userId, password));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithKeyStore(HttpGet, String, String, int, int)}
     */
    @Test
    void httpGetWithKeyStore_failureKeyMaterial() throws Exception {
        final String userId = "service-user";
        final String password = "password";

        when(resourceResolverService.createUsersReader()).thenReturn(resourceResolver);
        when(keyStoreService.getKeyStore(resourceResolver, userId)).thenReturn(keyStore);

        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class);
             MockedConstruction<SSLContextBuilder> mockSslBuilder = mockConstruction(SSLContextBuilder.class,
                     (mockBuilder, context) -> {
                         when(mockBuilder.loadKeyMaterial(any(KeyStore.class), any(char[].class))).thenThrow(UnrecoverableKeyException.class);
                         when(mockBuilder.build()).thenThrow(KeyManagementException.class);
                     })) {


            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            assertThrows(SecurityException.class, () -> httpClientService.httpGetWithKeyStore(new HttpGet("https://secure.example.com"), userId, password));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithKeyStore(HttpGet, String, String, int, int)}
     */
    @Test
    void httpGetWithKeyStore_failureKeyStore() throws Exception {
        final String userId = "service-user";
        final String password = "password";

        when(resourceResolverService.createUsersReader()).thenReturn(resourceResolver);
        when(keyStoreService.getKeyStore(resourceResolver, userId)).thenThrow(KeyStoreNotInitialisedException.class);

        assertThrows(UnknownHostException.class, () -> httpClientService.httpGetWithKeyStore(new HttpGet("https://secure.example.com"), userId, password));
    }

    /**
     * Method under test: {@link HttpClientServiceImpl#httpGetWithKeyStore(HttpGet, String, String, int, int)}
     */
    @Test
    void httpGetWithKeyStore_failureLogin() throws Exception {
        final String userId = "service-user";
        final String password = "password";

        when(resourceResolverService.createUsersReader()).thenThrow(new org.apache.sling.api.resource.LoginException("Simulated Login Failure"));

        try (MockedConstruction<SSLContextBuilder> ignored = mockConstruction(SSLContextBuilder.class)) {
            final SecurityException exception = assertThrows(SecurityException.class, () -> httpClientService.httpGetWithKeyStore(new HttpGet("https://example.com"), userId, password));
            assertInstanceOf(LoginException.class, exception.getCause());
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl@execute(HttpUriRequest, int, int)}
     */
    @Test
    void execute_error() throws IOException {
        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            when(closeableHttpClient.execute(any(HttpUriRequest.class))).thenThrow(new IOException("Network down"));
            final IOException exception = assertThrows(IOException.class, () -> httpClientService.httpGet(new HttpGet("http://example.com")));
            assertTrue(exception.getMessage().contains("Network down"));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl@execute(HttpUriRequest, int, int)}
     */
    @Test
    void execute_entityNull() throws IOException {
        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            when(closeableHttpResponse.getEntity()).thenReturn(null);
            when(closeableHttpClient.execute(any(HttpUriRequest.class))).thenReturn(closeableHttpResponse);
            assertThrows(IOException.class, () -> httpClientService.httpGet(new HttpGet("http://example.com")));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl@execute(HttpUriRequest, int, int)}
     */
    @Test
    void execute_builderNull() {
        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            when(httpClientBuilder.build()).thenReturn(null);
            assertThrows(NullPointerException.class, () -> httpClientService.httpGet(new HttpGet("http://example.com")));
        }
    }

    /**
     * Method under test: {@link HttpClientServiceImpl@execute(HttpUriRequest, int, int)}
     */
    @Test
    void execute_clientNull() throws IOException {
        try (MockedStatic<HttpClientBuilder> staticHttpClientBuilder = mockStatic(HttpClientBuilder.class)) {
            staticHttpClientBuilder.when(HttpClientBuilder::create).thenReturn(httpClientBuilder);
            when(closeableHttpClient.execute(any(HttpUriRequest.class))).thenReturn(null);
            assertThrows(NullPointerException.class, () -> httpClientService.httpGet(new HttpGet("http://example.com")));
        }
    }

}
