package app.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AllPages {
    private List<Page> pages;
    public AllPages() {

    }

    public AllPages(final Collection<Page> pages) {
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
