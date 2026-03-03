## Example usage

* [Implementation](#implementation)
    * [ErrorCode](#errorcode)
    * [SystemException](#systemexception)
* [Usage](#usage)

### Implementation

#### ErrorCode

Custom error code definition for specialized error

```java

import com.merkle.oss.aem.utils.exceptions.ErrorCode;

public enum PermissionErrorCode implements ErrorCode {

    PATH_NOT_FOUND,
    ACCESS_DENIED,
    REPOSITORY

}


```

#### SystemException

Custom exception definition for specialized error

```java

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import com.merkle.oss.aem.utils.exceptions.ErrorCode;
import com.merkle.oss.aem.utils.exceptions.SystemException;

public class PermissionException extends SystemException {

    @Serial
    private static final long serialVersionUID = 2190005989481725181L;

    public PermissionException(@NonNull final ErrorCode errorCode, @NonNull final String message, @NonNull final Object... args) {
        super(errorCode, message, args);
    }

}


```

### Usage

```java

public void setPolicy(final String resourcePath, final AccessControlPolicy policy) {
    try {
        //throws AccessDeniedException.class
        accessControlManager.setPolicy(resourcePath, policy);
    } catch (AccessDeniedException e) {
        throw new PermissionException(PermissionErrorCode.ACCESS_DENIED, "Access denied to set policy {} for resource path {}", policy, resourcePath, e);
    }
}


```