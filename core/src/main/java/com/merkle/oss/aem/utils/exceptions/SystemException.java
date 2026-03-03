package com.merkle.oss.aem.utils.exceptions;

import org.jspecify.annotations.NonNull;
import org.slf4j.helpers.MessageFormatter;

import java.io.Serial;

/**
 * Represents a generic system-level {@code RuntimeException} exception within the AEM utility framework.
 * Serves as a base error class for any specialized error handler.
 * Allows for quick custom error instantiation at runtime level
 * to dismiss try catch clutters during functional programming.
 * <p>
 * This exception supports SLF4J-style parameterized messages using {@code {}} anchors.
 *
 * @apiNote Example usage:
 * {@snippet :
 * throw new SystemException(ErrorCode.IO_ERROR,"Failed to process resource {}",  resourcePath, e);
 *}
 */
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4090005989481725181L;

    private final ErrorCode errorCode;

    /**
     * Constructs a new SystemException with a parameterized message and an error code.
     *
     * @param errorCode the functional error code associated with this exception
     * @param message   the error message pattern containing {@code {}} placeholders
     * @param args      the arguments to be substituted into the message placeholders
     */
    public SystemException(@NonNull final ErrorCode errorCode, @NonNull final String message, @NonNull final Object... args) {
        super(formatMessage(errorCode, message, args), MessageFormatter.arrayFormat(message, args).getThrowable());
        this.errorCode = errorCode;
    }

    private static String formatMessage(@NonNull final ErrorCode errorCode, @NonNull final String message, @NonNull final Object[] args) {
        return String.format("[%s] %s", errorCode, MessageFormatter.arrayFormat(message, args).getMessage());
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the {@link ErrorCode}
     */
    public @NonNull ErrorCode getErrorCode() {
        return errorCode;
    }

}
