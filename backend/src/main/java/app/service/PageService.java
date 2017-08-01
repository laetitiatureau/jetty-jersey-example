package app.service;

import app.data.Page;
import app.data.PageList;
import app.exception.EntityNotFoundException;

public interface PageService {
    Page getPage(String pageName) throws EntityNotFoundException;

    PageList getPageList();

    boolean activatePage(String pageName) throws EntityNotFoundException;

    boolean deactivatePage(String pageName) throws EntityNotFoundException;
}
