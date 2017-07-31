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

public class DummyUserService implements UserService {
    private static final Set<String> DEFAULT_ROLES = Collections.unmodifiableSet(Collections.singleton("user"));

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private final UserDirectory userDirectory;
    private final ObjectReader reader;
    private final ObjectWriter writer;

    private final File userFile;

    public DummyUserService(final File configDirectory) {
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
            addUser("joe@example.com", "password1", null);
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
    public User getUser(final String username) throws EntityNotFoundException {
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
    public User addUser(final String username, final String password, final Collection<String> roles) {
        try {
            lock.writeLock().lock();

            if (userDirectory.getUser(username) != null) {
                throw new IllegalArgumentException("User already exists");
            }

            User user = new User(username, roles != null ? new HashSet<>(roles) : DEFAULT_ROLES);

            userDirectory.addUser(user, BCrypt.hashpw(password, BCrypt.gensalt()));
            writeUsers();

            return user;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public User updateUser(final String username, final String password, final Collection<String> roles) throws EntityNotFoundException {
        try {
            lock.writeLock().lock();

            User currentUser = userDirectory.getUser(username);

            if (currentUser == null) {
                throw new EntityNotFoundException();
            }

            User user = new User(username, roles != null ? new HashSet<>(roles) : currentUser.getRoles());

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
