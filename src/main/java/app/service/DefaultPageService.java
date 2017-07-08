package app.service;

import app.data.AllPages;
import app.data.Page;

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
        Page page = new Page();
        page.setName(pageName);

        // TODO return the right state here
        // find the respective 'status' file in 'workdir', read it,
        // figure out what the state is and set the 'active' flag on the
        // 'Page' object accordingly
        //
        // for now, hardcode to 'inactive'
        page.setActive(false);
        return page;
    }

    @Override
    public AllPages getPages() {
        List<Page> states = new LinkedList<>();
        for (String pageName : allPageNames) {
            states.add(getPage(pageName));
        }
        AllPages allPages = new AllPages();
        allPages.setPages(states);
        return allPages;
    }

    @Override
    public Page activatePage(final String pageName) {
        if (!isValidPageName(pageName)) {
            throw new NotFoundException();
        }
        // TODO trigger service activation
        // find the correct file in the workdir and update

        // return getPage(pageName);
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Page deactivatePage(final String pageName) {
        if (!isValidPageName(pageName)) {
            throw new NotFoundException();
        }
        // TODO trigger service deactivation
        // find the correct file in the workdir and update

        // return getPage(pageName);
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

    private static String lookupConfigValue(final Configuration config, String name) {
        return (String) config.getProperty(name);
    }
}
