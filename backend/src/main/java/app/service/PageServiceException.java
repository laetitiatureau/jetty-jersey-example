package app.service;


public class PageServiceException extends RuntimeException {
    public PageServiceException(String msg) {
        super(msg);
    }

    public PageServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
