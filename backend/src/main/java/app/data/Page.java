package app.data;

import java.util.Objects;

public class Page {
    private boolean active;
    private String name;

    public Page() {
        // empty constructor for jackson
    }

    public Page(final String name, final boolean active) {
        this.name = name;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
