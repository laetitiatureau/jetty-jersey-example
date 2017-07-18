package app.service;

import app.data.Page;
import app.data.PageList;

public interface PageService {
    Page getPage(String pageName);

    PageList getPageList();

    Page activatePage(String pageName);

    Page deactivatePage(String pageName);
}
