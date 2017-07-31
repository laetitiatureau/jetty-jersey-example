package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Page {
    private final boolean active;
    private final String name;

    @JsonCreator
    public Page(@JsonProperty("name") final String name, @JsonProperty("active") final boolean active) {
        this.name = name;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (!(other instanceof Page)) {
            return false;
        } else {
            Page otherPage = (Page) other;
            return active == otherPage.active && Objects.equals(name, otherPage.name);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, name);
    }
}
