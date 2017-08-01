package app.exception;

public class ConfigurationException extends RuntimeException {
    public ConfigurationException(String m) {
        super(m);
    }
    public ConfigurationException(String m, Throwable cause) {
        super(m, cause);
    }
}
