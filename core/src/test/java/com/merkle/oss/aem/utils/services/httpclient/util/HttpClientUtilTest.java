package com.merkle.oss.aem.utils.services.httpclient.util;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link HttpClientUtil} class.
 */
class HttpClientUtilTest {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    // "admin:password" in Base64 is "YWRtaW46cGFzc3dvcmQ="
    private static final String EXPECTED_BASIC_ENCODED = "YWRtaW46cGFzc3dvcmQ=";
    private static final String TOKEN = "my-secret-bearer-token";

    /**
     * Method under test: {@link HttpClientUtil#buildBasicAuthenticationHeader(String, String)}
     */
    @Test
    void buildBasicAuthenticationHeader_withCredentials() {
        final Header header = HttpClientUtil.buildBasicAuthenticationHeader(USERNAME, PASSWORD);

        assertEquals(HttpHeaders.AUTHORIZATION, header.getName(), "Header name must be Authorization");
        assertEquals("Basic " + EXPECTED_BASIC_ENCODED, header.getValue(), "Value should be 'Basic ' followed by Base64(user:pass)");
    }

    /**
     * Method under test: {@link HttpClientUtil#buildBasicAuthenticationHeader(String)}
     */
    @Test
    void buildBasicAuthenticationHeader_withKey() {
        final Header header = HttpClientUtil.buildBasicAuthenticationHeader(EXPECTED_BASIC_ENCODED);

        assertEquals(HttpHeaders.AUTHORIZATION, header.getName());
        assertEquals("Basic " + EXPECTED_BASIC_ENCODED, header.getValue());
    }

    /**
     * Method under test: {@link HttpClientUtil#buildBasicAuthenticationHeader(String, String)}
     */
    @ParameterizedTest
    @CsvSource({
            "null, password",
            "admin, null",
            "null, null"
    })
    void buildBasicAuthenticationHeader_nullChecks(final String user, final String pass) {
        final String u = "null".equals(user) ? null : user;
        final String p = "null".equals(pass) ? null : pass;

        assertThrows(NullPointerException.class, () -> HttpClientUtil.buildBasicAuthenticationHeader(u, p));
    }

    /**
     * Method under test: {@link HttpClientUtil#buildBasicAuthenticationHeader(String)}
     */
    @Test
    void buildBasicAuthenticationHeader_null() {
        assertThrows(NullPointerException.class, () -> HttpClientUtil.buildBasicAuthenticationHeader(null));
    }

    /**
     * Method under test: {@link HttpClientUtil#buildBearerAuthenticationHeader(String)}
     */
    @Test
    void buildBearerAuthenticationHeader() {
        final Header header = HttpClientUtil.buildBearerAuthenticationHeader(TOKEN);

        assertEquals(HttpHeaders.AUTHORIZATION, header.getName());
        assertEquals("Bearer " + TOKEN, header.getValue(), "Value should be 'Bearer ' followed by the token");
    }

    /**
     * Method under test: {@link HttpClientUtil#buildBearerAuthenticationHeader(String)}
     */
    @Test
    void buildBearerAuthenticationHeader_null() {
        assertThrows(NullPointerException.class, () -> HttpClientUtil.buildBearerAuthenticationHeader(null));
    }

}
