package app.service;

import app.data.PageList;
import app.data.Page;

public interface PageService {
    Page getPage(String pageName);

    PageList getPageList();

    Page activatePage(String pageName);

    Page deactivatePage(String pageName);
}
