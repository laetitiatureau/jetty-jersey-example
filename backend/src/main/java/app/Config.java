package app;

import app.exception.ConfigurationException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;
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

        cfg.put(AUTH, props.getProperty(AUTH, "true"));
        if ("true".equals(cfg.get(AUTH))) {
            SignatureAlgorithm algorithm = SignatureAlgorithm.HS512;
            File confDir = (File) cfg.get(CONFDIR);
            File keyFile = new File(confDir, "jwt.jceks");
            cfg.put(JWT_KEY, loadOrCreateKey(keyFile, "jwt", algorithm));
            cfg.put(JWT_KEY_ALG, algorithm);
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

    /*
     * Load a secret key with the given alias from keyFile. If such a key doesn't exist, generate a new key with
     * the given algorithm and save it to keyFile.
     */
    private static Key loadOrCreateKey(final File keyFile, final String alias, SignatureAlgorithm algorithm) {
        final String keystoreType = "JCEKS";
        final char[] secret = "secret".toCharArray();

        try {
            KeyStore keyStore = KeyStore.getInstance(keystoreType);
            if (keyFile.exists()) {
                try (FileInputStream fis = new FileInputStream(keyFile)) {
                    keyStore.load(fis, secret);
                }
            } else {
                keyStore.load(null, secret);
            }

            if (!keyStore.containsAlias(alias)) {
                Key newKey = MacProvider.generateKey(algorithm);
                keyStore.setKeyEntry(alias, newKey, secret, null);
                try (FileOutputStream fos = new FileOutputStream(keyFile)) {
                    keyStore.store(fos, secret);
                }
                logger.info("Generating new signing key for jwt auth");
                return newKey;
            } else {
                logger.info("Reusing existing signing key for jwt auth");
                return keyStore.getKey(alias, secret);
            }

        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new ConfigurationException("Failed to create or load jwt key", e);
        }

    }
}
