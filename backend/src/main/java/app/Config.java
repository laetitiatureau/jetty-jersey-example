package app;

import app.exception.ConfigurationException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
    private static final Logger logger = Logger.getGlobal();

    public static final String CONFDIR = "confdir";
    public static final String CORS = "http.cors";
    public static final String HTTP_PORT = "http.port";
    public static final String SSL_ENABLED = "ssl.enabled";
    public static final String SSL_KEYSTORE = "ssl.keystore";
    public static final String SSL_KEYPASS = "ssl.keypass";
    public static final String HTTP_URI = "http.uri";
    public static final String JWT_KEY = "jwt.key";
    public static final String JWT_KEY_ALG = "jwt.keyalg";
    public static final String PAGES = "pages";
    public static final String AUTH = "auth";
    public static final String WEBCACHE = "webcache";
    public static final String WEBROOT = "webroot";
    public static final String WORKDIR = "workdir";

    private Config() {
        // static methods only - this class shouldn't be instantiated
    }

    public static Map<String, Object> loadConfig(final Properties props) throws IOException {
        Map<String, Object> cfg = new LinkedHashMap<>();
        cfg.put(HTTP_URI, props.getProperty(HTTP_URI, "http://0.0.0.0"));
        cfg.put(HTTP_PORT, Integer.parseInt(props.getProperty(HTTP_PORT, "8080")));

        String workDir = props.getProperty(WORKDIR);
        if (workDir != null) {
            File workDirFile = new File(workDir);
            if (!workDirFile.exists() || !workDirFile.canRead() || !workDirFile.isDirectory()) {
                throw new ConfigurationException("Config setting for 'workdir' invalid - can't access " + workDir);
            }
            cfg.put(WORKDIR, workDir);
        } else {
            File tmpWorkDir = Files.createTempDirectory(null).toFile();
            tmpWorkDir.deleteOnExit();
            cfg.put(WORKDIR, tmpWorkDir.toString());
            logger.warning("Config setting for 'workdir' not defined - " +
                    "using temporary directory. Files will be deleted on shutdown.");
        }

        if (props.getProperty(PAGES) == null) {
            throw new ConfigurationException("Config setting for 'pages' (comma-separated list of valid pagenames) not defined");
        }
        cfg.put(PAGES, props.getProperty(PAGES));

        String webRoot = props.getProperty(WEBROOT);
        if (webRoot != null) {
            File webRootDir = new File(webRoot);
            if (!webRootDir.exists() || !webRootDir.canRead()) {
                throw new ConfigurationException("Config setting for 'webroot' is invalid - can't access directory " + webRoot);
            }
            cfg.put(WEBROOT, webRoot);
        }

        String webCache = props.getProperty(WEBCACHE);
        if (webCache != null) {
            cfg.put(WEBCACHE, Boolean.parseBoolean(webCache));
        } else {
            cfg.put(WEBCACHE, true);
        }

        cfg.put(CORS, props.getProperty(CORS, "false"));

        String secure = props.getProperty(AUTH, "false");
        if ("true".equals(secure)) {
            SignatureAlgorithm algorithm = SignatureAlgorithm.HS512;
            cfg.put(JWT_KEY, MacProvider.generateKey(algorithm));
            cfg.put(JWT_KEY_ALG, algorithm);
        }

        cfg.put(AUTH, props.getProperty(AUTH, "false"));

        if (props.getProperty(CONFDIR) != null) {
            String confDirString = props.getProperty(CONFDIR);
            File confDir = new File(confDirString);
            if (confDir.isDirectory() && confDir.canRead() && confDir.canWrite()) {
                cfg.put(CONFDIR, confDir);
            } else {
                throw new ConfigurationException("Config setting for 'confdir' is invalid - can't access directory" + confDirString);
            }
        } else {
            File tmpConfDir = Files.createTempDirectory(null).toFile();
            tmpConfDir.deleteOnExit();

            cfg.put(CONFDIR, tmpConfDir);
            logger.warning("Config setting for 'confdir' not defined - " +
                    "using temporary directory. Files will be deleted on shutdown.");
        }

        cfg.put(SSL_ENABLED, props.getProperty(SSL_ENABLED, "false"));
        if ("true".equals(cfg.get(SSL_ENABLED))) {
            String keystoreFile = props.getProperty(SSL_KEYSTORE);

            if (keystoreFile != null && !(new File(keystoreFile).exists())) {
                throw new ConfigurationException("Config setting for 'ssl.keystore' is invalid - can't access file");
            }

            cfg.put(SSL_KEYSTORE, keystoreFile);
            cfg.put(SSL_KEYPASS, props.getProperty(SSL_KEYPASS));
        }

        for (Map.Entry<String, Object> entry : cfg.entrySet()) {
            logger.info("config: " + entry.getKey() + "=" + entry.getValue());
        }

        return cfg;
    }
}
