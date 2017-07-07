package app.page;

public interface PageManager {
    PageState getPageState(String environmentName);

    PageStates getPageStates();

    PageState activatePage(String environmentName);

    PageState deactivatePage(String environmentName);

}
