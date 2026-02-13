package com.merkle.oss.aem.utils.services.httpclient;

import com.merkle.oss.aem.utils.annotations.tooling.Generated;
import com.merkle.oss.aem.utils.java.ClassUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.jspecify.annotations.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Static utility class providing helper methods for common HTTP client operations.
 */
public final class HttpClientUtil {

    /**
     * The standard prefix for Basic Authentication: "Basic".
     */
    public static final String BASIC_AUTH_SCHEME = "Basic";

    /**
     * The standard prefix for Bearer Token Authentication: "Bearer".
     */
    public static final String BEARER_AUTH_SCHEME = "Bearer";

    @Generated("Bypass coverage for static utility constructor")
    private HttpClientUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Constructs a {@link org.apache.http.Header} for HTTP Basic Authentication using a username and password.
     * <p>
     * The credentials are concatenated with a colon (user:pass) and encoded using
     * Base64 as per RFC 7617 requirements.
     *
     * @param username The plain-text username for authentication.
     * @param password The plain-text password for authentication.
     * @return A Header containing the "Authorization" key and the encoded "Basic ..." value.
     */
    public static @NonNull Header buildBasicAuthenticationHeader(@NonNull final String username, @NonNull final String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        return new BasicHeader(HttpHeaders.AUTHORIZATION, buildBasicAuthenticationValue(username, password));
    }

    /**
     * Constructs a {@link org.apache.http.Header} for HTTP Basic Authentication
     * using a pre-encoded or single-part authentication key.
     *
     * @param authenticationKey The authentication key (e.g., a pre-Base64 encoded string).
     * @return A Header containing the "Authorization" key and the "Basic ..." value.
     */
    public static @NonNull Header buildBasicAuthenticationHeader(@NonNull final String authenticationKey) {
        Objects.requireNonNull(authenticationKey);

        return new BasicHeader(HttpHeaders.AUTHORIZATION, buildBasicAuthenticationValue(authenticationKey));
    }

    /**
     * Constructs a {@link org.apache.http.Header} for Bearer Token Authentication.
     * <p>
     * Typically used for OAuth 2.0 or JWT (JSON Web Token) based API integrations.
     *
     * @param bearerToken The security token to be transmitted.
     * @return A Header object containing the "Authorization" key and the "Bearer ..." value.
     */
    public static @NonNull Header buildBearerAuthenticationHeader(@NonNull final String bearerToken) {
        Objects.requireNonNull(bearerToken);

        return new BasicHeader(HttpHeaders.AUTHORIZATION, buildBearerTokenValue(bearerToken));
    }

    /**
     * Internal helper to format and Base64-encode username and password credentials.
     *
     * @param username The plain-text username.
     * @param password The plain-text password.
     * @return The formatted authentication string (e.g., "Basic dXNlcjpwYXNz").
     */
    private static @NonNull String buildBasicAuthenticationValue(@NonNull final String username, @NonNull final String password) {
        final String auth = username + ":" + password;
        final Charset charset = StandardCharsets.UTF_8;
        final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(charset));

        return buildBasicAuthenticationValue(new String(encodedAuth, charset));
    }

    /**
     * Internal helper to append the Basic Auth scheme prefix to a key.
     *
     * @param authenticationKey The key to append.
     * @return The formatted string (e.g., "Basic [key]").
     */
    private static @NonNull String buildBasicAuthenticationValue(@NonNull final String authenticationKey) {
        return BASIC_AUTH_SCHEME + StringUtils.SPACE + authenticationKey;
    }

    /**
     * Internal helper to append the Bearer Auth scheme prefix to a token.
     *
     * @param bearerToken The token to append.
     * @return The formatted string (e.g., "Bearer [token]").
     */
    private static @NonNull String buildBearerTokenValue(@NonNull final String bearerToken) {
        return BEARER_AUTH_SCHEME + StringUtils.SPACE + bearerToken;
    }

}
