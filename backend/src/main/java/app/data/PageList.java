package app.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PageList {
    private final List<Page> pages;

    @JsonCreator
    public PageList(@JsonProperty("pages") final Collection<Page> pages) {
        this.pages = new LinkedList<>();
        this.pages.addAll(Collections.unmodifiableList(new LinkedList<>(pages)));
    }

    public List<Page> getPages() {
        return pages;
    }
}
