package app.data;

import java.util.HashSet;
import java.util.Objects;
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

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof User)) {
            return false;
        } else {
            User otherUser = (User) other;
            return Objects.equals(name, otherUser.name) && Objects.equals(roles, otherUser.roles);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roles);
    }
}
