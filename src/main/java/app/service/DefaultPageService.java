package app.service;

import app.data.Page;
import app.data.PageList;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class DefaultPageService implements PageService {
    private final Set<String> allPageNames;
    private final File workdir;

    public DefaultPageService(final Configuration config) throws URISyntaxException, IOException {
        this(loadPagesFromConfig(config), lookupConfigValue(config, "workdir"));
    }

    public DefaultPageService(final Collection<String> pageNames, final String workdir) throws IOException {
        File dir = new File(workdir);
        if (!dir.exists() || !dir.canWrite()) {
            throw new IOException("Can't access workdir: " + workdir);
        }
        this.workdir = dir;

        Set<String> s = new HashSet<>();
        s.addAll(pageNames);
        this.allPageNames = Collections.unmodifiableSet(s);
    }

    @Override
    public Page getPage(final String pageName) {
        if (!isValidPageName(pageName)) {
            throw new NotFoundException();
        }

        // TODO acquire lock for this page
        Page page = new Page(pageName, readPageFile(pageName));
        // TODO free lock
        return page;
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
        if (!isValidPageName(pageName)) {
            throw new NotFoundException();
        }
        // TODO acquire lock for page
        writePageFile(pageName, true);
        Page page = new Page(pageName, true);
        // TODO free lock
        return page;
    }

    @Override
    public Page deactivatePage(final String pageName) {
        if (!isValidPageName(pageName)) {
            throw new NotFoundException();
        }
        // TODO acquire lock for page
        writePageFile(pageName, false);
        Page page = new Page(pageName, false);
        // TODO return lock for page
        return page;
    }

    private boolean readPageFile(final String pageName) {
        // TODO implement this
        // - Find respective file for this page in 'workdir'
        // - Read the file and set correct 'active' state on page
        // - Return 'active' state
        return false;
    }

    private void writePageFile(final String pageName, final boolean active) {
        // TODO implement this
        throw new RuntimeException("Not implemented yet");
    }

    private boolean isValidPageName(final String pageName) {
        return pageName != null && allPageNames.contains(pageName.toLowerCase());
    }

    private static Collection<String> loadPagesFromConfig(final Configuration config) {
        Set<String> s = new HashSet<>();
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
