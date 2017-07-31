package app.service;

import app.data.User;
import app.exception.EntityNotFoundException;
import app.exception.UnauthorizedException;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    User authenticate(String userName, String password) throws UnauthorizedException;

    User getUser(String userName) throws EntityNotFoundException;

    User addOrModifyUser(String userName, String password, Collection<String> roles);

    void removeUser(String userName);
}
