package app.service;

import app.data.User;
import app.data.UserDirectory;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class FileUserService implements UserService {
    private static final Logger log = Logger.getGlobal();

    private static final Set<String> DEFAULT_ROLES = Collections.unmodifiableSet(Collections.singleton("user"));

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private final UserDirectory userDirectory;
    private final ObjectReader reader;
    private final ObjectWriter writer;

    private final File userFile;

    public FileUserService(final File configDirectory) {
        if (configDirectory == null) {
            throw new IllegalArgumentException("config dir invalid");
        }

        ObjectMapper mapper = new ObjectMapper();
        this.reader = mapper.readerFor(UserDirectory.class);
        this.writer = mapper.writerWithDefaultPrettyPrinter().forType(UserDirectory.class);

        this.userFile = new File(configDirectory, "users.json");

        if (userFile.exists()) {
            this.userDirectory = loadUsers();
        } else {
            this.userDirectory = new UserDirectory();
            String adminPw = UUID.randomUUID().toString();

            addOrModifyUser("admin", adminPw, Collections.singleton("admin"));
            log.info("No user directory file found. Creating a new one.");
            log.info("Created admin user 'admin' with password " + adminPw);
            log.info("Please change the password.");

            // TODO test remove me
            addOrModifyUser("joe@example.com", "password1", null);
        }
    }

    @Override
    public User authenticate(final String username, final String password) throws UnauthorizedException {
        try {
            lock.readLock().lock();

            User user = userDirectory.getUser(username);

            if (user == null) {
                throw new UnauthorizedException();
            }

            if (!BCrypt.checkpw(password, userDirectory.getPassword(username))) {
                throw new UnauthorizedException();
            }

            return user;

        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public User getUser(final String username) {
        try {
            lock.readLock().lock();

            User user = userDirectory.getUser(username);

            if (user == null) {
                throw new EntityNotFoundException();
            }

            return user;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public User addOrModifyUser(final String username, final String password, final Collection<String> roles) {
        try {
            lock.writeLock().lock();

            User existingUser = userDirectory.getUser(username);

            Set<String> defaultRoles = existingUser != null ? existingUser.getRoles() : DEFAULT_ROLES;

            User user = new User(username, roles != null ? new HashSet<>(roles) : defaultRoles);

            userDirectory.addUser(user, BCrypt.hashpw(password, BCrypt.gensalt()));

            writeUsers();

            return user;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void removeUser(final String userName) {
        try {
            lock.writeLock().lock();

            if (userDirectory.removeUser(userName)) {
                writeUsers();
            }

        } finally {
            lock.writeLock().unlock();
        }
    }

    private UserDirectory loadUsers() {
        try {
            return reader.readValue(userFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeUsers() {
        try {
            writer.writeValue(userFile, userDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}