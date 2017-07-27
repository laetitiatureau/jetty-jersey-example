package app.data;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String name;
    private Set<String> roles;

    public User() {
        // empty constructor for jackson
    }

    public User(String name, Set<String> roles) {
        this.name = name;
        this.roles = new HashSet<>(roles);
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

}
