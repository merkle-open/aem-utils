package com.merkle.oss.aem.utils.services.httpclient;

import com.merkle.oss.aem.utils.exceptions.ErrorCode;
import com.merkle.oss.aem.utils.exceptions.SystemException;
import org.jspecify.annotations.NonNull;

import java.io.Serial;

/**
 * Specialized {@code RuntimeException} thrown when an http client service error occurs.
 *
 * @see SystemException
 */
public class HttpClientServiceException extends SystemException {

    @Serial
    private static final long serialVersionUID = 2290005989481725181L;

    /**
     * Constructs a new HttpClientServiceException with a parameterized message and an error code.
     *
     * @param errorCode the specific error code representing the permission failure
     * @param message   the error message pattern containing {@code {}} placeholders
     * @param args      the arguments to be substituted into the message placeholders
     */
    public HttpClientServiceException(@NonNull final ErrorCode errorCode, @NonNull final String message, @NonNull final Object... args) {
        super(errorCode, message, args);
    }

}
