package app.service;

import app.data.AllPages;
import app.data.Page;

public interface PageService {
    Page getPage(String pageName);

    AllPages getPages();

    Page activatePage(String pageName);

    Page deactivatePage(String pageName);
}
