package app.service;

import app.data.User;

import java.util.Collection;

public interface UserService {
    User authenticate(String userName, String password);

    User getUser(String userName);

    User addOrModifyUser(String userName, String password, Collection<String> roles);

    void removeUser(String userName);
}
