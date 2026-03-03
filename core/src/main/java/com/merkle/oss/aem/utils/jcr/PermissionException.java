package com.merkle.oss.aem.utils.jcr;

import com.merkle.oss.aem.utils.exceptions.ErrorCode;
import com.merkle.oss.aem.utils.exceptions.SystemException;
import org.jspecify.annotations.NonNull;

import java.io.Serial;

/**
 * Specialized {@code RuntimeException} thrown when a permission violation occurs.
 *
 * @see SystemException
 */
public class PermissionException extends SystemException {

    @Serial
    private static final long serialVersionUID = 3190005989481725181L;

    /**
     * Constructs a new PermissionException with a parameterized message and an error code.
     *
     * @param errorCode the specific error code representing the permission failure
     * @param message   the error message pattern containing {@code {}} placeholders
     * @param args      the arguments to be substituted into the message placeholders
     */
    public PermissionException(@NonNull final ErrorCode errorCode, @NonNull final String message, @NonNull final Object... args) {
        super(errorCode, message, args);
    }

}
