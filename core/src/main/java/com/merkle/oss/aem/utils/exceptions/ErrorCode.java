package com.merkle.oss.aem.utils.exceptions;

import java.io.Serializable;

/**
 * A marker interface for defining unique, domain-specific error codes across the AEM utility framework.
 * <p>
 * Error codes allow callers to programmatically
 * identify the root cause of a failure (e.g., {@code REPOSITORY_ERROR} vs {@code LOGIN_FAILURE})
 * without relying on brittle string matching of exception messages.
 *
 * @implNote It is recommended that implementations (usually Enums) provide a meaningful
 * name via {@link Object#toString()} or {@link Enum#name()} to be used in
 * {@link SystemException} message formatting.
 * {@snippet :
 *  public enum PermissionErrorCode implements ErrorCode {
 *  PATH_NOT_FOUND,
 *  ACCESS_DENIED,
 *  REPOSITORY
 *  }
 *}
 * @see SystemException
 */
public interface ErrorCode extends Serializable {
}
