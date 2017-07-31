package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class UserDirectory {
    @JsonProperty("users")
    private Map<String, User> users;
    @JsonProperty("passwords")
    private Map<String, String> passwords;

    public UserDirectory() {
        this.users = new HashMap<>();
        this.passwords = new HashMap<>();
    }

    @JsonCreator
    public UserDirectory(@JsonProperty("users") Map<String, User> users, @JsonProperty("passwords") Map<String, String> passwords) {
        this();

        this.users.putAll(users);
        this.passwords.putAll(passwords);
    }

    public User getUser(String user) {
        return users.get(user);
    }

    public String getPassword(String user) {
        return passwords.get(user);
    }

    public void addUser(User user, String password) {
        if (user.getName() == null) {
            throw new IllegalArgumentException();
        }

        users.put(user.getName(), user);
        passwords.put(user.getName(), password);
    }

    public boolean removeUser(String username) {
        User user = users.remove(username);
        passwords.remove(username);

        return user != null;
    }
}
