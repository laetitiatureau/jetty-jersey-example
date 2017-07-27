package app.service;

import app.data.Page;
import app.data.PageList;
import app.exception.PageServiceException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class DefaultPageService implements PageService {
    private static final Logger logger = Logger.getGlobal();

    private final Set<String> allPageNames;
    private final Map<String, ReentrantReadWriteLock> fileLocks;
    private final File workdir;

    public DefaultPageService(final Configuration config) {
        this(loadPagesFromConfig(config), lookupConfigValue(config, "workdir"));
    }

    public DefaultPageService(final Collection<String> pageNames, final String workdir) {
        File dir = new File(workdir);
        if (!dir.exists() || !dir.canWrite()) {
            throw new PageServiceException("Can't access workdir: " + workdir);
        }
        this.workdir = dir;

        Set<String> s = new LinkedHashSet<>();
        Map<String, ReentrantReadWriteLock> l = new HashMap<>();
        for (String pageName : pageNames) {
            s.add(pageName);
            l.put(pageName, new ReentrantReadWriteLock());
        }
        this.allPageNames = Collections.unmodifiableSet(s);
        this.fileLocks = Collections.unmodifiableMap(l);
    }

    @Override
    public Page getPage(final String pageName) {
        if (isInvalidPageName(pageName)) {
            throw new NotFoundException();
        }

        ReentrantReadWriteLock lock = fileLocks.get(pageName);
        try {
            lock.readLock().lock();
            return new Page(pageName, pageFileExists(pageName));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public PageList getPageList() {
        List<Page> pages = new LinkedList<>();
        for (String pageName : allPageNames) {
            pages.add(getPage(pageName));
        }
        PageList pageList = new PageList();
        pageList.setPages(pages);
        return pageList;
    }

    @Override
    public Page activatePage(final String pageName) {
        if (isInvalidPageName(pageName)) {
            throw new NotFoundException();
        }

        ReentrantReadWriteLock lock = fileLocks.get(pageName);
        try {
            lock.writeLock().lock();
            createPageFile(pageName);
            logger.info("Activated page " + pageName);
            return new Page(pageName, true);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Page deactivatePage(final String pageName) {
        if (isInvalidPageName(pageName)) {
            throw new NotFoundException();
        }

        ReentrantReadWriteLock lock = fileLocks.get(pageName);
        try {
            lock.writeLock().lock();
            removePageFile(pageName);
            logger.info("Deactivated page " + pageName);
            return new Page(pageName, false);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean pageFileExists(final String pageName) {
        File pageFile = new File(workdir, pageName);
        return pageFile.exists();
    }

    /**
     * Creates a new page file relative to inside of workdir.
     *
     * @param pageName the name of the new file
     */
    private void createPageFile(final String pageName) {
        File pageFile = new File(workdir, pageName);
        try {
            boolean status = pageFile.createNewFile();
            if (!status) {
                // file already exists
            }
        } catch (IOException e) {
            throw new PageServiceException("Could not create file " + pageFile.toString(), e);
        }
    }

    /**
     * Deletes a file from workdir.
     *
     * @param pageName name of the file to delete
     */
    private void removePageFile(final String pageName) {
        File pageFile = new File(workdir, pageName);
        if (!pageFile.exists()) {
            return;
        }

        boolean deleted = pageFile.delete();
        if (!deleted) {
            throw new PageServiceException("Could not delete file " + pageFile.toString());
        }
    }

    public File getWorkdir() {
        return workdir;
    }

    private boolean isInvalidPageName(final String pageName) {
        return pageName == null || !allPageNames.contains(pageName);
    }

    private static Collection<String> loadPagesFromConfig(final Configuration config) {
        Set<String> s = new LinkedHashSet<>();
        String rawValue = (String) config.getProperty("pages");
        String[] splitValues = rawValue.trim().split(",");
        for (String value : splitValues) {
            s.add(value.trim());
        }
        return s;
    }

    private static String lookupConfigValue(final Configuration config, final String name) {
        return (String) config.getProperty(name);
    }
}
