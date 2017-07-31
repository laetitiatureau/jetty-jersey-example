package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private final String name;
    private final Set<String> roles;

    @JsonCreator
    public User(@JsonProperty("name") String name, @JsonProperty("roles") Set<String> roles) {
        this.name = name;
        this.roles = new HashSet<>(roles);
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return roles;
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
