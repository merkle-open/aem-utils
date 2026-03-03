package com.merkle.oss.aem.utils.services.httpclient;

import com.merkle.oss.aem.utils.exceptions.ErrorCode;

public enum HttpClientServiceErrorCode implements ErrorCode {

    NO_SUCH_ALGORITHM,
    KEY_STORE,
    LOGIN,
    KEY_STORE_NOT_INITIALISED,
    KEY_MANAGEMENT,
    UNRECOVERABLE_KEY

}
