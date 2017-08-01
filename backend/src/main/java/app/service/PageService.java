package app.service;

import app.data.Page;
import app.data.PageList;

public interface PageService {
    Page getPage(String pageName);

    PageList getPageList();

    boolean activatePage(String pageName);

    boolean deactivatePage(String pageName);
}
