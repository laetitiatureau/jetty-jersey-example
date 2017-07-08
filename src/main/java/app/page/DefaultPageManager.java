package app.page;

import javax.ws.rs.NotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DefaultPageManager implements PageManager {
    private final Set<String> environments;
    private final File workDir;

    public DefaultPageManager(final File workDir) throws URISyntaxException, IOException {
        this(DefaultPageManager.class.getResource("/environments.txt"), workDir);
    }

    private DefaultPageManager(final URL environmentsFile, final File workDir) throws URISyntaxException, IOException {
        this(Files.readAllLines(Paths.get(environmentsFile.toURI()),
                Charset.defaultCharset()), workDir);
    }

    public DefaultPageManager(final Collection<String> environments, File workDir) throws IOException {
        if (!workDir.isDirectory() || !workDir.canWrite()) {
            throw new IOException("Work directory not accessible");
        }

        Set<String> env = new HashSet<>();
        env.addAll(environments);
        this.environments = Collections.unmodifiableSet(env);
        this.workDir = workDir;
    }

    @Override
    public PageState getPageState(String environmentName) {
        if (!isValidEnvironment(environmentName)) {
            throw new NotFoundException();
        }
        PageState pageState = new PageState();
        pageState.setEnvironmentName(environmentName);

        // TODO return the right state here
        // find the respective 'status' file in 'workdir', read it,
        // figure out what the state is and set the 'active' flag on the
        // 'PageState' object accordingly
        //
        // for now, hardcode to 'inactive'
        pageState.setActive(false);
        return pageState;
    }

    @Override
    public PageStates getPageStates() {
        List<PageState> states = new LinkedList<>();
        for (String environment : environments) {
            states.add(getPageState(environment));
        }
        PageStates pageStates = new PageStates();
        pageStates.setEnvironments(states);
        return pageStates;
    }

    @Override
    public PageState activatePage(String environmentName) {
        if (!isValidEnvironment(environmentName)) {
            throw new NotFoundException();
        }
        // TODO trigger maintenance page activation
        // find the correct file in the workdir and update

        return getPageState(environmentName);
    }

    @Override
    public PageState deactivatePage(String environmentName) {
        if (!isValidEnvironment(environmentName)) {
            throw new NotFoundException();
        }
        // TODO trigger maintenance page deactivation
        // find the correct file in the workdir and update

        return getPageState(environmentName);
    }

    public File getWorkDir() {
        return workDir;
    }

    private boolean isValidEnvironment(String environmentName) {
        return environmentName != null && environments.contains(environmentName.toLowerCase());
    }
}
