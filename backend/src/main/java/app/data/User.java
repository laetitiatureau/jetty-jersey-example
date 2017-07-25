package app.data;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String name;
    private Set<String> roles;
    private int version;

    public User() {
        // empty constructor for marshalling
    }

    public User(String name, Set<String> roles, int version) {
        this.name = name;
        this.roles = new HashSet<>(roles);
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
