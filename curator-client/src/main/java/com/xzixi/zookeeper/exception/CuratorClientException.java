package com.xzixi.zookeeper.exception;

public class CuratorClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CuratorClientException() {
    }

    public CuratorClientException(String message) {
        super(message);
    }

    public CuratorClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CuratorClientException(Throwable cause) {
        super(cause);
    }

    public CuratorClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
