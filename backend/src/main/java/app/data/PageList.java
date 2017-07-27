package app.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PageList {
    private List<Page> pages;

    public PageList() {
        // empty constructor for jackson
    }

    public PageList(final Collection<Page> pages) {
        this.pages = new LinkedList<>();
        this.pages.addAll(pages);
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(final List<Page> pages) {
        this.pages = pages;
    }
}
