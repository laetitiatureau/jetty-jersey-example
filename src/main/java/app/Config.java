package app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
    private static final Logger logger = Logger.getGlobal();

    public static final String WEBCACHE = "webcache";
    public static final String PAGES = "pages";
    public static final String HTTP_URI = "http.uri";
    public static final String HTTP_PORT = "http.port";
    public static final String WORKDIR = "workdir";
    public static final String WEBROOT = "webroot";

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
                throw new ConfigurationException("Config setting for 'webroot' is invalid - can access directory " + webRoot);
            }
            cfg.put(WEBROOT, webRoot);
        }

        String webCache = props.getProperty(WEBCACHE);
        if (webCache != null) {
            cfg.put(WEBCACHE, Boolean.parseBoolean(webCache));
        } else {
            cfg.put(WEBCACHE, true);
        }

        for (Map.Entry<String, Object> entry : cfg.entrySet()) {
            logger.info("config: " + entry.getKey() + "=" + entry.getValue());
        }

        return cfg;
    }
}
